package com.atex.milan.video.processor;

/**
 * MediaType.
 *
 * @author mnova
 */
public enum MediaType {

    ORIGINAL("original", ""),

    VIDEO("mp4", "mp4"),

    THUMB("thumb", "jpg"),

    AUDIO("audio", "mp3"),;

    private final String mediaType;
    private final String extension;

    MediaType(final String mediaType, final String extension) {
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return extension;
    }

}
