package com.atex.milan.video.processor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * This processor just logs the message header.
 *
 * @author mnova
 */
public class LoggerProcessor implements Processor
{
  private static final Logger logger = Logger.getLogger(LoggerProcessor.class.getName());

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.fine("LoggerProcessor - start work");

    final Message originalMessage = exchange.getIn();
    final Map<String, Object> headers = originalMessage.getHeaders();

    for (final Entry<String, Object> entry : headers.entrySet()) {
      final String key = entry.getKey();
      logger.fine("Header key " + key);
      logger.fine("Header val " + entry.getValue());
    }
  }
}
