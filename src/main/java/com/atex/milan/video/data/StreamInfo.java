package com.atex.milan.video.data;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * StreamInfo
 *
 * @author mnova
 */
public class StreamInfo
{
  private String type;
  private Map<String, Object> data = Maps.newHashMap();

  public String getType()
  {
    return type;
  }

  public void setType(final String type)
  {
    this.type = type;
  }

  public Map<String, Object> getData()
  {
    return data;
  }

  public void setData(final Map<String, Object> data)
  {
    this.data = data;
  }

}
