package com.atex.milan.video.couchbase;

import com.atex.milan.video.exceptions.CouchException;

/**
 * DBClient
 *
 * @author mnova
 */
public interface DBClient
{
  void init()  throws CouchException;
  void shutdown();

  public boolean set(final String id, final Object o) throws CouchException;
  public Object get(final String id) throws CouchException;
  public <T> T get(final String id, final Class<T> c) throws CouchException;

}
