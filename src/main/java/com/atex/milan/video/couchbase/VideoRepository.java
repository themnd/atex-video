package com.atex.milan.video.couchbase;

import com.atex.milan.video.data.Video;
import com.atex.milan.video.exceptions.CouchException;

/**
 * Repository video
 *
 * @author mnova
 */
public interface VideoRepository
{
  /**
   * Initialize the repository access.
   * Usually called when the context is started.
   *
   * @throws CouchException
   */
  void initRepository() throws CouchException;

  /**
   * Shutdown the repository.
   * Usually called when the context is destroyed.
   */
  void shutdown();

  /**
   * Add a new video.
   *
   * @param v the object to be added.
   * @return the Video object with the id set.
   * @throws CouchException
   */
  Video addVideo(final Video v) throws CouchException;

  boolean setVideo(Video v) throws CouchException;

  /**
   * Fetch the video data.
   *
   * @param id the id of the video.
   * @return null if the video has not been found.
   * @throws CouchException
   */
  Video getVideo(final String id) throws CouchException;

  Video getVideoByUUID(String uuid) throws CouchException;
}
