package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static io.jenkins.plugins.propelo.job_reporter.extensions.PropeloJobReporterConfiguration.CONFIGURATION;

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

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD2"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD2);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-HARNESS-PROD4"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD4);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-HARNESS-EU1"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_EU1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownApplicationTypeThrows() {
        ApplicationType.fromString("SEI-Legacy-US");
    }
}
