package com.atex.milan.video.processor;

import java.util.Map;

import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.util.InjectorUtils;

/**
 * Base guice processor
 *
 * @author mnova
 */
public abstract class BaseGuiceProcessor implements Processor
{
  private static final Logger logger = LoggerFactory.getLogger(BaseGuiceProcessor.class);

  public BaseGuiceProcessor()
  {
    final InjectorUtils injectorUtils = InjectorUtils.getInstance();
    injectorUtils.injectMembers(this);
  }

  protected Object getMessageProperty(final Message msg, final String name)
  {
    final Map<String, Object> headers = msg.getHeaders();
    return (Object) headers.get(name);
  }

  protected void setMessageProperty(final Message msg, final String name, final Object value)
  {
    final Map<String, Object> headers = msg.getHeaders();
    headers.put(name, value);
  }
}
