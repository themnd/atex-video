package com.atex.milan.video.servlet.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.exceptions.CouchException;
import com.atex.milan.video.guice.ConfigureModule;
import com.atex.milan.video.util.InjectorUtils;
import com.google.inject.Module;
import com.polopoly.application.Application;
import com.polopoly.application.ApplicationStatusReporter;
import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.LegacyDaemonThreadsStopper;
import com.polopoly.application.StandardApplication;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.client.HttpCmClientHelper;
import com.polopoly.cm.client.HttpFileServiceClient;

public class VideoContextListener implements ServletContextListener
{
  static final Logger LOGGER = Logger.getLogger(VideoContextListener.class.getName());

  private Application application;

  @Override
  public void contextInitialized(final ServletContextEvent event)
  {
    LOGGER.info("init context");

    final ServletContext sc = event.getServletContext();

    try {
      application = new StandardApplication(ApplicationServletUtil.getApplicationName(sc));
      application.setManagedBeanRegistry(ApplicationServletUtil.getManagedBeanRegistry());
      ConnectionProperties connectionProperties = ApplicationServletUtil.getConnectionProperties(sc);
      CmClientBase cmClient = HttpCmClientHelper.createAndAddToApplication(application, connectionProperties);

      this.application.addApplicationComponent(new HttpFileServiceClient());

      // Reports status back to the cm server
      application.addApplicationComponent(new ApplicationStatusReporter(cmClient));

      // Sync cache.
      // LRUSynchronizedUpdateCache syncCache = new LRUSynchronizedUpdateCache();
      // application.addApplicationComponent(syncCache);

      // Read connection properties.
      application.readConnectionProperties(connectionProperties);

      // Init.
      application.init();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException(e);
    }

    // Put in global scope.
    ApplicationServletUtil.setApplication(sc, application);

    final InjectorUtils injectorUtils = InjectorUtils.getInstance();
    injectorUtils.initInjector(sc, new Module[] { new ConfigureModule(sc) });

    try {
      final VideoRepository repository = InjectorUtils.getInstance().newInstance(VideoRepository.class);
      repository.initRepository();
    } catch (CouchException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event)
  {
    LOGGER.info("destroying context");

    final VideoRepository repository = InjectorUtils.getInstance().newInstance(VideoRepository.class);
    repository.shutdown();

    final ServletContext sc = event.getServletContext();

    // Remove from global scope.
    ApplicationServletUtil.setApplication(sc, null);

    // Destroy.
    application.destroy();

    LegacyDaemonThreadsStopper.stopStaticDaemons();

    InjectorUtils.getInstance().removeInjector(sc);
  }

}
