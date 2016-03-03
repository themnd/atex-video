package com.atex.milan.video.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atex.milan.video.processor.VideoConfigurationProcessor;
import com.atex.milan.video.processor.VideoUploadConfigurationProcessor;
import com.atex.milan.video.util.InjectorUtils;
import com.atex.milan.video.util.ServiceProperties;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.polopoly.util.StringUtil;

/**
 * UploadServlet
 * 13/04/15 on 13:07
 *
 * @author mnova
 */
public class UploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UploadServlet.class.getName());

    public static final String CONTENT_ID_PARAM = "contentId";
    public static final String VIDEO_UUID_PARAM = "videoUUID";

    /**
     * Cleans files after upload
     */
    private transient FileCleaningTracker fileCleaningTracker;

    private static final int FOUR_KB = 4196;

    @Inject
    private ServiceProperties serviceProperties;

    /**
     * File repository
     */
    private File repository;

    private ClassPathXmlApplicationContext springContext;
    private CamelContext camelContext;

    private ProducerTemplate template;

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);

        final ServletContext servletContext = config.getServletContext();

        InjectorUtils.getInstance().injectMembers(this);

        repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(servletContext);

        springContext = new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
        camelContext = (CamelContext) springContext.getBean("camel");

        template = camelContext.createProducerTemplate();
        try {
            template.start();
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Cannot start producer: " + e.getMessage(), e);
        }

        LOGGER.info("UploadServlet initialized.");
    }

    @Override
    public void destroy() {

        if (template != null) {
            try {
                template.stop();
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Cannot stop producer: " + e.getMessage(), e);
            }
        }

        super.destroy();
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart) {
            doHandleUpload(req, resp);
        } else {
            throw new IllegalArgumentException("No files to save");
        }
    }

    private void doHandleUpload(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {

        try {
            // Create a factory for disk-based file items
            final FileItemFactory factory = getFileItemFactory();

            // Create a new file upload handler
            final ServletFileUpload upload = new ServletFileUpload(factory);

            LOGGER.info("processing files upload");

            final List<FileItem> items = upload.parseRequest(req);
            LOGGER.info("items: " + items);

            String contentId = null;
            String videoUUID = null;
            byte[] content = null;
            String fileName = null;

            for (final FileItem item : items) {

                if (item.isFormField()) {
                    final String name = item.getFieldName();
                    final String value = item.getString();

                    LOGGER.info("name: " + name);
                    LOGGER.info("value: " + value);

                    if (StringUtil.equalsIgnoreCase(name, CONTENT_ID_PARAM)) {
                        contentId = value;
                    } else if (StringUtil.equalsIgnoreCase(name, VIDEO_UUID_PARAM)) {
                        videoUUID = value;
                    }
                } else {

                    if (content != null) {
                        throw new ServletException("only one file per upload");
                    }

                    fileName = item.getName();
                    content = item.get();

                    LOGGER.info("filename: " + fileName);
                }

            }

            if (contentId == null || content == null || videoUUID == null) {
                throw new ServletException("missing parameter");
            }

            final Map<String, Object> headers = Maps.newHashMap();
            headers.put(VideoUploadConfigurationProcessor.ORIGNAME_HEADER, FilenameUtils.getName(fileName));
            headers.put(VideoUploadConfigurationProcessor.CONTENTID_HEADER, contentId);
            headers.put(VideoUploadConfigurationProcessor.FROMUPLOAD_HEADER, true);
            headers.put(VideoConfigurationProcessor.VIDEOID_HEADER, videoUUID);

            final File tempFile = new File(repository, String.format("%s-%s", videoUUID, FilenameUtils.getName(fileName)));
            FileUtils.writeByteArrayToFile(tempFile, content);

            template.send("seda:audioUploadProcess", ExchangePattern.InOnly, new Processor() {
                @Override
                public void process(final Exchange exchange) throws Exception {
                    exchange.getIn().setHeaders(headers);
                    GenericFile<File> f = new GenericFile<>();
                    f.setFile(tempFile);
                    f.changeFileName(tempFile.getAbsolutePath());
                    f.bindToExchange(exchange);
                    exchange.getIn().setBody(f);
                }
            });

        } catch (FileUploadException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        }

    }


    /**
     * @return a {@link DiskFileItemFactory} with container default repository and a {@link FileCleaningTracker}
     */
    private FileItemFactory getFileItemFactory()
    {

        final DiskFileItemFactory factory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository);
        factory.setFileCleaningTracker(fileCleaningTracker);
        return factory;
    }

}
