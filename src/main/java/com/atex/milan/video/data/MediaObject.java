package com.atex.milan.video.data;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Video model
 *
 * @author mnova
 */
public class MediaObject extends BaseModel
{
  private String id;
  private String uuid;
  private String externalId;
  private String securityParentId;
  private String name;
  private String title;
  private String siteCode;
  private String contentId;
  private long published;
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

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(final String externalId) {
    this.externalId = externalId;
  }

  public String getSecurityParentId() {
    return securityParentId;
  }

  public void setSecurityParentId(final String securityParentId) {
    this.securityParentId = securityParentId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getSiteCode() {
    return siteCode;
  }

  public void setSiteCode(final String siteCode) {
    this.siteCode = siteCode;
  }

  public String getContentId() {
    return contentId;
  }

  public void setContentId(final String contentId) {
    this.contentId = contentId;
  }

  public long getPublished() {
    return published;
  }

  public void setPublished(final long published) {
    this.published = published;
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
