package com.atex.milan.video.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

/**
 * Common functionality for the xml parsers.
 *
 * @author mnova
 */
public abstract class AbstractParser {

    static final Logger LOGGER = Logger.getLogger(AbstractParser.class.getName());

    protected Boolean parseBoolean(final String value) {
        if (value != null) {
            // we use lowercase because in python true is True.
            return Boolean.valueOf(value.toLowerCase().trim());
        }
        return null;
    }

    protected Date parseDate(final String value) {

        if (!Strings.isNullOrEmpty(value)) {

            try {
                final long ts = Long.parseLong(value);
                final Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setLenient(false);
                calendar.setTimeInMillis(ts);
                return calendar.getTime();
            } catch (NumberFormatException e) {

                final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

                // we want to get errors if the date format
                // is not what we expect.

                fmt.setLenient(false);

                try {
                    return fmt.parse(value);
                } catch (ParseException e2) {
                    LOGGER.log(Level.WARNING, "Cannot parse date '" + value + "': " + e2.getMessage());
                }

            }
        }

        return null;
    }

    protected String getChildValue(final Node element, final String name) {
        final Node node = getChildElement(element, name);
        if (node != null) {
            final Node textNode = node.getLastChild();
            if (textNode != null) {
                return textNode.getTextContent();
            }
        }
        return null;
    }

    protected Node getChildElement(final Node element, final String name) {
        final NodeList nodeList = element.getChildNodes();
        for (int idx = 0; idx < nodeList.getLength(); idx++) {
            final Node node = nodeList.item(idx);
            if (node.getNodeType() == Element.ELEMENT_NODE) {
                if (node.getNodeName().equals(name)) {
                    return node;
                }
            }
        }
        return null;
    }
}
