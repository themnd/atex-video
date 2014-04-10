package com.atex.milan.video.processor;

import java.io.File;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert video to mp4.
 *
 * @author mnova
 */
public class VideoProcessor extends BaseVideoProcessor
{
  private static final Logger logger = LoggerFactory.getLogger(VideoProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("{} - start work", this.getClass().getSimpleName());

    final Message originalMessage = exchange.getIn();

    final Map<String, Object> headers = originalMessage.getHeaders();

    final String name = (String) headers.get("CamelFileName");

    final String path = (String) headers.get("CamelFileAbsolutePath");

    try {
      logger.info("Processing file {}", path);

      final String outFilename = FilenameUtils.getBaseName(name) + ".mp4";
      final File out = new File(getVideoOutputDir(originalMessage), outFilename);
      final File in = new File(path);

      final int exitValue = getVideoConverter().convert(in, out);
      logger.info("exit value {}", exitValue);

      if (exitValue != 0) {
        throw new Exception("Error " + exitValue);
      }

      setVideoPath(originalMessage, out.getAbsolutePath());
      setVideoType(originalMessage, FilenameUtils.getExtension(outFilename));

    } catch (Exception e) {
      logger.error("Error while processing {}: {}", path, e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Processed file {}", path);
    }
  }
}
