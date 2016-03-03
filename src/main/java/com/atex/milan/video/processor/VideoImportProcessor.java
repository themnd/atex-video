package com.atex.milan.video.processor;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FilenameUtils;

import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.data.DataType;
import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.MediaObject;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * VideoImportProcessor
 *
 * @author mnova
 */
public class VideoImportProcessor extends BaseMediaProcessor
{
  static final Logger LOGGER = Logger.getLogger(VideoImportProcessor.class.getName());

  @Inject
  private VideoRepository videoRepository;

  @Inject
  private MediaFileResolver mediaFileResolver;

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    LOGGER.fine(this.getClass().getSimpleName() + " - start work");

    try {
      final Message msg = exchange.getIn();

      final Map<String, Object> headers = msg.getHeaders();

      final String name = (String) headers.get("CamelFileNameOnly");

      final String videoUUID = (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOID_HEADER);

      final long ts = new Date().getTime();
      final long processTime = (ts - Long.parseLong(getTimestamp(msg)));

      try {
        MediaObject v = videoRepository.getMediaByUUID(videoUUID);
        final boolean isNew = (v == null);
        if (v == null) {
          v = new MediaObject();
          v.setType(DataType.VIDEO);
          v.setUuid(videoUUID);
          v.setCreated(ts);

          LOGGER.info("Creating video with uuid: " + videoUUID);
        } else {
          LOGGER.info("Updating video with uuid: " + videoUUID);

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
              LOGGER.info("Removing file " + f.getAbsolutePath());
              f.delete();
            } catch (Exception e) {
              LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
          }
        }

        v.setName(FilenameUtils.getName(name));
        v.setModified(ts);
        v.setProcessTime(processTime);
        v.setVideoInfo(getVideoInfoOrig(msg));
        v.setMedia(getMedia(msg));
        v.setSiteCode(getSiteCode(msg));

        final String title = Optional.fromNullable((String) headers.get(VideoConfigurationProcessor.VIDEONAME_HEADER))
                .or(v.getName());
        v.setTitle(title);
        if (headers.get(VideoConfigurationProcessor.VIDEODATE_HEADER) instanceof Date) {
          final Date videoDate = (Date) headers.get(VideoConfigurationProcessor.VIDEODATE_HEADER);
          v.setPublished(videoDate.getTime());
        }

        if (isNew) {
          final MediaObject newVideo = videoRepository.addMedia(v);
          if (newVideo == null) {
            throw new Exception("Error saving in database");
          }
          createPolopolyContentXML(newVideo);
        } else {
          if (!videoRepository.setMedia(v)) {
            throw new Exception("Error saving in database");
          }
          createPolopolyContentXML(v);
        }

      } finally {

        LOGGER.info("Processed video with uuid: " + videoUUID);
      }

    } finally {
      LOGGER.info(this.getClass().getSimpleName() + " - end work");
    }
  }

  private String getSiteCode(final Message msg) {
    return getMessageProperty(msg, VideoConfigurationProcessor.SITECODE_HEADER);
  }

}
