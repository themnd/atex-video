package com.atex.milan.video.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Guice injector helper util.
 */
public final class InjectorUtils
{
    private static final Logger LOGGER = Logger.getLogger(InjectorUtils.class.getName());

    private static InjectorUtils injectorUtils = new InjectorUtils();

    private Injector injector;

    private InjectorUtils() {
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

    public void initInjector(final ServletContext servletContext, final Module[] modules) {
        Injector servletInjector = (Injector) servletContext.getAttribute(Injector.class.getName());
        if (servletInjector == null) {
            LOGGER.info("new injector");
            servletInjector = Guice.createInjector(modules);
        } else {
            LOGGER.info("new child injector");
            servletInjector = servletInjector.createChildInjector(modules);
        }
        servletContext.setAttribute(Injector.class.getName(), servletInjector);
        setInjector(servletInjector);
    }

    public void removeInjector(final ServletContext servletContext) {
        setInjector(null);
        servletContext.removeAttribute(Injector.class.getName());
    }

    public void injectMembers(final Object object) {
        checkNotNull(injector).injectMembers(object);
    }

    public <T> T newInstance(final Class<T> c) {
        return checkNotNull(injector).getInstance(c);
    }

}
