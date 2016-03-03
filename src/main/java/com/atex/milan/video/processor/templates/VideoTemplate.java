package com.atex.milan.video.processor.templates;

import com.google.common.base.Strings;

/**
 * Video template.
 *
 * @author mnova
 */
public class VideoTemplate {

    private String externalId;
    private String contentId;
    private String siteId;
    private String inputTemplate;
    private String videoId;
    private String videoUUID;
    private String title;
    private String description;
    private String byline;
    private String publicationDate;
    private String url;
    private String imageExternalId;
    private String imageUrl;
    private String section;
    private String expiryDate;
    private String thumbExternalId;
    private String thumbName;
    private String thumbCaption;
    private String uploadDate;
    private Boolean enabledComments;
    private String categorization;
    private String duration;
    private Boolean publishToPolopoly;
    private String contentDataBeanType = "com.atex.plugins.video.VideoContentDataBean";

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(final String contentId) {
        this.contentId = contentId;
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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(final String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUUID() {
        return videoUUID;
    }

    public void setVideoUUID(final String videoUUID) {
        this.videoUUID = videoUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(final String byline) {
        this.byline = byline;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(final String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public boolean hasPublicationDate() {
        return !Strings.isNullOrEmpty(publicationDate);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getImageExternalId() {
        return imageExternalId;
    }

    public void setImageExternalId(final String imageExternalId) {
        this.imageExternalId = imageExternalId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean hasImage() {
        return !Strings.isNullOrEmpty(imageExternalId);
    }

    public String getSection() {
        return section;
    }

    public void setSection(final String section) {
        this.section = section;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getThumbExternalId() {
        return thumbExternalId;
    }

    public void setThumbExternalId(final String thumbExternalId) {
        this.thumbExternalId = thumbExternalId;
    }

    public String getThumbName() {
        return thumbName;
    }

    public void setThumbName(final String thumbName) {
        this.thumbName = thumbName;
    }

    public String getThumbCaption() {
        return thumbCaption;
    }

    public void setThumbCaption(final String thumbCaption) {
        this.thumbCaption = thumbCaption;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(final String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Boolean isEnabledComments() {
        return enabledComments;
    }

    public void setEnabledComments(final Boolean enabledComments) {
        this.enabledComments = enabledComments;
    }

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(final String categorization) {
        this.categorization = categorization;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public Boolean getPublishToPolopoly() {
        return publishToPolopoly;
    }

    public void setPublishToPolopoly(final Boolean publishToPolopoly) {
        this.publishToPolopoly = publishToPolopoly;
    }

    public String getContentDataBeanType() {
        return contentDataBeanType;
    }

    public void setContentDataBeanType(final String contentDataBeanType) {
        this.contentDataBeanType = contentDataBeanType;
    }
}
