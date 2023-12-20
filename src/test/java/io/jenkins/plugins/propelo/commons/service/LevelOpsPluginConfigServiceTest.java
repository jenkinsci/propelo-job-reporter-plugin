package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class LevelOpsPluginConfigServiceTest {
    @Test
    public void testApplicationType() throws Exception {

        PropeloPluginImpl propeloPlugin = new PropeloPluginImpl();

        propeloPlugin.setApplicationType("SEI-Harness-PROD1");
        ApplicationType applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD1);

        propeloPlugin.setApplicationType("SEI-Harness-PROD3");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD3);

        propeloPlugin.setApplicationType("SEI-Legacy");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY);

        propeloPlugin.setApplicationType("SEI-Legacy-EU");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY_EU);

        propeloPlugin.setApplicationType("SEI-Harness-PROD2");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_PROD2);
    }
}
