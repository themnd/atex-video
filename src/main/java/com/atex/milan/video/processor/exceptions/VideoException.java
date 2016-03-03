package com.atex.milan.video.processor.exceptions;

/**
 * Simple general exception.
 *
 * @author mnova
 */
public class VideoException extends Exception {

    public VideoException(final String message) {
        super(message);
    }

    public VideoException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
