package com.atex.milan.video.resolver;

import java.io.File;

import com.atex.milan.video.data.Media;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Resolves the relative media path.
 *
 * @author mnova
 */
public class MediaFileResolver
{
  @Inject
  @Named("video.repository.base")
  private String baseRepository;

  /**
   * Return a File object that points to the relative media.
   *
   * @param media
   * @return
   */
  public File getFile(final Media media)
  {
    String videoPath = media.getPath();
    if (!videoPath.startsWith("/") && (baseRepository != null)) {
      videoPath = String.format("%s/%s", baseRepository, videoPath);
    }
    return new File(videoPath);
  }

  /**
   * Return the relative path to the given file f.
   *
   * @param f
   * @return
   */
  public String makeRelative(final File f)
  {
    String videoPath = f.getAbsolutePath();
    if (baseRepository != null) {
      if (videoPath.startsWith(baseRepository)) {
        videoPath = videoPath.substring(baseRepository.length() + 1);
        if (videoPath.startsWith("/")) {
          videoPath = videoPath.substring(1);
        }
      }
    }
    return videoPath;
  }

}
