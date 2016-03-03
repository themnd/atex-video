package com.atex.milan.video.processor.templates;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Image template.
 *
 * @author mnova
 */
public class ImageTemplate {

    private String externalId;
    private String siteId;
    private String inputTemplate = "standard.Image";
    private String contentDataBeanType = "com.atex.standard.image.ImageContentDataBean";
    private String title;
    private String description;
    private String byline;
    private String url;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    public String getInputTemplate() {
        return inputTemplate;
    }

    public void setInputTemplate(final String inputTemplate) {
        this.inputTemplate = inputTemplate;
    }

    public String getContentDataBeanType() {
        return contentDataBeanType;
    }

    public void setContentDataBeanType(final String contentDataBeanType) {
        this.contentDataBeanType = StringEscapeUtils.escapeJavaScript(contentDataBeanType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = StringEscapeUtils.escapeJavaScript(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = StringEscapeUtils.escapeJavaScript(description);
    }

    public String getByline() {
        return byline;
    }

    public void setByline(final String byline) {
        this.byline = StringEscapeUtils.escapeJavaScript(byline);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
