package com.atex.milan.video.couchbase;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.exceptions.CouchException;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * DBClientImpl
 *
 * @author mnova
 */
public class DBClientImpl implements DBClient
{
  private static final Logger logger = LoggerFactory.getLogger(DBClientImpl.class);

  final private List<URI> hosts;

  final private String couchBucket;

  final private String couchPwd;
  
  private CouchbaseClient client = null;

  @Inject
  public DBClientImpl(
          @Named("couchbase.pools")
          final String couchPools,

          @Named("couchbase.bucket")
          final String couchBucket,

          @Named("couchbase.pwd")
          final String couchPwd)
  {
    hosts = Lists.newArrayList();
    for (final String uri : Splitter.on("|").omitEmptyStrings().split(couchPools)) {
      try {
        hosts.add(new URI(uri));
      } catch (URISyntaxException e) {
        logger.error("{} is not a URI: {}", uri, e.getMessage());
      }
    }
    this.couchBucket = couchBucket;
    this.couchPwd = Strings.nullToEmpty(couchPwd);
  }

  public List<URI> getHosts()
  {
    return hosts;
  }

  public String getCouchBucket()
  {
    return couchBucket;
  }

  public String getCouchPwd()
  {
    return couchPwd;
  }

  public CouchbaseClient getClient() throws IOException
  {
    if (client == null) {
      client = new CouchbaseClient(getHosts(), getCouchBucket(), getCouchPwd());
    }
    return client;
  }

  @Override
  public void init() throws CouchException
  {
    try {
      getClient();
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
      throw new CouchException(e);
    }
  }

  @Override
  public void shutdown()
  {
    // Shutting down properly
    if (client != null) {
      client.shutdown();
    }
  }

  @Override
  public boolean set(final String id, final Object o) throws CouchException
  {
    final Gson gson = new Gson();

    return Optional.fromNullable(wrapCouchCall(new CouchFunction<Boolean>()
    {
      @Override
      public Boolean apply(final CouchbaseClient c) throws Exception
      {
        return c.set(id, gson.toJson(o)).get();
      }
    })).or(false);
  }

  @Override
  public Object get(final String id) throws CouchException
  {
    return wrapCouchCall(new CouchFunction<Object>()
    {
      @Override
      public Object apply(final CouchbaseClient c) throws Exception
      {
        return c.get(id);
      }

    });
  }

  @Override
  public <T> T get(final String id, final Class<T> c) throws CouchException
  {
    final Object o = get(id);
    if (o != null) {
      final Gson gson = new Gson();
      return gson.fromJson((String) o, c);
    }
    return null;
  }

  @Override
  public boolean add(final String id, final Object o) throws CouchException
  {
    final Gson gson = new Gson();

    return Optional.fromNullable(wrapCouchCall(new CouchFunction<Boolean>()
    {
      @Override
      public Boolean apply(final CouchbaseClient c) throws Exception
      {
        return c.add(id, gson.toJson(o)).get();
      }
    })).or(false);
  }

  @Override
  public long incr(final String id) throws CouchException
  {
    return wrapCouchCall(new CouchFunction<Long>()
    {
      @Override
      public Long apply(final CouchbaseClient c) throws Exception
      {
        return c.incr(id, 1);
      }
    });
  }

  @Override
  public <T> List<T> queryView(final String designName, final String viewName, final Query q, final DBViewRowConverter<T> r) throws CouchException
  {
    checkNotNull(designName);
    checkNotNull(viewName);
    checkNotNull(q);
    checkNotNull(r);

    final Gson gson = new Gson();

    return wrapCouchCall(new CouchFunction<List<T>>()
    {
      @Override
      public List<T> apply(final CouchbaseClient c) throws Exception
      {
        final List<T> list = Lists.newArrayList();
        final View view = c.getView(designName, viewName);
        final ViewResponse results = c.query(view, q);
        for (final ViewRow row : results) {
          final T o = r.convertRow(gson, row);
          if (o != null) {
            list.add(o);
          }
        }
        return list;
      }
    });
  }

  @Override
  public <T> List<T> queryViewDocs(final String designName, final String viewName, final Query q, final Class<T> c) throws CouchException
  {
    checkNotNull(q);
    checkArgument(q.willIncludeDocs());

    return queryView(designName, viewName, q, new DBViewRowConverter<T>()
    {
      @Override
      public T convertRow(final Gson gson, final ViewRow row)
      {
        return gson.fromJson((String)row.getDocument(), c);
      }
    });
  }

  @Override
  public boolean createDesignDoc(final DesignDocument doc) throws CouchException
  {
    checkNotNull(doc);

    return wrapCouchCall(new CouchFunction<Boolean>()
    {
      @Override
      public Boolean apply(final CouchbaseClient c) throws Exception
      {
        return Optional.fromNullable(c.createDesignDoc(doc)).or(false);
      }
    });
  }

  private <T> T wrapCouchCall(final CouchFunction<T> f) throws CouchException
  {
    try {
      return f.apply(getClient());
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
      throw new CouchException(e);
    }
  }

  private interface CouchFunction<T>
  {
    public T apply(final CouchbaseClient c) throws Exception;
  }
}
