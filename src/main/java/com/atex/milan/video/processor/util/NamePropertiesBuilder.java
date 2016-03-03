package com.atex.milan.video.processor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atex.milan.video.util.ServiceProperties;
import com.google.common.base.Strings;

/**
 * Allow you to process a filename and calculate {@link com.atex.milan.video.processor.util.NameProperties}.
 *
 * @author mnova
 */
public class NamePropertiesBuilder {

    private static final Logger LOGGER = Logger.getLogger(NamePropertiesBuilder.class.getName());

    private final ServiceProperties serviceProperties;
    private String fileName;

    /**
     * Constructor.
     *
     * @param serviceProperties
     */
    public NamePropertiesBuilder(final ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    /**
     * Required: set the processed file name.
     *
     * @param name a not null String.
     * @return this.
     */
    public NamePropertiesBuilder setFileName(final String name) {
        this.fileName = name;
        return this;
    }

    /**
     * Build the {@link com.atex.milan.video.processor.util.NameProperties} object.
     *
     * @return a not null object.
     */
    public NameProperties build() {

        LOGGER.fine("processing: " + fileName);

        final NameProperties p = new NameProperties();
        if (!Strings.isNullOrEmpty(fileName)) {
            p.setName(fileName);
            final Pattern re = getNameRegex();
            final Matcher m = re.matcher(fileName);
            if (m.matches()) {
                p.setPublishDate(parsePublishDate(m.group("date")));
                p.setTitle(m.group("title"));
                p.setExtension(m.group("extension"));
            }
        }
        return p;
    }

    private Date parsePublishDate(final String value) {
        if (!Strings.isNullOrEmpty(value)) {
            final SimpleDateFormat fmt;
            if (value.length() == 8) {
                fmt = new SimpleDateFormat("yyyyMMdd");
            } else if (value.length() == 6) {
                fmt = new SimpleDateFormat("yyMMdd");
            } else {
                fmt = null;
            }
            if (fmt != null) {

                // we want to get errors if the date format
                // is not what we expect.

                fmt.setLenient(false);

                try {
                    return fmt.parse(value);
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "Cannot parse date '" + value + "': " + e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private ServiceProperties getServiceProperties() {
        return serviceProperties;
    }

    private Pattern getNameRegex() {
        final String regex = getServiceProperties().getProperty("video.name.regex");
        return Pattern.compile(regex);
    }

}
