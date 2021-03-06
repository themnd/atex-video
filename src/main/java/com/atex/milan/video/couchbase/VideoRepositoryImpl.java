package com.atex.milan.video.couchbase;

import java.util.List;
import java.util.logging.Logger;

import com.atex.milan.video.data.MediaObject;
import com.atex.milan.video.exceptions.CouchException;
import com.couchbase.client.protocol.views.ComplexKey;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;
import com.couchbase.client.protocol.views.ViewDesign;
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
  static final Logger LOGGER = Logger.getLogger(VideoRepositoryImpl.class.getName());

  public static final String VIDEO_COUNTER_ID = "video::counter";
  public static final String VIDEO_DESIGN = "video_ds";
  public static final String VIDEO_UUID_VIEW = "video_uuid";
  public static final String VIDEO_EXTERNALID_VIEW = "video_externalid";

  private final DBClient client;

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

    {
      final DesignDocument doc = new DesignDocument(VIDEO_DESIGN);
      {
        final String mapFunction = "function (doc, meta) {\n" +
                "  if (doc.type) {\n" +
                "    emit(doc.uuid, meta.id); \n" +
                "  }\n" +
                "}";
        final ViewDesign viewDesign = new ViewDesign(VIDEO_UUID_VIEW, mapFunction);
        doc.getViews().add(viewDesign);
      }
      {
        final String mapFunction = "function (doc, meta) {\n" +
                "  if (doc.type && doc.externalId) {\n" +
                "    emit(doc.externalId, meta.id); \n" +
                "  }\n" +
                "}";
        final ViewDesign viewDesign = new ViewDesign(VIDEO_EXTERNALID_VIEW, mapFunction);
        doc.getViews().add(viewDesign);
      }
      client.createDesignDoc(doc);
    }
  }

  @Override
  public void shutdown()
  {
    client.shutdown();
  }

  @Override
  public MediaObject addMedia(final MediaObject v) throws CouchException
  {
    final String id = createVideoId();
    v.setId(id);
    if (client.add(id, v)) {
      return v;
    }
    return null;
  }

  @Override
  public boolean setMedia(final MediaObject v) throws CouchException
  {
    final String id = v.getId();
    return client.set(id, v);
  }

  @Override
  public MediaObject getMedia(final String id) throws CouchException
  {
    final String videoId;
    if (id.startsWith("video::")) {
      videoId = id;
    } else {
      videoId = "video::" + id;
    }
    return client.get(videoId, MediaObject.class);
  }

  @Override
  public MediaObject getMediaByUUID(final String uuid) throws CouchException
  {
    final Query q = new Query()
            .setIncludeDocs(true)
            .setStale(Stale.FALSE)
            .setKey(ComplexKey.of(uuid))
            .setLimit(1);
    final List<MediaObject> list = client.queryViewDocs(VIDEO_DESIGN, VIDEO_UUID_VIEW, q, MediaObject.class);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public MediaObject getMediaByExternalId(final String externalId) throws CouchException {
    final Query q = new Query()
            .setIncludeDocs(true)
            .setStale(Stale.FALSE)
            .setKey(ComplexKey.of(externalId))
            .setLimit(1);
    final List<MediaObject> list = client.queryViewDocs(VIDEO_DESIGN, VIDEO_EXTERNALID_VIEW, q, MediaObject.class);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
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
