package com.atex.milan.video.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

/**
 * MetricsUtil
 *
 * @author mnova
 */
public abstract class MetricsUtil
{
  static public Timer createTimer(final Object o, final String prefix, final String name)
  {
    return createTimer(o.getClass(), prefix, name);
  }

  static public Timer createTimer(final Class<?> c, final String prefix, final String name)
  {
    return Metrics.newTimer(
            new MetricName(c, name, prefix),
            TimeUnit.MILLISECONDS,
            TimeUnit.MILLISECONDS);
  }

  static public <T> T timedExecution(final Timer timer, final Function<T> f) throws IOException, ServletException
  {
    final TimerContext context = timer.time();
    try {
      return f.apply();
    } finally {
      if (context != null) {
        context.stop();
      }
    }
  }

  static public <T> T timedExecution(final Function<Timer> ft, final Function<T> f) throws IOException, ServletException
  {
    return timedExecution(ft.apply(), f);
  }

  public interface Function<T>
  {
    T apply() throws IOException, ServletException;
  }


}
