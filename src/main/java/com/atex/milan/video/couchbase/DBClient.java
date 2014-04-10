package com.atex.milan.video.couchbase;

import com.atex.milan.video.exceptions.CouchException;

/**
 * Client for accessing the couchbase data.
 *
 * @author mnova
 */
public interface DBClient
{
  /**
   * Initialize the client.
   *
   * @throws CouchException
   */
  void init()  throws CouchException;

  /**
   * Shutdown the client.
   */
  void shutdown();

  /**
   * Add a new object to the repository.
   *
   * @param id
   * @param o
   * @return
   * @throws CouchException
   */
  boolean add(final String id, final Object o) throws CouchException;

  /**
   * Set an object to the given id.
   *
   * @param id
   * @param o
   * @return true if the set succeeded
   * @throws CouchException
   */
  public boolean set(final String id, final Object o) throws CouchException;

  /**
   * Get the object from the given id.
   *
   * @param id
   * @return null if not found.
   * @throws CouchException
   */
  public Object get(final String id) throws CouchException;

  /**
   * Get the object from the given id.
   *
   * @param id
   * @param c
   * @param <T>
   * @return null if not found.
   * @throws CouchException
   */
  public <T> T get(final String id, final Class<T> c) throws CouchException;

  /**
   * Increment by 1 the given counter key.
   *
   * @param id
   * @return the incremented value
   * @throws CouchException
   */
  long incr(String id) throws CouchException;
}
