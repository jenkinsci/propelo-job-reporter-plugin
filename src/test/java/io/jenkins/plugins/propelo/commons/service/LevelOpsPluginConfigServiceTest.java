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

        propeloPlugin.setApplicationType("SEI-Harness");
        ApplicationType applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS);

        propeloPlugin.setApplicationType("SEI-Harness-Compliance");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_COMPLIANCE);

        propeloPlugin.setApplicationType("SEI-Legacy");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY);

        propeloPlugin.setApplicationType("SEI-Legacy-EU");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY_EU);

        propeloPlugin.setApplicationType("SEI-Legacy-ASIA");
        applicationType = PropeloPluginImpl.getInstance().getApplicationType();
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY_ASIA);
    }
}
