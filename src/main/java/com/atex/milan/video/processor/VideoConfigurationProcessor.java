package com.atex.milan.video.processor;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * setup configuration for video conversion.
 *
 * @author mnova
 */
public class VideoConfigurationProcessor extends BaseVideoProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoConfigurationProcessor.class);

  public static final String OUTPUTDIR_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoOutputDir";
  public static final String VIDEOID_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoId";
  public static final String VIDEOPATH_HEADER = VideoConfigurationProcessor.class.getName() + ".VideoPath";
  public static final String THUMBPATH_HEADER = VideoConfigurationProcessor.class.getName() + ".ThumbPath";

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message originalMessage = exchange.getIn();

    final Map<String, Object> headers = originalMessage.getHeaders();

    final String name = (String) headers.get("CamelFileName");
    final String path = (String) headers.get("CamelFileAbsolutePath");

    final String videoId = UUID.randomUUID().toString();

    final long time = new Date().getTime();

    final File outDir = new File(new File(getOutDir(), videoId), Long.toString(time));
    outDir.mkdirs();

    headers.put(OUTPUTDIR_HEADER, outDir);
    headers.put(VIDEOID_HEADER, videoId);
  }
}
