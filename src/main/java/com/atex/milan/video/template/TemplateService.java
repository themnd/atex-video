package com.atex.milan.video.template;

import java.io.IOException;
import java.io.Writer;

/**
 * A templating service for simple template rendering.
 *
 * @author mnova
 */
public interface TemplateService {

    Writer execute(final Writer writer, final String templateName, final Object scope) throws IOException;

    Writer execute(final Writer writer, final String templateName, final Object[] scopes) throws IOException;

    String execute(final String templateName, final Object scope) throws IOException;

    String execute(final String templateName, final Object[] scopes) throws IOException;

}
