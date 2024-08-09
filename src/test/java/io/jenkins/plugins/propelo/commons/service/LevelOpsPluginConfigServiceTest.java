package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;

import static io.jenkins.plugins.propelo.commons.models.PropeloJobReporterConfiguration.CONFIGURATION;

public class LevelOpsPluginConfigServiceTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void testApplicationType() throws Exception {

        PropeloPluginImpl propeloPlugin = new PropeloPluginImpl();

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD1"));
        ApplicationType applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD1);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD3"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD3);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Legacy"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Legacy-EU"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY_EU);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD2"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD2);
    }
}
