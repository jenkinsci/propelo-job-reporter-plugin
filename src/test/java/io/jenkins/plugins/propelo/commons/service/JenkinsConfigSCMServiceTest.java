package io.jenkins.plugins.propelo.commons.service;

import com.google.common.base.Optional;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JenkinsConfigSCMServiceTest {

    @Test
    void testSCMUserRemoteConfig() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/SCMUserRemoteConfig.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("https://virajajgaonkar@bitbucket.org/virajajgaonkar/leetcode.git", result.get().getUrl());
        assertNull(result.get().getUserName());
    }

    @Test
    void testBitbucketSCMNavigator() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/BitbucketSCMNavigator.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("https://bitbucket.org", result.get().getUrl());
        assertEquals("virajajgaonkar", result.get().getUserName());
    }

    @Test
    void testGithubProjectProperty() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/GithubProjectProperty.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("https://github.com/AnamikaN/WarpService.git", result.get().getUrl());
        assertNull(result.get().getUserName());
    }

    @Test
    void testGitHubPushTrigger() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/GitHubPushTrigger.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("https://github.com/TechPrimers/jenkins-example.git", result.get().getUrl());
        assertNull(result.get().getUserName());
    }

    @Test
    void testBitbucketSCMNavigator3() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/Stream-Maven-2.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("https://github.com/virajajgaonkar/LeetCode.git", result.get().getUrl());
        assertNull(result.get().getUserName());
    }

    @Test
    void testPerforceScmUrl() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
        File configFile = new File(this.getClass().getClassLoader().getResource("configs_scm/PerforceConfig.xml").toURI());
        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        Optional<JenkinsConfigSCMService.SCMResult> result = jenkinsConfigSCMService.parseSCMData(configFile);
        assertTrue(result.isPresent());
        assertEquals("sandbox", result.get().getUrl());
        assertNull(result.get().getUserName());
    }
}