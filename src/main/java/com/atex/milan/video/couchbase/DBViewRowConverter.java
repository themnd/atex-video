package com.atex.milan.video.couchbase;

import com.couchbase.client.protocol.views.ViewRow;
import com.google.gson.Gson;

/**
 * Utility interface used to convert a query result.
 *
 * @author mnova
 */
public interface DBViewRowConverter<T>
{
  /**
   * Convert the given row.
   *
   * @param gson an already initialized Gson instance.
   * @param row the row to be converted.
   * @return
   */
  T convertRow(final Gson gson, final ViewRow row);
}
