package com.atex.milan.video.couchbase;

import java.util.List;

import com.atex.milan.video.exceptions.CouchException;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.Query;

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
  long incr(final String id) throws CouchException;

  /**
   * Return a results list based on a query on a view.
   * The client will iterate over the results set and convert the document from json using the provided r function.
   *
   * @param designName the design name
   * @param viewName the view name
   * @param q the query
   * @param r the convert function, it it return null the result will not be added to the list.
   * @return a not null list.
   * @throws CouchException
   */
  <T> List<T> queryView(final String designName, final String viewName, final Query q, final DBViewRowConverter<T> r) throws CouchException;

  /**
   * Return a results list based on a query on a view.
   * The client will iterate over the results set and convert the document from json to the desired class (c param).
   *
   * @param designName the design name
   * @param viewName the view name
   * @param q the query must have setIncludeDocs(true).
   * @param c the class of the converted document.
   * @return a not null list.
   * @throws CouchException
   */
  <T> List<T> queryViewDocs(final String designName, final String viewName, final Query q, final Class<T> c) throws CouchException;

  /**
   * Allow you to create a Design document.
   *
   * @param doc
   * @return
   * @throws CouchException
   */
  boolean createDesignDoc(final DesignDocument doc) throws CouchException;
}
