package com.atex.milan.video.processor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
  public static final String TIMESTAMP_HEADER = VideoConfigurationProcessor.class.getName() + ".Timestamp";
  public static final String ORIG_VIDEO_INFO_HEADER = VideoConfigurationProcessor.class.getName() + ".OrigVideoInfo";
  public static final String MEDIA_HEADER = VideoConfigurationProcessor.class.getName() + ".Media";

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message originalMessage = exchange.getIn();

    final Map<String, Object> headers = originalMessage.getHeaders();

    final String name = (String) headers.get("CamelFileName");
    final String path = (String) headers.get("CamelFileAbsolutePath");

    final String videoId = new File(path).getParentFile().getName();

    final Date d = new Date();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH");
    final File outDir = new File(getOutDir(), dateFormat.format(d));
    outDir.mkdirs();

    headers.put(OUTPUTDIR_HEADER, outDir);
    headers.put(VIDEOID_HEADER, videoId);
    headers.put(TIMESTAMP_HEADER, Long.toString(d.getTime()));
  }
}
