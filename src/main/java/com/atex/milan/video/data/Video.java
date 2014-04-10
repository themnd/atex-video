package com.atex.milan.video.data;

/**
 * Video
 * 09/04/14 on 15:02
 *
 * @author mnova
 */
public class Video
{
  private String id;
  private String videoPath;
  private String thumbPath;

  public String getId()
  {
    return id;
  }

  public void setId(final String id)
  {
    this.id = id;
  }

  public String getVideoPath()
  {
    return videoPath;
  }

  public void setVideoPath(final String videoPath)
  {
    this.videoPath = videoPath;
  }

  public String getThumbPath()
  {
    return thumbPath;
  }

  public void setThumbPath(final String thumbPath)
  {
    this.thumbPath = thumbPath;
  }
}
