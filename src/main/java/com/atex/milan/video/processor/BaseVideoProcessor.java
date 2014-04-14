package com.atex.milan.video.processor;

import java.io.File;
import java.util.List;

import org.apache.camel.Message;

import com.atex.milan.video.converter.VideoConverter;
import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.VideoInfo;
import com.atex.milan.video.util.ServiceProperties;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * VideoProcessor
 *
 * @author mnova
 */
public abstract class BaseVideoProcessor extends BaseGuiceProcessor
{
  @Inject
  private ServiceProperties serviceProperties;

  @Inject
  private VideoConverter videoConverter;

  private File outDir;

  @Inject
  public void init()
  {
    outDir = new File(serviceProperties.getProperty("ffmpeg.process.outdir"));
    outDir.mkdirs();
  }

  public VideoConverter getVideoConverter()
  {
    return videoConverter;
  }

  public File getOutDir()
  {
    return outDir;
  }

  public File getVideoOutputDir(final Message msg)
  {
    return (File) getMessageProperty(msg, VideoConfigurationProcessor.OUTPUTDIR_HEADER);
  }

  public String getVideoId(final Message msg)
  {
    return (String) getMessageProperty(msg, VideoConfigurationProcessor.VIDEOID_HEADER);
  }

  public void setVideoInfoOrig(final Message msg, final VideoInfo vi)
  {
    setMessageProperty(msg, VideoConfigurationProcessor.ORIG_VIDEO_INFO_HEADER, vi);
  }
  
  public VideoInfo getVideoInfoOrig(final Message msg)
  {
    return (VideoInfo) getMessageProperty(msg, VideoConfigurationProcessor.ORIG_VIDEO_INFO_HEADER);
  }

  public String getTimestamp(final Message msg)
  {
    return (String) getMessageProperty(msg, VideoConfigurationProcessor.TIMESTAMP_HEADER);
  }

  public void addMedia(final Message msg, final Media media)
  {
    List<Media> list = getMedia(msg);
    if (list == null) {
      list = Lists.newArrayList();
      setMessageProperty(msg, VideoConfigurationProcessor.MEDIA_HEADER, list);
    }
    list.add(media);
  }

  public List<Media> getMedia(final Message msg)
  {
    return (List<Media>) getMessageProperty(msg, VideoConfigurationProcessor.MEDIA_HEADER);
  }
}
