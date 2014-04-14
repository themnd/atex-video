package com.atex.milan.video.processor;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.data.FormatInfo;
import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.StreamInfo;
import com.atex.milan.video.data.VideoInfo;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Convert video to mp4.
 *
 * @author mnova
 */
public class VideoMediaProcessor extends BaseVideoProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoMediaProcessor.class);

  private static final String[] TAGNAMES = {
          "codec_name",
          "codec_long_name",
          "width",
          "height",
          "display_aspect_ratio",
          "duration_ts",
          "bit_rate",
          "channels",
          "sample_rate"
  };

  @Inject
  private MediaFileResolver mediaFileResolver;

  final private String type;
  final private String extension;
  final private boolean isThumbnail;

  public VideoMediaProcessor(final String type, final String extension, final Boolean isThumbnail)
  {
    this.type = type;
    this.extension = extension;
    this.isThumbnail = isThumbnail;
  }

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message originalMessage = exchange.getIn();

    final Map<String, Object> headers = originalMessage.getHeaders();

    final String name = (String) headers.get("CamelFileName");

    final String path = (String) headers.get("CamelFileAbsolutePath");

    final String ts = getTimestamp(originalMessage);

    try {
      logger.info("Processing file {}", path);

      final String outFilename = String.format("%s.%s", ts, extension);
      final File out = new File(getVideoOutputDir(originalMessage), outFilename);
      final File in = new File(path);

      final int exitValue;

      if (isThumbnail) {
        exitValue = getVideoConverter().extractThumb(in, out);
      } else {
        exitValue = getVideoConverter().convert(in, out);
      }

      logger.info("exit value {}", exitValue);
      if (exitValue != 0) {
        throw new Exception("Error " + exitValue);
      }

      if (!isThumbnail) {
        setVideoInfoOrig(originalMessage, createVideoInfo(in));
      }

      final String videoPath = mediaFileResolver.makeRelative(out);

      final Media media = new Media();
      media.setPath(videoPath);
      media.setType(type);
      media.setExtension(extension);
      media.setSize(out.length());
      if (!isThumbnail) {
        media.setVideoInfo(createVideoInfo(out));
      }

      addMedia(originalMessage, media);

    } catch (Exception e) {
      logger.error("Error while processing {}: {}", path, e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Processed file {}", path);
    }
  }

  private VideoInfo createVideoInfo(final File f) throws Exception
  {
    final FormatInfo fi = new FormatInfo();

    final Map<String, Object> info = getVideoConverter().extractVideoInfo(f);

    {
      final Map<String, String> map = (Map<String, String>) info.get("format");
      if (map != null) {
        fi.setName(map.get("format_name"));
        fi.setLongname(map.get("format_long_name"));
        fi.setDuration(map.get("duration"));
      }
    }
    
    final List<StreamInfo> streams = Lists.newArrayList();

    {
      final List<Map<String, Object>> list = (List<Map<String, Object>>) info.get("streams");
      if (list != null) {
        for (final Map<String, Object> map : list) {

          final StreamInfo si = new StreamInfo();
          streams.add(si);

          if (map.get("codec_type") != null) {
            si.setType((String) map.get("codec_type"));
          }

          for (final String n : TAGNAMES) {
            final Object value = map.get(n);
            if (value != null) {
              si.getData().put(n, map.get(n));
            }
          }
        }
      }
    }

    final VideoInfo vi = new VideoInfo();
    vi.setFormatInfo(fi);
    vi.setStreams(streams);
    return vi;
  }

}
