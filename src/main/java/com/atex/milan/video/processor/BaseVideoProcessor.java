package com.atex.milan.video.processor;

import java.io.File;

import org.apache.camel.Message;

import com.atex.milan.video.converter.VideoConverter;
import com.atex.milan.video.util.ServiceProperties;
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

  public void setVideoPath(final Message msg, final String path)
  {
    setMessageProperty(msg, VideoConfigurationProcessor.VIDEOPATH_HEADER, path);
  }

  public void setThumbPath(final Message msg, final String path)
  {
    setMessageProperty(msg, VideoConfigurationProcessor.THUMBPATH_HEADER, path);
  }

}
