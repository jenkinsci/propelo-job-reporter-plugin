package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class LevelOpsPluginConfigServiceTest {
    @Test
    public void testApplicationType() throws Exception {

        LevelOpsPluginConfigService levelOpsPluginConfigService = new LevelOpsPluginConfigService();

        File file = new File("src/test/resources/propelo-job-reporter_1.xml");
        ApplicationType applicationType = levelOpsPluginConfigService.getApplicationType(file);
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS);

        file = new File("src/test/resources/propelo-job-reporter_2.xml");
        applicationType = levelOpsPluginConfigService.getApplicationType(file);
        Assert.assertEquals(applicationType, ApplicationType.SEI_HARNESS_COMPLIANCE);

        file = new File("src/test/resources/propelo-job-reporter_3.xml");
        applicationType = levelOpsPluginConfigService.getApplicationType(file);
        Assert.assertEquals(applicationType, ApplicationType.SEI_LEGACY);
    }
}
