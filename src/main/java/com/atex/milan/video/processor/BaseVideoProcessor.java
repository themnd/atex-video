package com.atex.milan.video.processor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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
  private static final Logger logger = Logger.getLogger(BaseVideoProcessor.class.getName());

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

  public ServiceProperties getServiceProperties() {
    return serviceProperties;
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

  public File getOutDirWithTimestamp(final Date d) {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    final File dir = new File(getOutDir(), dateFormat.format(d));
    dir.mkdirs();
    return dir;
  }

  protected boolean checkFileIsStable(final File f)
  {
    return checkFileIsStable(f, 10, 120);
  }

  /**
   * Check that file have a stable size for at least stableTimeout seconds.
   *
   * @param f
   *          the file to be checked.
   * @param stableTimeout
   *          the number of seconds the files must have the same size.
   * @param maxTimeouts
   *          the max number of seconds we may wait.
   */
  protected boolean checkFileIsStable(final File f, final int stableTimeout, int maxTimeouts)
  {
    boolean fileIsStable = false;

    final File filePath = f.getAbsoluteFile();

    logger.info("check file stability for " + filePath);

    try {
      while (!f.exists() && (maxTimeouts >= 0)) {
        logger.fine("file " + filePath + " not exist, waiting again for " + maxTimeouts);
        maxTimeouts -= 1;
        Thread.sleep(1000);
      }
      if (f.exists()) {
        logger.fine("file " + filePath + " now exist");

        int curTimeouts = 0;
        long prevSize = -1;

        while ((curTimeouts < stableTimeout) && (maxTimeouts >= 0)) {
          final long curSize = f.length();

          logger.fine("file " + filePath + " has size " + curSize + " (timeouts: " + maxTimeouts + ")");

          if (prevSize != curSize) {
            logger.info("file " + filePath + " changed size from " + prevSize + " to " + curSize);
            curTimeouts = 0;
            prevSize = curSize;
            continue;
          }

          logger.fine("wait " + curTimeouts + " out of " + stableTimeout + " for file " + filePath);

          if (curSize > 0) {
            curTimeouts += 1;
          }

          maxTimeouts -= 1;
          Thread.sleep(1000);
        }

        // file is stable if size not change in specified stableTimeout and maxTimeouts is not exceeded
        // we consider not stable a file of size 0
        fileIsStable = (maxTimeouts >= 0) && (f.length() > 0);

      }
    } catch (final InterruptedException e) {
      logger.severe(e.getMessage());
      throw new RuntimeException(e);
    }

    logger.info("file " + filePath + " is " + (fileIsStable ? "stable" : "not stable"));

    return fileIsStable;
  }


}
