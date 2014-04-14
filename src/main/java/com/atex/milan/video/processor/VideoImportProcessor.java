package com.atex.milan.video.processor;

import java.util.Date;
import java.util.Map;

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
public class VideoImportProcessor extends BaseVideoProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoImportProcessor.class);

  @Inject
  private VideoRepository videoRepository;
  
  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    try {
      final Message msg = exchange.getIn();

      final Map<String, Object> headers = msg.getHeaders();

      final String name = (String) headers.get("CamelFileNameOnly");

      final String videoUUID = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOID_HEADER);

      final long ts = new Date().getTime();
      final long processTime = (ts - Long.parseLong(getTimestamp(msg)));
      
      Video v = videoRepository.getVideoByUUID(videoUUID);
      final boolean isNew = (v == null);
      if (v == null) {
        v = new Video();
        v.setType(DataType.VIDEO);
        v.setUuid(videoUUID);
        v.setCreated(ts);

        logger.info("Creating video with uuid: {}", videoUUID);
      } else {
        logger.info("Updating video with uuid: {}", videoUUID);
      }

      v.setName(FilenameUtils.getName(name));
      v.setModified(ts);
      v.setProcessTime(processTime);
      v.setVideoInfo(getVideoInfoOrig(msg));
      v.setMedia(getMedia(msg));

      if (isNew) {
        final Video newVideo = videoRepository.addVideo(v);
        if (newVideo == null) {
          throw new Exception("Error saving in database");
        }
      } else {
        if (!videoRepository.setVideo(v)) {
          throw new Exception("Error saving in database");
        }
      }
    } finally {
      logger.trace("{} - end work", this.getClass().getSimpleName());
    }
  }

}
