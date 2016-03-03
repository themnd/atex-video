package com.atex.milan.video.processor;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import com.atex.milan.video.data.Media;

/**
 * AudioProcessor
 * 12/04/15 on 14:42
 *
 * @author mnova
 */
public class AudioProcessor extends BaseMediaProcessor {

    private static final Logger LOGGER = Logger.getLogger(AudioProcessor.class.getName());

    @Override
    public void process(final Exchange exchange) throws Exception {
        LOGGER.info(this.getClass().getSimpleName() + " - start work");

        final Message originalMessage = exchange.getIn();

        final Map<String, Object> headers = originalMessage.getHeaders();

        final String name = (String) headers.get("CamelFileName");

        final String path = (String) headers.get("CamelFileAbsolutePath");

        final String ts = getTimestamp(originalMessage);

        final String videoId = getVideoId(originalMessage);

        try {
            LOGGER.info("Processing file " + path);

            final String outFilename = String.format("%s_%s.%s", videoId, ts, MediaType.AUDIO.getExtension());
            final File out = new File(getVideoOutputDir(originalMessage), outFilename);
            final File in = new File(path);

            final int exitValue = convertAudio(in, out);

            LOGGER.info("exit value " + exitValue);
            if (exitValue != 0) {
                throw new Exception("Error " + exitValue);
            }

            setVideoInfoOrig(originalMessage, createMediaInfo(in));

            final String videoPath = getMediaFileResolver().makeRelative(out);

            final Media media = new Media(MediaType.AUDIO);
            media.setPath(videoPath);
            media.setSize(out.length());
            media.setVideoInfo(createMediaInfo(out));

            addMedia(originalMessage, media);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while processing " + path + ": " + e.getMessage(), e);
            throw e;
        } finally {
            LOGGER.info("Processed file " + path);
        }

    }

}
