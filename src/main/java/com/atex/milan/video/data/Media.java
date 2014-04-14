package com.atex.milan.video.data;

/**
 * Media
 * 11/04/14 on 09:21
 *
 * @author mnova
 */
public class Media
{
  private String type;
  private String extension;
  private String path;
  private long size;
  private VideoInfo videoInfo;

  public String getType()
  {
    return type;
  }

  public void setType(final String type)
  {
    this.type = type;
  }

  public String getExtension()
  {
    return extension;
  }

  public void setExtension(final String extension)
  {
    this.extension = extension;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(final String path)
  {
    this.path = path;
  }

  public long getSize()
  {
    return size;
  }

  public void setSize(final long size)
  {
    this.size = size;
  }

  public VideoInfo getVideoInfo()
  {
    return videoInfo;
  }

  public void setVideoInfo(final VideoInfo videoInfo)
  {
    this.videoInfo = videoInfo;
  }
}
