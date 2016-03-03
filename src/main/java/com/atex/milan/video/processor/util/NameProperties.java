package com.atex.milan.video.processor.util;

import java.util.Date;

import com.google.common.base.Objects;

/**
 * Video properties.
 *
 * @author mnova
 */
public class NameProperties {

    /**
     * Publish date
     */
    private Date publishDate;

    /**
     * Title
     */
    private String title;

    /**
     * File name
     */
    private String name;

    /**
     * File extension
     */
    private String extension;

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(final Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("publishDate", publishDate)
                      .add("title", title)
                      .add("name", name)
                      .add("extension", extension)
                      .toString();
    }
}
