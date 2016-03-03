package com.atex.milan.video.template;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.milan.video.processor.templates.ImageTemplate;
import com.atex.milan.video.processor.templates.VideoTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceImplTest {

    private TemplateService service = new TemplateServiceImpl();

    @Test
    public void testSimpleScope() throws IOException {
        final String templateName = "/mustache/simpleScope.hbs";
        final Map<String, String> scope = Maps.newHashMap();
        scope.put("name", "marco");

        final String result = service.execute(templateName, scope);
        Assert.assertEquals("Hello marco", result);
    }

    @Test
    public void testComplexScope() throws IOException {
        final String templateName = "/mustache/complexScope.hbs";
        final List<User> userList = Lists.newArrayList();
        userList.add(new User("user1", "user1@email.it"));
        userList.add(new User("user2", "user2@email.it"));
        userList.add(new User("user3", "user3@email.it"));

        final Object o = new Object() {
            final String name = "arena";
            final List<User> users = userList;
        };

        final String result = service.execute(templateName, o);
        final String expected = "Hello arena\n" +
                "<b>user1-user1@email.it</b>\n" +
                "<b>user2-user2@email.it</b>\n" +
                "<b>user3-user3@email.it</b>\n";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testVideoScopeWithComments() throws IOException {
        final String templateName = "/mustache/video.template.hbs";
        final VideoTemplate videoTemplate = new VideoTemplate();
        videoTemplate.setExternalId(UUID.randomUUID().toString());
        videoTemplate.setSiteId(RandomStringUtils.randomAlphabetic(10));
        //videoTemplate.setInputTemplate(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setVideoId(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setVideoUUID(UUID.randomUUID().toString());
        videoTemplate.setTitle(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setSection(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setPublicationDate(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setExpiryDate(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setUploadDate(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setUrl(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setImageExternalId(UUID.randomUUID().toString());
        videoTemplate.setImageUrl(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setThumbName(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setThumbCaption(RandomStringUtils.randomAlphabetic(10));
        videoTemplate.setEnabledComments(true);

        final String result = service.execute(templateName, videoTemplate);

        Assert.assertTrue(result, result.contains(videoTemplate.getExternalId()));
        Assert.assertTrue(result, result.contains(videoTemplate.getSiteId()));
        //Assert.assertTrue(result, result.contains(videoTemplate.getInputTemplate()));
        Assert.assertTrue(result, result.contains(videoTemplate.getVideoId()));
        Assert.assertTrue(result, result.contains(videoTemplate.getVideoUUID()));
        Assert.assertTrue(result, result.contains(videoTemplate.getTitle()));
        Assert.assertTrue(result, result.contains(videoTemplate.getPublicationDate()));
        //Assert.assertTrue(result, result.contains(videoTemplate.getExpiryDate()));
        Assert.assertTrue(result, result.contains(videoTemplate.getUrl()));
        Assert.assertTrue(result, result.contains(videoTemplate.getImageExternalId()));
        Assert.assertTrue(result, result.contains(videoTemplate.getImageUrl()));
    }

    @Test
    public void testVideoScopeWithSingleQuotes() throws IOException {
        final String templateName = "/mustache/video.template.hbs";
        final VideoTemplate videoTemplate = new VideoTemplate();

        final String name = "\\Ferrari/ 'sensazionale' di \"martedì\"";
        videoTemplate.setTitle(name);

        final String result = service.execute(templateName, videoTemplate);

        Assert.assertTrue(result, result.contains("\\Ferrari/ &#39;sensazionale&#39; di &quot;martedì&quot;"));
    }

    @Test
    public void testImageScopeWithSingleQuotes() throws IOException {
        final String templateName = "/mustache/image.template.hbs";
        final ImageTemplate template = new ImageTemplate();

        final String name = "\\Ferrari/ 'sensazionale' <di> \"martedì\"";
        template.setTitle(name);

        final String result = service.execute(templateName, template);
        System.out.println(result);

        Assert.assertFalse(result, result.contains("<di>"));
        Assert.assertFalse(result, result.contains("\"martedì\""));
        Assert.assertFalse(result, result.contains("'sensazionale'"));
    }

    private class User {
        private String name;
        private String email;

        public User(final String name, final String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    private class UserList {
        private List<User> users = Lists.newArrayList();

        public List<User> getUsers() {
            return users;
        }
    }

}