package com.atex.milan.video.servlet.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.exceptions.CouchException;
import com.atex.milan.video.guice.ConfigureModule;
import com.atex.milan.video.util.InjectorUtils;
import com.google.inject.Module;

public class VideoContextListener implements ServletContextListener
{
  static final private Logger logger = LoggerFactory.getLogger(VideoContextListener.class);

  @Override
  public void contextInitialized(final ServletContextEvent event)
  {
    logger.info("init context");

    initLogging();

    final ServletContext context = event.getServletContext();

    final InjectorUtils injectorUtils = InjectorUtils.getInstance();
    injectorUtils.initInjector(context,
            new Module[]{
                    new ConfigureModule()
            }
    );

    try {
      final VideoRepository repository = InjectorUtils.getInstance().newInstance(VideoRepository.class);
      repository.initRepository();
    } catch (CouchException e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  private void initLogging()
  {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event)
  {
    logger.info("destroying context");

    final VideoRepository repository = InjectorUtils.getInstance().newInstance(VideoRepository.class);
    repository.shutdown();

    final ServletContext servletContext = event.getServletContext();
    InjectorUtils.getInstance().removeInjector(servletContext);
  }

}
