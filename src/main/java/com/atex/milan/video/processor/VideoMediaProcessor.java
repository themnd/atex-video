package com.atex.milan.video.processor;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.VideoInfo;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.google.inject.Inject;

/**
 * Convert video to mp4.
 *
 * @author mnova
 */
public class VideoMediaProcessor extends BaseMediaProcessor {

  static final Logger LOGGER = Logger.getLogger(VideoMediaProcessor.class.getName());

  @Inject
  private MediaFileResolver mediaFileResolver;

  private final String type;
  private final String extension;
  private final boolean isThumbnail;

  public VideoMediaProcessor(final String type, final String extension, final Boolean isThumbnail) {
    this.type = type;
    this.extension = extension;
    this.isThumbnail = isThumbnail;
  }

  @Override
  public void process(final Exchange exchange) throws Exception {

    LOGGER.info(this.getClass().getSimpleName() + " - start work");

    final Message originalMessage = exchange.getIn();

    final Map<String, Object> headers = originalMessage.getHeaders();

    final String name = (String) headers.get("CamelFileName");

    final String path = (String) headers.get("CamelFileAbsolutePath");

    final String ts = getTimestamp(originalMessage);

    final String videoId = getVideoId(originalMessage);

    try {
      LOGGER.info("Processing file " + path);

      final String outFilename = String.format("%s_%s.%s", videoId, ts, extension);
      final File out = new File(getVideoOutputDir(originalMessage), outFilename);
      final File in = new File(path);

      final int exitValue;

      if (isThumbnail) {
        final VideoInfo videoInfo = getVideoInfoOrig(originalMessage);
        final boolean useShort = isShort(videoInfo);
        exitValue = getVideoConverter().extractThumb(in, out, useShort);
      } else {
        exitValue = convertVideo(in, out);
      }

      LOGGER.info("exit value " + exitValue);
      if (exitValue != 0) {
        throw new Exception("Error " + exitValue);
      }

      if (!isThumbnail) {
        setVideoInfoOrig(originalMessage, createMediaInfo(in));
      }

      final String videoPath = mediaFileResolver.makeRelative(out);

      final Media media = new Media();
      media.setPath(videoPath);
      media.setType(type);
      media.setExtension(extension);
      media.setSize(out.length());
      if (!isThumbnail) {
        media.setVideoInfo(createMediaInfo(out));
      }

      addMedia(originalMessage, media);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error while processing " + path + ": " + e.getMessage(), e);
      throw e;
    } finally {
      LOGGER.info("Processed file " + path);
    }
  }

}
