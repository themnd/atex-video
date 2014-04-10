package com.atex.milan.video.exceptions;

/**
 * Exception throws when handling with couchbase operations.
 *
 * @author mnova
 */
public class CouchException extends Exception
{
  public CouchException(final Throwable e)
  {
    super(e);
  }

  public CouchException(final String m)
  {
    super(m);
  }

}
