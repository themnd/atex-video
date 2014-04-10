package com.atex.milan.video.util;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * InjectorUtils
 * 09/04/14 on 07:51
 *
 * @author mnova
 */
public class InjectorUtils
{
  static final private Logger logger = LoggerFactory.getLogger(InjectorUtils.class);

  private static InjectorUtils injectorUtils = new InjectorUtils();
  
  private Injector injector;
  
  private InjectorUtils()
  {
  }
  
  public static InjectorUtils getInstance()
  {
    return injectorUtils;
  }

  public Injector getInjector()
  {
    return injector;
  }

  public void setInjector(final Injector injector)
  {
    this.injector = injector;
  }

  public void initInjector(final ServletContext servletContext, final Module[] modules)
  {
    Injector injector = (Injector) servletContext.getAttribute(Injector.class.getName());
    if (injector == null) {
      logger.info("new injector");
      injector = Guice.createInjector(modules);
    } else {
      logger.info("new child injector");
      injector = injector.createChildInjector(modules);
    }
    servletContext.setAttribute(Injector.class.getName(), injector);
    setInjector(injector);
  }

  public void removeInjector(final ServletContext servletContext)
  {
    setInjector(null);
    servletContext.removeAttribute(Injector.class.getName());
  }

  public void injectMembers(final Object object)
  {
    checkNotNull(injector);

    injector.injectMembers(object);
  }

  public <T> T newInstance(final Class<T> c)
  {
    checkNotNull(injector);

    return injector.getInstance(c);
  }

}
