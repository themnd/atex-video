package com.atex.milan.video.processor;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.atex.milan.video.util.InjectorUtils;

/**
 * Base guice processor
 *
 * @author mnova
 */
public abstract class BaseGuiceProcessor implements Processor
{
  private static final Logger logger = Logger.getLogger(BaseGuiceProcessor.class.getName());

  public BaseGuiceProcessor()
  {
    final InjectorUtils injectorUtils = InjectorUtils.getInstance();
    injectorUtils.injectMembers(this);
  }

  protected <T extends Object> T getMessageProperty(final Message msg, final String name)
  {
    final Map<String, Object> headers = msg.getHeaders();
    return (T) headers.get(name);
  }

  protected void setMessageProperty(final Message msg, final String name, final Object value)
  {
    final Map<String, Object> headers = msg.getHeaders();
    headers.put(name, value);
  }
}
