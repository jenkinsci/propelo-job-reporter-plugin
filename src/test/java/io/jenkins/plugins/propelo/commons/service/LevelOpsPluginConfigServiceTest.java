package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static io.jenkins.plugins.propelo.job_reporter.extensions.PropeloJobReporterConfiguration.CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithJenkins
class LevelOpsPluginConfigServiceTest {

    private JenkinsRule r;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void testApplicationType() {
        PropeloPluginImpl propeloPlugin = new PropeloPluginImpl();

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD1"));
        ApplicationType applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        assertEquals(ApplicationType.SEI_HARNESS_PROD1, applicationType);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD3"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        assertEquals(ApplicationType.SEI_HARNESS_PROD3, applicationType);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Legacy"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        assertEquals(ApplicationType.SEI_LEGACY, applicationType);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Legacy-EU"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        assertEquals(ApplicationType.SEI_LEGACY_EU, applicationType);

        CONFIGURATION.setApplicationType(ApplicationType.fromString("SEI-Harness-PROD2"));
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        assertEquals(ApplicationType.SEI_HARNESS_PROD2, applicationType);
    }
}
