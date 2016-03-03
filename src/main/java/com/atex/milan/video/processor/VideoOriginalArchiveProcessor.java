package com.atex.milan.video.processor;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.atex.milan.video.data.Media;

/**
 * VideoOriginalArchiveProcessor.
 */
public class VideoOriginalArchiveProcessor extends BaseMediaProcessor {

    static final Logger LOGGER = Logger.getLogger(VideoOriginalArchiveProcessor.class.getName());

    @Override
    public void process(final Exchange exchange) throws Exception {

        LOGGER.info(this.getClass().getSimpleName() + " - start work");

        final Message originalMessage = exchange.getIn();

        final Map<String, Object> headers = originalMessage.getHeaders();

        final String name = (String) headers.get("CamelFileName");

        final String path = (String) headers.get("CamelFileAbsolutePath");

        final String videoId = getVideoId(originalMessage);

        try {
            final Date origDate;
            if (headers.get(VideoConfigurationProcessor.VIDEODATE_HEADER) instanceof Date) {
                origDate = (Date) headers.get(VideoConfigurationProcessor.VIDEODATE_HEADER);
            } else {
                origDate = new Date();
            }

            final Media media = copyOriginalToArchive(new File(path), videoId, origDate);
            addMedia(originalMessage, media);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while processing " + path + ": " + e.getMessage(), e);
            throw e;
        } finally {
            LOGGER.info(this.getClass().getSimpleName() + " - end work");
        }
    }

}
