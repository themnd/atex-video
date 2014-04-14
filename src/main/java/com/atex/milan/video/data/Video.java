package com.atex.milan.video.data;

import java.util.List;

import com.google.common.collect.Lists;

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
  private long created;
  private long modified;
  private long processTime;
  private VideoInfo videoInfo;
  private List<Media> media = Lists.newArrayList();

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

  public long getCreated()
  {
    return created;
  }

  public void setCreated(final long created)
  {
    this.created = created;
  }

  public long getModified()
  {
    return modified;
  }

  public void setModified(final long modified)
  {
    this.modified = modified;
  }

  public long getProcessTime()
  {
    return processTime;
  }

  public void setProcessTime(final long processTime)
  {
    this.processTime = processTime;
  }

  public VideoInfo getVideoInfo()
  {
    return videoInfo;
  }

  public void setVideoInfo(final VideoInfo videoInfo)
  {
    this.videoInfo = videoInfo;
  }

  public List<Media> getMedia()
  {
    return media;
  }

  public void setMedia(final List<Media> media)
  {
    this.media = media;
  }

}
