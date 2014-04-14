package com.atex.milan.video.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.util.ServiceProperties;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;

/**
 * Process a video file with ffmpeg.
 * 
 * @author mnova
 */
public class VideoConverterImpl implements VideoConverter
{
  private static final Logger logger = LoggerFactory.getLogger(VideoConverterImpl.class);

  final private ServiceProperties serviceProperties;

  private String ffmpegBin;
  private String ffprobeBin;
  private List<String> videoOptions;
  private List<String> thumbOptions;
  private List<String> probeOptions;
  private List<String> probeData;
  private File workDir;

  @Inject
  public VideoConverterImpl(final ServiceProperties serviceProperties)
  {
    this.serviceProperties = serviceProperties;
  }

  @Inject
  public void init()
  {
    ffmpegBin = serviceProperties.getProperty("ffmpeg.location");
    videoOptions = Lists.newArrayList(Splitter
            .on(" ")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffmpeg.video.options")));
    thumbOptions = Lists.newArrayList(Splitter
            .on(" ")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffmpeg.thumb.options")));
    ffprobeBin = serviceProperties.getProperty("ffprobe.location");
    probeOptions = Lists.newArrayList(Splitter
            .on(" ")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffprobe.probe.options")));
    probeData = Lists.newArrayList(Splitter
            .on("|")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffprobe.probe.data")));
    final String dir = serviceProperties.getProperty("ffmpeg.process.workdir");
    if (dir != null) {
      workDir = new File(dir);
    } else {
      workDir = createTempDir();
    }
  }

  @Override
  public int convert(final File in, final File out) throws Exception
  {
    try {
      logger.info("Converting {} to {}", in.getAbsolutePath(), out.getAbsolutePath());

      final List<String> arguments = createArguments(in, out, videoOptions);
      return executeFFMpeg(arguments);
    } catch (Exception e) {
      logger.error("Error while processing {}: {}", in.getAbsolutePath(), e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Converted file {}", out.getAbsolutePath());
    }
  }

  @Override
  public int extractThumb(final File video, final File thumb) throws Exception
  {
    try {
      logger.info("Extract thumb from {} to {}", video.getAbsolutePath(), thumb.getAbsolutePath());

      final List<String> arguments = createArguments(video, thumb, thumbOptions);
      return executeFFMpeg(arguments);
    } catch (Exception e) {
      logger.error("Error while processing {}: {}", video.getAbsolutePath(), e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Extracted file {}", thumb.getAbsolutePath());
    }
  }

  @Override
  public Map<String, Object> extractVideoInfo(final File video) throws Exception
  {
    try {
      logger.info("Extract video info from {}", video.getAbsolutePath());

      final List<String> arguments = createFFProbeArguments(video, probeOptions);
      return executeFFProbe(arguments);
    } catch (Exception e) {
      logger.error("Error while processing {}: {}", video.getAbsolutePath(), e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Extracted info from file {}", video.getAbsolutePath());
    }
  }

  private int executeFFMpeg(final List<String> arguments) throws IOException,
                                                                 InterruptedException
  {
    final File tmpWorkingDir = createTempDir(workDir);

    try {
      final Process p = new ProcessBuilder()
              .command(arguments)
              .directory(tmpWorkingDir)
              .start();

      final String ins = IOUtils.toString(p.getInputStream());
      final String errs = IOUtils.toString(p.getErrorStream());

      logger.info(ins);
      logger.error(errs);

      final int exitValue = p.waitFor();
      logger.info("exit value {}", exitValue);
      return exitValue;
    } finally {
      FileUtils.deleteDirectory(tmpWorkingDir);
    }
  }

  private Map<String, Object> executeFFProbe(final List<String> arguments) throws IOException,
                                                                                  InterruptedException
  {
    final File tmpWorkingDir = createTempDir(workDir);

    try {
      final Process p = new ProcessBuilder()
              .command(arguments)
              .directory(tmpWorkingDir)
              .start();

      final String ins = IOUtils.toString(p.getInputStream());
      final String errs = IOUtils.toString(p.getErrorStream());

      logger.info(ins);
      logger.error(errs);

      final int exitValue = p.waitFor();
      logger.info("exit value {}", exitValue);
      if (exitValue == 0) {
        final Map<String, Object> m = new HashMap<String, Object>();
        return (Map<String, Object>) new Gson().fromJson(ins, m.getClass());
      }
      return null;
    } finally {
      FileUtils.deleteDirectory(tmpWorkingDir);
    }
  }

  private List<String> createArguments(final File in, final File out, final List<String> options)
  {
    final List<String> arguments = new ArrayList<String>();
    arguments.add(ffmpegBin);
    arguments.add("-i");
    arguments.add(in.getAbsolutePath());
    arguments.addAll(options);
    if (out != null) {
      arguments.add(out.getAbsolutePath());
    }
    return arguments;
  }

  private List<String> createFFProbeArguments(final File in, final List<String> options)
  {
    final List<String> arguments = new ArrayList<String>();
    arguments.add(ffprobeBin);
    arguments.add("-i");
    arguments.add(in.getAbsolutePath());
    arguments.addAll(options);
    return arguments;
  }

  private File createTempDir()
  {
    final File baseDir = new File(System.getProperty("java.io.tmpdir"));
    return createTempDir(baseDir);
  }

  private File createTempDir(final File baseDir)
  {
    final String baseName = System.currentTimeMillis() + "-";

    for (int counter = 0; counter < 10000; counter++) {
      File tempDir = new File(baseDir, baseName + counter);
      if (tempDir.mkdir()) {
        tempDir.deleteOnExit();
        return tempDir;
      }
    }
    throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to "
        + baseName + 9999 + ')');
  }

}
