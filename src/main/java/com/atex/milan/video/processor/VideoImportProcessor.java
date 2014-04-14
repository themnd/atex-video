package com.atex.milan.video.processor;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.data.DataType;
import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.Video;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.google.common.collect.Lists;
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
  
  @Inject
  private MediaFileResolver mediaFileResolver;

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
        
        final List<File> files = Lists.newArrayList();
        for (final Media m : v.getMedia()) {
          final File f = mediaFileResolver.getFile(m);
          if (f != null && f.exists()) {
            files.add(f);
          }
        }

        // do not remove unchanged media files.

        for (final Media m : getMedia(msg)) {
          final File f = mediaFileResolver.getFile(m);
          if (files.contains(f)) {
            files.remove(f);
          }
        }

        for (final File f : files) {
          try {
            logger.trace("Removing file {}", f.getAbsolutePath());
            f.delete();
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
          }
        }
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
