package com.atex.milan.video.processor;

import java.io.File;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

/**
 * VideoCleanupProcessor.
 */
public class VideoCleanupProcessor extends BaseVideoProcessor {

    @Override
    public void process(final Exchange exchange) throws Exception {

        final Message originalMessage = exchange.getIn();

        final Map<String, Object> headers = originalMessage.getHeaders();

        final String name = (String) headers.get("CamelFileName");

        final String path = (String) headers.get("CamelFileAbsolutePath");

        final String videoId = getVideoId(originalMessage);

        final File filePath = new File(path);
        if (filePath.getParentFile().exists()) {
            filePath.getParentFile().delete();
        }

    }
}
