package com.atex.milan.video.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.atex.milan.video.util.ServiceProperties;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
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
  static final Logger logger = Logger.getLogger(VideoConverterImpl.class.getName());

  private final ServiceProperties serviceProperties;

  private String ffmpegBin;
  private String ffprobeBin;
  private String qtfaststartBin;
  private List<String> videoOptions;
  private List<String> videoGlobalOptions;
  private List<String> thumbOptions;
  private List<String> thumbShortOptions;
  private List<String> thumbGlobalOptions;
  private List<String> audioOptions;
  private List<String> audioGlobalOptions;
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
    videoOptions = getOptions("ffmpeg.video.options");
    videoGlobalOptions = getOptions("ffmpeg.video.global.options");
    thumbOptions = getOptions("ffmpeg.thumb.options");
    thumbShortOptions = getOptions("ffmpeg.thumb.short.options");
    thumbGlobalOptions = getOptions("ffmpeg.thumb.global.options");
    audioOptions = getOptions("ffmpeg.audio.options");
    audioGlobalOptions = getOptions("ffmpeg.audio.global.options");
    ffprobeBin = serviceProperties.getProperty("ffprobe.location");
    probeOptions = Lists.newArrayList(Splitter
            .on(" ")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffprobe.probe.options")));
    probeData = Lists.newArrayList(Splitter
            .on("|")
            .omitEmptyStrings()
            .split(serviceProperties.getProperty("ffprobe.probe.data")));
    qtfaststartBin = serviceProperties.getProperty("qtfaststart.location");
    final String dir = serviceProperties.getProperty("ffmpeg.process.workdir");
    if (dir != null) {
      workDir = new File(dir);
    } else {
      workDir = createTempDir();
    }
  }

  private List<String> getOptions(final String optionName) {
    final String options = serviceProperties.getProperty(optionName);
    if (Strings.isNullOrEmpty(options)) {
      return Lists.newArrayList();
    } else {
      return Lists.newArrayList(Splitter
              .on(" ")
              .omitEmptyStrings()
              .split(options));
    }
  }

  @Override
  public int convert(final File in, final File out) throws Exception
  {
    try {
      logger.info("Converting " + in.getAbsolutePath() + " to " +  out.getAbsolutePath());

      final List<String> arguments = createArguments(in, out, videoGlobalOptions, videoOptions);
      return executeFFMpeg(arguments);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while processing " + in.getAbsolutePath() + ": " + e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Converted file " + out.getAbsolutePath());
    }
  }

  @Override
  public int extractThumb(final File video, final File thumb, final boolean useShort) throws Exception
  {
    try {
      logger.info("Extract thumb from " + video.getAbsolutePath() + " to " + thumb.getAbsolutePath());

      final List<String> arguments;
      if (useShort) {
        arguments = createArguments(video, thumb, thumbGlobalOptions, thumbShortOptions);
      } else {
        arguments = createArguments(video, thumb, thumbGlobalOptions, thumbOptions);
      }
      return executeFFMpeg(arguments);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while processing " + video.getAbsolutePath() + ": " + e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Extracted file " + thumb.getAbsolutePath());
    }
  }

  @Override
  public int convertAudio(final File in, final File out) throws Exception {
    try {
      logger.info("Converting " + in.getAbsolutePath() + " to " +  out.getAbsolutePath());

      final List<String> arguments = createArguments(in, out, audioGlobalOptions, audioOptions);
      return executeFFMpeg(arguments);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while processing " + in.getAbsolutePath() + ": " + e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Converted file " + out.getAbsolutePath());
    }
  }

  @Override
  public Map<String, Object> extractVideoInfo(final File video) throws Exception
  {
    try {
      logger.info("Extract video info from " + video.getAbsolutePath());

      final List<String> arguments = createFFProbeArguments(video, probeOptions);
      return executeFFProbe(arguments);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while processing " + video.getAbsolutePath() + ": " + e.getMessage(), e);
      throw e;
    } finally {
      logger.info("Extracted info from file " + video.getAbsolutePath());
    }
  }

  @Override
  public int qtFastStart(final File in, final File out) throws Exception {

    try {
      logger.info("qtFastStart video from " + in.getAbsolutePath());

      final List<String> arguments = createQTFastStartArguments(in, out);
      return executeQTFastStart(arguments);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while processing " + in.getAbsolutePath() + ": " + e.getMessage(), e);
      throw e;
    } finally {
      logger.info("qtFastStart video to file " + out.getAbsolutePath());
    }
  }

  private int executeFFMpeg(final List<String> arguments) throws IOException,
                                                                 InterruptedException
  {
    return executeExternalProcess(arguments);
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
      logger.severe(errs);

      final int exitValue = p.waitFor();
      logger.info("exit value " + exitValue);
      if (exitValue == 0) {
        final Map<String, Object> m = new HashMap<String, Object>();
        return (Map<String, Object>) new Gson().fromJson(ins, m.getClass());
      }
      return null;
    } finally {
      FileUtils.deleteDirectory(tmpWorkingDir);
    }
  }

  private int executeQTFastStart(final List<String> arguments) throws IOException, InterruptedException
  {
    return executeExternalProcess(arguments);
  }

  private int executeExternalProcess(final List<String> arguments) throws IOException, InterruptedException
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
      logger.severe(errs);

      final int exitValue = p.waitFor();
      logger.info("exit value " + exitValue);
      return exitValue;
    } finally {
      FileUtils.deleteDirectory(tmpWorkingDir);
    }
  }

  private List<String> createArguments(final File in, final File out, final List<String> globals,
                                       final List<String> options)
  {
    final List<String> arguments = new ArrayList<String>();
    arguments.add(ffmpegBin);
    arguments.addAll(globals);
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

  private List<String> createQTFastStartArguments(final File in, final File out)
  {
    final List<String> arguments = new ArrayList<String>();
    arguments.add(qtfaststartBin);
    arguments.add(in.getAbsolutePath());
    arguments.add(out.getAbsolutePath());
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
