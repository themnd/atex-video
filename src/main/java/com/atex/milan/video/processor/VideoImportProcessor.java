package com.atex.milan.video.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.couchbase.DBClient;
import com.atex.milan.video.data.Video;
import com.google.inject.Inject;

/**
 * VideoImportProcessor
 * 09/04/14 on 14:58
 *
 * @author mnova
 */
public class VideoImportProcessor extends BaseGuiceProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoImportProcessor.class);

  @Inject
  private DBClient dbClient;

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message msg = exchange.getIn();

    final String videoId = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOID_HEADER);
    final String videoPath = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOPATH_HEADER);
    final String thumbPath = (String) getMessageProperty(msg, VideoConfigurationProcessor.THUMBPATH_HEADER);
    
    final Video v = new Video();
    v.setId(videoId);
    v.setVideoPath(videoPath);
    v.setThumbPath(thumbPath);

    final boolean exitValue = dbClient.set(videoId, v);
    if (!exitValue) {
      throw new Exception("Error saving in database");
    }
  }

}
