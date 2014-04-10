package com.atex.milan.video.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.data.DataType;
import com.atex.milan.video.data.Video;
import com.google.inject.Inject;

/**
 * VideoImportProcessor
 *
 * @author mnova
 */
public class VideoImportProcessor extends BaseGuiceProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoImportProcessor.class);

  @Inject
  private VideoRepository videoRepository;

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message msg = exchange.getIn();

    final String videoUUID = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOID_HEADER);
    final String videoType = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOTYPE_HEADER);
    final String videoPath = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOPATH_HEADER);
    final String thumbPath = (String) getMessageProperty(msg, VideoConfigurationProcessor.THUMBPATH_HEADER);
    
    final Video v = new Video();
    v.setType(DataType.VIDEO);
    v.setUuid(videoUUID);
    v.setVideoType(videoType);
    v.setName(FilenameUtils.getName(videoPath));
    v.setVideoPath(videoPath);
    v.setThumbPath(thumbPath);

    {
      final Video newVideo = videoRepository.addVideo(v);
      if (newVideo == null) {
        throw new Exception("Error saving in database");
      }
    }
  }

}
