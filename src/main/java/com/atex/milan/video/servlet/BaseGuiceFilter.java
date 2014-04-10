package com.atex.milan.video.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public abstract class BaseGuiceFilter implements Filter
{
  private static final Logger logger = LoggerFactory.getLogger(BaseGuiceFilter.class);
  
  private Injector injector;
  private ServletContext servletContext;
  
  @Override
  public void init(final FilterConfig config) throws ServletException
  {
    logger.info("init {}", this.getClass().getName());

    servletContext = config.getServletContext();
    
    injector = (Injector) servletContext.getAttribute(Injector.class.getName());
    injector.injectMembers(this);
  }
  
  @Override
  public void destroy()
  {
    logger.info("destroy {}", this.getClass().getName());
  }

  protected ServletContext getServletContext()
  {
    return servletContext;
  }

  /**
   * Inject object members.
   *
   * @param object
   * @return the object itself.
   */
  public <T extends Object> T injectMembers(final T object)
  {
    injector.injectMembers(object);
    return object;
  }

}
