package com.atex.milan.video.processor;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor just logs the message header.
 *
 * @author mnova
 */
public class LoggerProcessor implements Processor
{
  private static final Logger logger = LoggerFactory.getLogger(LoggerProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception
  {
    logger.trace("LoggerProcessor - start work");

    final Message originalMessage = exchange.getIn();
    final Map<String, Object> headers = originalMessage.getHeaders();

    for (final Entry<String, Object> entry : headers.entrySet()) {
      final String key = entry.getKey();
      logger.trace("Header key {}", key);
      logger.trace("Header val {}", entry.getValue());
    }
  }
}
