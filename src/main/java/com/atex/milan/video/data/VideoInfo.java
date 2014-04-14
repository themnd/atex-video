package com.atex.milan.video.data;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * VideoInfo
 * 11/04/14 on 22:51
 *
 * @author mnova
 */
public class VideoInfo
{
  private List<StreamInfo> streams = Lists.newArrayList();
  private FormatInfo formatInfo;

  public List<StreamInfo> getStreams()
  {
    return streams;
  }

  public void setStreams(final List<StreamInfo> streams)
  {
    this.streams = streams;
  }

  public FormatInfo getFormatInfo()
  {
    return formatInfo;
  }

  public void setFormatInfo(final FormatInfo formatInfo)
  {
    this.formatInfo = formatInfo;
  }
}
