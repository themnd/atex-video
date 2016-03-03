package com.atex.milan.video.processor.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.milan.video.util.ServiceProperties;

@RunWith(MockitoJUnitRunner.class)
public class NamePropertiesBuilderTest {

    private Random rnd = new Random(new Date().getTime());

    private NamePropertiesBuilder builder;

    @Before
    public void before() throws IOException {
        final ServiceProperties serviceProperties = new ServiceProperties();
        builder = new NamePropertiesBuilder(serviceProperties);
    }

    @Test
    public void nameWithFullDate() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + "_" + title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameWithShortDate() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + "_" + title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameWithWrongDate() {
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);
        final String fileName = "20150133" + "_" + title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        Assert.assertNull(p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameNoDate() {
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);
        final String fileName = title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        Assert.assertNull(p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameNoSeparator() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameMultipleSeparator() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String title = createRandomTitle();
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + " --__-- " + title + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertEquals(title, p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameNoTitleWithSeparator() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + " --__-- " + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertNull(p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void nameNoTitleNoSeparator() {
        final Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.HOUR);
        final String ext = RandomStringUtils.randomAlphabetic(3);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final String fileName = dateFormat.format(c.getTime()) + "." + ext;

        builder.setFileName(fileName);

        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertEquals(fileName, p.getName());
        compareDates(c, p.getPublishDate());
        Assert.assertNull(p.getTitle());
        Assert.assertEquals(ext, p.getExtension());
    }

    @Test
    public void invalidName() {
        final NameProperties p = builder.build();
        Assert.assertNotNull(p);

        Assert.assertNull(p.getName());
        Assert.assertNull(p.getPublishDate());
        Assert.assertNull(p.getTitle());
        Assert.assertNull(p.getExtension());
    }

    private void compareDates(final Calendar expected, final Date givenDate) {
        final Calendar given = Calendar.getInstance();
        given.setTime(givenDate);
        Assert.assertEquals(expected.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        Assert.assertEquals(expected.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        Assert.assertEquals(expected.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
    }

    private String createRandomTitle()
    {
        int numWords = rnd.nextInt(3);
        String title = RandomStringUtils.randomAlphanumeric(5);
        while (numWords-- > 0) {
            title += " " + RandomStringUtils.randomAlphanumeric(2 + rnd.nextInt(10));
        }
        return title;
    }

}