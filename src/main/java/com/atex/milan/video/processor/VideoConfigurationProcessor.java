package com.atex.milan.video.processor;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.atex.milan.video.processor.util.NameProperties;
import com.atex.milan.video.processor.util.NamePropertiesBuilder;

/**
 * setup configuration for video conversion.
 *
 * @author mnova
 */
public class VideoConfigurationProcessor extends BaseVideoProcessor {
    static final Logger logger = Logger.getLogger(VideoConfigurationProcessor.class.getName());

    public static final String OUTPUTDIR_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoOutputDir";
    public static final String VIDEOID_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoId";
    public static final String SITECODE_HEADER = VideoConfigurationProcessor.class.getName() + ".SiteCode";
    public static final String VIDEODATE_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoDate";
    public static final String VIDEONAME_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoName";
    public static final String TIMESTAMP_HEADER = VideoConfigurationProcessor.class.getName() + ".Timestamp";
    public static final String ORIG_VIDEO_INFO_HEADER = VideoConfigurationProcessor.class.getName() + ".OrigVideoInfo";
    public static final String MEDIA_HEADER = VideoConfigurationProcessor.class.getName() + ".Media";

    @Override
    public void process(final Exchange exchange) throws Exception {
        logger.fine(this.getClass().getSimpleName() + " - start work");

        try {
            final Message originalMessage = exchange.getIn();

            final Map<String, Object> headers = originalMessage.getHeaders();

            final String name = (String) headers.get("CamelFileName");
            final String path = (String) headers.get("CamelFileAbsolutePath");

            final File filePath = new File(path);

            final String videoFilename = getVideoFileName(filePath);

            final NameProperties nameProperties = new NamePropertiesBuilder(getServiceProperties())
                    .setFileName(videoFilename)
                    .build();

            logger.info("name properties: " + nameProperties.toString());

            if (nameProperties.getPublishDate() != null) {
                headers.put(VIDEODATE_HEADER, nameProperties.getPublishDate());
            }
            if (nameProperties.getTitle() != null) {
                headers.put(VIDEONAME_HEADER, nameProperties.getTitle());
            }

            final String videoId = getVideoId(filePath);

            final Date d = new Date();
            final File outDir = getOutDirWithTimestamp(d);

            headers.put(OUTPUTDIR_HEADER, outDir);
            headers.put(VIDEOID_HEADER, videoId);
            headers.put(TIMESTAMP_HEADER, Long.toString(d.getTime()));

            detectSiteCode(headers, filePath);
        } finally {

            logger.fine(this.getClass().getSimpleName() + " - end work");

        }
    }

    protected String getVideoId(final File filePath) {
        return filePath.getParentFile().getName();
    }

    protected String getVideoFileName(final File filePath) {
        return filePath.getName();
    }

    protected void detectSiteCode(final Map<String, Object> headers, final File filePath) {
        final String siteCode = filePath.getParentFile().getParentFile().getName();
        if (!siteCode.equals("process")) {
            headers.put(SITECODE_HEADER, siteCode);
        }
    }

}
