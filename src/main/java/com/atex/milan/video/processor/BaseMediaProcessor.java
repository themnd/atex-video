package com.atex.milan.video.processor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.atex.milan.video.data.DataType;
import com.atex.milan.video.data.FormatInfo;
import com.atex.milan.video.data.Media;
import com.atex.milan.video.data.StreamInfo;
import com.atex.milan.video.data.MediaObject;
import com.atex.milan.video.data.VideoInfo;
import com.atex.milan.video.processor.templates.BatchTemplate;
import com.atex.milan.video.processor.templates.ImageTemplate;
import com.atex.milan.video.processor.templates.VideoTemplate;
import com.atex.milan.video.resolver.MediaFileResolver;
import com.atex.milan.video.template.TemplateService;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.polopoly.cm.client.CMException;
import com.polopoly.common.lang.StringUtil;

/**
 * Base video conversion.
 *
 * @author mnova
 */
public abstract class BaseMediaProcessor extends BaseVideoProcessor {
    private static final Logger LOGGER = Logger.getLogger(BaseMediaProcessor.class.getName());

    private static final String[] TAGNAMES = {
            "codec_name",
            "codec_long_name",
            "width",
            "height",
            "display_aspect_ratio",
            "duration_ts",
            "bit_rate",
            "channels",
            "sample_rate"
    };

    @Inject
    private TemplateService templateService;

    @Inject
    private MediaFileResolver mediaFileResolver;

    public TemplateService getTemplateService() {
        return templateService;
    }

    public MediaFileResolver getMediaFileResolver() {
        return mediaFileResolver;
    }

    protected boolean isShort(final VideoInfo videoInfo) {
        if (videoInfo != null) {
            final FormatInfo formatInfo = videoInfo.getFormatInfo();
            if (formatInfo != null) {
                final String duration = formatInfo.getDuration();
                try {
                    final double durationSecs = Double.parseDouble(duration);
                    final int configuredDuration = Integer.parseInt(getServiceProperties().getProperty("ffmpeg.thumb.short.length"));
                    return durationSecs < configuredDuration;
                } catch (NumberFormatException e) {
                    LOGGER.severe("Cannot parse: " + duration);
                }
            }
        }
        return false;
    }

    protected int convertVideo(final File in, final File out) throws Exception {

        final int exitValue;
        final File tmpFile = new File(out.getParentFile(), "tmp." + out.getName());
        try {
            exitValue = getVideoConverter().convert(in, tmpFile);
            if (exitValue == 0) {
                return getVideoConverter().qtFastStart(tmpFile, out);
            }
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
        return exitValue;
    }

    protected int convertAudio(final File in, final File out) throws Exception {

    /*
    final int exitValue;
    final File tmpFile = new File(out.getParentFile(), "tmp." + out.getName());
    try {
      exitValue = getVideoConverter().convertAudio(in, tmpFile);
      if (exitValue == 0) {
        return getVideoConverter().qtFastStart(tmpFile, out);
      }
    } finally {
      if (tmpFile.exists()) {
        tmpFile.delete();
      }
    }
    */
        return getVideoConverter().convertAudio(in, out);
    }

    protected VideoInfo createMediaInfo(final File f) throws Exception {
        final FormatInfo fi = new FormatInfo();

        final Map<String, Object> info = getVideoConverter().extractVideoInfo(f);

        {
            final Map<String, String> map = (Map<String, String>) info.get("format");
            if (map != null) {
                fi.setName(map.get("format_name"));
                fi.setLongname(map.get("format_long_name"));
                fi.setDuration(map.get("duration"));
            }
        }

        final List<StreamInfo> streams = Lists.newArrayList();

        {
            final List<Map<String, Object>> list = (List<Map<String, Object>>) info.get("streams");
            if (list != null) {
                for (final Map<String, Object> map : list) {

                    final StreamInfo si = new StreamInfo();
                    streams.add(si);

                    if (map.get("codec_type") != null) {
                        si.setType((String) map.get("codec_type"));
                    }

                    for (final String n : TAGNAMES) {
                        final Object value = map.get(n);
                        if (value != null) {
                            si.getData().put(n, map.get(n));
                        }
                    }
                }
            }
        }

        final VideoInfo vi = new VideoInfo();
        vi.setFormatInfo(fi);
        vi.setStreams(streams);
        return vi;
    }

    protected File createPolopolyContentXML(final MediaObject video) throws CMException, IOException {
        if (video.getType().equals(DataType.VIDEO)) {
            return createPolopolyVideoContentXML(video);
        } else {
            return createPolopolyAudioContentXML(video);
        }
    }

    protected File createPolopolyVideoContentXML(final MediaObject video) throws CMException, IOException {

        final String videoId = checkNotNull(video.getId()).toLowerCase().replace("video::", "");
        final String videoServerUrl = checkNotNull(getServiceProperties().getProperty("video.server.url"));

        final String videoExternalId = Optional.fromNullable(video.getExternalId()).or("media.video." + videoId);
        final String imageExternalId = "media.image." + videoId;

        final VideoTemplate videoTemplate = new VideoTemplate();
        videoTemplate.setInputTemplate(getServiceProperties().getProperty("default.video.inputTemplate"));
        if (video.getPublished() > 0) {
            videoTemplate.setPublicationDate(Long.toString(video.getPublished()));
        }
        videoTemplate.setExternalId(videoExternalId);
        videoTemplate.setContentId(video.getContentId());
        videoTemplate.setSiteId(getSecurityParentId(video));
        videoTemplate.setTitle(video.getTitle());
        videoTemplate.setVideoUUID(video.getUuid());
        videoTemplate.setVideoId(video.getId());
        if (!Strings.isNullOrEmpty(videoId)) {
            videoTemplate.setUrl(videoServerUrl + "/video-server/media/video/" + videoId + ".mp4");
            videoTemplate.setImageUrl(videoServerUrl + "/video-server/media/video/" + videoId + ".jpg");
        }
        if (video.getPublished() > 0) {
            videoTemplate.setPublicationDate(Long.toString(video.getPublished()));
        }
        setVideoDuration(videoTemplate, video);
        videoTemplate.setThumbExternalId(imageExternalId);
        videoTemplate.setImageExternalId(imageExternalId);

        final ImageTemplate imageTemplate = new ImageTemplate();
        imageTemplate.setInputTemplate(getServiceProperties().getProperty("default.image.inputTemplate"));
        imageTemplate.setExternalId(imageExternalId);
        imageTemplate.setSiteId(getSecurityParentId(video));
        imageTemplate.setTitle(video.getTitle());
        imageTemplate.setUrl(videoServerUrl + "/video-server/media/video/" + videoId + ".jpg");

        final String polopolyInbox = getInboxDirectory();

        final String imageXml = templateService.execute("/mustache/image.template.hbs", imageTemplate);
        final String videoXml = templateService.execute("/mustache/video.template.hbs", videoTemplate);

        final BatchTemplate batchTemplate = new BatchTemplate();
        batchTemplate.setContent(imageXml + "\n" + videoXml);
        return saveTemplate(new File(polopolyInbox, makeNameCompliant(videoTemplate.getExternalId(), ".xml")),
                "/mustache/batch.template.hbs", batchTemplate);
    }

    protected File createPolopolyAudioContentXML(final MediaObject video) throws CMException, IOException {

        final String videoId = checkNotNull(video.getId()).toLowerCase().replace("video::", "");
        final String videoServerUrl = checkNotNull(getServiceProperties().getProperty("video.server.url"));

        final String videoExternalId = Optional.fromNullable(video.getExternalId()).or("media.video." + videoId);

        final VideoTemplate videoTemplate = new VideoTemplate();
        if (video.getPublished() > 0) {
            videoTemplate.setPublicationDate(Long.toString(video.getPublished()));
        }
        videoTemplate.setExternalId(videoExternalId);
        videoTemplate.setContentId(video.getContentId());
        videoTemplate.setSiteId(getSecurityParentId(video));
        videoTemplate.setTitle(video.getTitle());
        videoTemplate.setVideoUUID(video.getUuid());
        videoTemplate.setVideoId(video.getId());
        if (!Strings.isNullOrEmpty(videoId)) {
            videoTemplate.setUrl(videoServerUrl + "/video-server/media/video/" + videoId + ".mp3");
        }
        if (video.getPublished() > 0) {
            videoTemplate.setPublicationDate(Long.toString(video.getPublished()));
        }
        setAudioDuration(videoTemplate, video);

        final String polopolyInbox = getInboxDirectory();

        final String videoXml;
        if (Strings.isNullOrEmpty(video.getContentId())) {
            videoXml = templateService.execute("/mustache/ath.audio.template.hbs", videoTemplate);
        } else {
            videoXml = templateService.execute("/mustache/ath.audio.upload.template.hbs", videoTemplate);
        }

        final BatchTemplate batchTemplate = new BatchTemplate();
        batchTemplate.setContent(videoXml);
        return saveTemplate(new File(polopolyInbox, makeNameCompliant(videoTemplate.getExternalId(), ".xml")),
                "/mustache/batch.template.hbs", batchTemplate);
    }

    protected String getSecurityParentId(final MediaObject video) {
        return Optional.fromNullable(video.getSecurityParentId())
                       .or(Optional.fromNullable(getSecurityParentFromSiteCode(video)))
                       .or(getDefaultSecurityParentId());
    }

    protected String getSecurityParentFromSiteCode(final MediaObject video) {
        if (video != null) {
            final String siteCode = video.getSiteCode();
            if (siteCode != null) {
                return siteCode + ".site.d";
            }
        }
        return null;
    }

    protected String getDefaultSecurityParentId() {
        return getServiceProperties().getProperty("default.securityParentId");
    }

    protected String makeNameCompliant(final String name, final String extension) {
        return name.replaceAll("\\.", "_") + extension;
    }

    protected File saveTemplate(final File f, final String templateName, final Object scope) throws IOException {
        final String xml = templateService.execute(templateName, scope);
        LOGGER.info(xml);
        Files.write(xml, f, Charset.forName("UTF-8"));
        return f;
    }

    private boolean hasThumbnailMedia(final MediaObject video) {
        for (final Media media : video.getMedia()) {
            if (media.getType().equals(MediaType.THUMB.getMediaType())) {
                return true;
            }
        }
        return false;
    }

    protected String getInboxDirectory() {
        return checkNotNull(getServiceProperties().getProperty("polopoly.inbox"));
    }

    protected File getArchiveDirForOriginal(final Date origDate) {
        final File archiveDir = new File(getServiceProperties().getProperty("video.repository.archive"));
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        final File outDir = new File(archiveDir, dateFormat.format(origDate));
        outDir.mkdirs();
        return outDir;
    }

    protected Media copyOriginalToArchive(final File original, final String videoId, final Date origDate,
                                          final boolean doCopy) throws IOException {
        final File outDir = getArchiveDirForOriginal(origDate);

        final String extension = FilenameUtils.getExtension(original.getName());
        final String outFilename = String.format("%s.%s", videoId, extension);
        final File out = new File(outDir, outFilename);

        if (doCopy) {
            FileUtils.copyFile(original, out);
        } else {
            Files.move(original, out);
        }

        final String videoPath = mediaFileResolver.makeRelative(out);

        final Media media = new Media();
        media.setPath(videoPath);
        media.setType(MediaType.ORIGINAL.getMediaType());
        media.setExtension(extension);
        media.setSize(out.length());
        return media;
    }

    protected Media copyOriginalToArchive(final File original, final String videoId, final Date origDate)
            throws IOException {
        return copyOriginalToArchive(original, videoId, origDate, true);
    }

    protected Media moveOriginalToArchive(final File original, final String videoId, final Date origDate)
            throws IOException {
        return copyOriginalToArchive(original, videoId, origDate, false);
    }

    protected void setVideoDuration(final VideoTemplate videoTemplate, final MediaObject video) {
        if (video.getMedia().size() > 0) {
            for (final Media media : video.getMedia()) {
                if (StringUtil.equals(media.getType(), MediaType.VIDEO.getMediaType())) {
                    if (media.getVideoInfo() != null && media.getVideoInfo().getFormatInfo() != null) {
                        videoTemplate.setDuration(media.getVideoInfo().getFormatInfo().getDuration());
                    }
                }
            }
        }
    }

    protected void setAudioDuration(final VideoTemplate videoTemplate, final MediaObject video) {
        if (video.getMedia().size() > 0) {
            for (final Media media : video.getMedia()) {
                if (StringUtil.equals(media.getType(), MediaType.AUDIO.getMediaType())) {
                    if (media.getVideoInfo() != null && media.getVideoInfo().getFormatInfo() != null) {
                        videoTemplate.setDuration(media.getVideoInfo().getFormatInfo().getDuration());
                    }
                }
            }
        }
    }

}
