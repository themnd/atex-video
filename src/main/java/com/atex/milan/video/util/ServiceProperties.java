package com.atex.milan.video.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.base.Optional;

public class ServiceProperties extends Properties
{
  private static final long serialVersionUID = 1L;
  
  public ServiceProperties() throws IOException
  {
    super();

    System.out.println(this.getClass().getResource("/service.properties").toString());
    final InputStream stream = this.getClass()
      .getResourceAsStream("/service.properties");

    load(stream);
  }
  
  public Integer getIntegerValue(final String key, final int defaultValue)
  {
    int value;
    try {
      final String s = getProperty(key);
      if (s == null || s.length() == 0) {
        return defaultValue;
      }
      value = Integer.parseInt(s);
    } catch (final NumberFormatException ne) {
      return defaultValue;
    }
    return value;
  }

  /**
   * @param key
   *          property key
   * @return The boolean value represented from the property named key
   */
  public boolean getBooleanValue(final String key, final boolean defaultValue)
  {
    return Boolean.valueOf(Optional.fromNullable(getProperty(key)).or(Boolean.toString(defaultValue)));
  }

}
