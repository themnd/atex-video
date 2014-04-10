package com.atex.milan.video.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.milan.video.converter.VideoConverter;
import com.atex.milan.video.converter.VideoConverterImpl;
import com.atex.milan.video.couchbase.DBClient;
import com.atex.milan.video.couchbase.DBClientImpl;
import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.couchbase.VideoRepositoryImpl;
import com.atex.milan.video.util.ServiceProperties;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * Video Module
 *
 * @author mnova
 */
public class ConfigureModule extends AbstractModule
{
  private static final Logger logger = LoggerFactory.getLogger(ConfigureModule.class.getName());

  @Override
  protected void configure()
  {
    try {

      final ServiceProperties serviceProperties = new ServiceProperties();
      bind(ServiceProperties.class).toInstance(serviceProperties);

      Names.bindProperties(binder(), serviceProperties);

      bind(VideoConverter.class).to(VideoConverterImpl.class);
      bind(DBClient.class).to(DBClientImpl.class).in(Scopes.SINGLETON);
      bind(VideoRepository.class).to(VideoRepositoryImpl.class).in(Scopes.SINGLETON);

    } catch (final Throwable e) {
      logger.error("Cannot initialize GUICE: {}", e.getMessage(), e);
      throw new RuntimeException("Cannot initialize GUICE: " + e.getMessage(), e);
    }
  }
}
