package com.atex.milan.video.data;

/**
 * Video model
 *
 * @author mnova
 */
public class Video extends BaseModel
{
  private String id;
  private String uuid;
  private String name;
  private String videoType;
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

  public String getUuid()
  {
    return uuid;
  }

  public void setUuid(final String uuid)
  {
    this.uuid = uuid;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public String getVideoType()
  {
    return videoType;
  }

  public void setVideoType(final String videoType)
  {
    this.videoType = videoType;
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
