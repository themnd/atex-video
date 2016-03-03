package com.atex.milan.video.resolver;

import java.io.File;
import java.util.logging.Logger;

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
  static final Logger LOGGER = Logger.getLogger(MediaFileResolver.class.getName());

  @Inject
  @Named("video.repository.base")
  private String baseRepository;

  @Inject
  @Named("video.repository.archive")
  private String archiveRepository;

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
   * Return a File object that points to the relative media.
   *
   * @param media
   * @return
   */
  public File getFileArchive(final Media media)
  {
    String videoPath = media.getPath();
    if (!videoPath.startsWith("/") && (archiveRepository != null)) {
      videoPath = String.format("%s/%s", archiveRepository, videoPath);
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
    String videoPath = makeRelativeFor(f, baseRepository);
    if (videoPath.equals(f.getAbsolutePath())) {
      videoPath = makeRelativeFor(f, archiveRepository);
    }
    return videoPath;
  }

  private String makeRelativeFor(final File f, final String base) {
    String videoPath = f.getAbsolutePath();
    if (base != null) {
      if (videoPath.startsWith(base)) {
        videoPath = videoPath.substring(base.length() + 1);
        if (videoPath.startsWith("/")) {
          videoPath = videoPath.substring(1);
        }
      }
    }
    return videoPath;
  }

}
