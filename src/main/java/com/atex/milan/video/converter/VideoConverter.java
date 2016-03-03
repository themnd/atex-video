package com.atex.milan.video.converter;

import java.io.File;
import java.util.Map;

/**
 * Interface for processing a video.
 *
 * @author mnova
 */
public interface VideoConverter
{
  /**
   * Convert video
   *
   * @param in
   * @param out
   * @return
   * @throws Exception
   */
  int convert(final File in, final File out) throws Exception;

  /**
   * Extract the thumbnail from the video
   *
   * @param video
   * @param thumb
   * @param useShort
   * @return
   * @throws Exception
   */
  int extractThumb(final File video, final File thumb, final boolean useShort) throws Exception;

  /**
   * Convert audio
   *
   * @param in
   * @param out
   * @return
   * @throws Exception
   */
  int convertAudio(final File in, final File out) throws Exception;

  /**
   * Extract video information.
   *
   * @param video
   * @return
   * @throws Exception
   */
  Map<String, Object> extractVideoInfo(final File video) throws Exception;

  /**
   * Call qtfaststart to fix IOS streaming.
   * See http://www.stoimen.com/blog/2010/11/12/how-to-make-mp4-progressive-with-qt-faststart.
   *
   * @param in
   * @param out
   * @return
   * @throws Exception
   */
  int qtFastStart(final File in, final File out) throws Exception;
}
