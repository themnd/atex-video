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
public class VideoUploadConfigurationProcessor extends VideoConfigurationProcessor
{
  private static final Logger logger = Logger.getLogger(VideoUploadConfigurationProcessor.class.getName());

  public static final String ORIGNAME_HEADER = VideoUploadConfigurationProcessor.class.getName() + ".origName";
  public static final String CONTENTID_HEADER = VideoUploadConfigurationProcessor.class.getName() + ".contentId";
  public static final String FROMUPLOAD_HEADER = VideoUploadConfigurationProcessor.class.getName() + ".fromUpload";

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.fine(this.getClass().getSimpleName() + " - start work");

    try {
      final Message originalMessage = exchange.getIn();

      final Map<String, Object> headers = originalMessage.getHeaders();

      final String name = (String) headers.get("CamelFileName");
      final String path = (String) headers.get("CamelFileAbsolutePath");

      final File filePath = new File(path);

      final String videoFilename = (String) headers.get(ORIGNAME_HEADER);

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

      final String videoId = (String) headers.get(VIDEOID_HEADER);

      final Date d = new Date();
      final File outDir = getOutDirWithTimestamp(d);

      headers.put(OUTPUTDIR_HEADER, outDir);
      headers.put(TIMESTAMP_HEADER, Long.toString(d.getTime()));

    } finally {

      logger.fine(this.getClass().getSimpleName() + " - end work");

    }
  }

}
