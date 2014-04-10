package com.atex.milan.video.converter;

import java.io.File;

/**
 * VideoConverter
 * 09/04/14 on 11:30
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
  public int convert(final File in, final File out) throws Exception;

  /**
   * Extract the thumbnail from the video
   *
   * @param video
   * @param thumb
   * @return
   * @throws Exception
   */
  public int extractThumb(final File video, final File thumb) throws Exception;
}
