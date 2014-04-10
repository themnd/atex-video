package com.atex.milan.video.couchbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.data.Video;
import com.atex.milan.video.exceptions.CouchException;
import com.google.inject.Inject;

/**
 * Video Repository implementation.
 *
 * This is usually used as a Singleton.
 *
 * @author mnova
 */
public class VideoRepositoryImpl implements VideoRepository
{
  private static final Logger logger = LoggerFactory.getLogger(VideoRepositoryImpl.class);

  public static final String VIDEO_COUNTER_ID = "video::counter";

  final private DBClient client;

  @Inject
  public VideoRepositoryImpl(final DBClient client)
  {
    this.client = client;
  }

  @Override
  public void initRepository() throws CouchException
  {
    client.init();

    // initialize the video counter

    if (client.get(VIDEO_COUNTER_ID) == null) {
      if (!client.add(VIDEO_COUNTER_ID, 0)) {
        throw new CouchException("Cannot initialize the video counter!");
      }
    }
  }

  @Override
  public void shutdown()
  {
    client.shutdown();
  }

  @Override
  public Video addVideo(final Video v) throws CouchException
  {
    final String id = createVideoId();
    v.setId(id);
    if (client.add(id, v)) {
      return v;
    }
    return null;
  }

  @Override
  public Video getVideo(final String id) throws CouchException
  {
    final String videoId;
    if (id.startsWith("video::")) {
      videoId = id;
    } else {
      videoId = "video::" + id;
    }
    return client.get(videoId, Video.class);
  }

  private String createVideoId() throws CouchException
  {
    final long nextId = getNextId();
    return createVideoId(nextId);
  }

  private String createVideoId(final long id) throws CouchException
  {
    return "video::" + id;
  }

  private long getNextId() throws CouchException
  {
    return client.incr(VIDEO_COUNTER_ID);
  }
}
