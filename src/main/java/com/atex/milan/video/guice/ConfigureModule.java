package com.atex.milan.video.guice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.atex.milan.video.converter.VideoConverter;
import com.atex.milan.video.converter.VideoConverterImpl;
import com.atex.milan.video.couchbase.DBClient;
import com.atex.milan.video.couchbase.DBClientImpl;
import com.atex.milan.video.couchbase.VideoRepository;
import com.atex.milan.video.couchbase.VideoRepositoryImpl;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.atex.milan.video.template.TemplateService;
import com.atex.milan.video.template.TemplateServiceImpl;
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
  private static final Logger logger = Logger.getLogger(ConfigureModule.class.getName());

  private final ServletContext servletContext;

  public ConfigureModule(final ServletContext servletContext) {
    this.servletContext = servletContext;
  }

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
      bind(MediaFileResolver.class).toInstance(new MediaFileResolver());
      bind(TemplateService.class).to(TemplateServiceImpl.class);

      /*
      bind(FileService.class).toProvider(new Provider<FileService>() {

        @Override
        public FileService get() {

          final Application application = ApplicationServletUtil.getApplication(servletContext);
          final HttpFileServiceClient fileServiceClient = (HttpFileServiceClient) application
                  .getApplicationComponent(HttpFileServiceClient.DEFAULT_COMPOUND_NAME);

          return fileServiceClient.getFileService();
        }

      });
      */

    } catch (final Throwable e) {
      logger.log(Level.SEVERE, "Cannot initialize GUICE: " + e.getMessage(), e);
      throw new RuntimeException("Cannot initialize GUICE: " + e.getMessage(), e);
    }
  }
}
