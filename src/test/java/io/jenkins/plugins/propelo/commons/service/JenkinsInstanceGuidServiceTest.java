package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.service.JenkinsInstanceGuidService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import static io.jenkins.plugins.propelo.commons.plugins.Common.JENKINS_INSTANCE_GUID_FILE;
import static io.jenkins.plugins.propelo.commons.plugins.Common.UTF_8;

public class JenkinsInstanceGuidServiceTest {

    @Test
    public void testCreateOrReturnInstanceGuidNewInstall() throws IOException {
        File expandedLevelOpsPluginDir = null;
        File dataDirectory = null;
        File dataDirectoryWithVersion = null;
        try {
            expandedLevelOpsPluginDir = Files.createTempDirectory("testUnzip").toFile();
            dataDirectory = new File(expandedLevelOpsPluginDir, "data");
            dataDirectoryWithVersion = new File(expandedLevelOpsPluginDir, "data-with-version");

            JenkinsInstanceGuidService service = new JenkinsInstanceGuidService(expandedLevelOpsPluginDir, dataDirectory, dataDirectoryWithVersion);
            String guid = service.createOrReturnInstanceGuid();
            Assert.assertTrue(StringUtils.isNotBlank(guid));
            for(int i=0; i < 10; i++){
                Assert.assertEquals(guid, service.createOrReturnInstanceGuid());
            }
        } finally {
            if (expandedLevelOpsPluginDir != null){
                FileUtils.deleteDirectory(expandedLevelOpsPluginDir);
            }
            if (dataDirectory != null){
                FileUtils.deleteDirectory(dataDirectory);
            }
            if (dataDirectoryWithVersion != null){
                FileUtils.deleteDirectory(dataDirectoryWithVersion);
            }
        }
    }

    @Test
    public void testCreateOrReturnInstanceGuidExistingDataDirectory() throws IOException {
        File jenkinsInstanceGuidFileInDataDirectory = null;
        File expandedLevelOpsPluginDir = null;
        File dataDirectory = null;
        File dataDirectoryWithVersion = null;
        try {
            expandedLevelOpsPluginDir = Files.createTempDirectory("testUnzip").toFile();
            dataDirectory = new File(expandedLevelOpsPluginDir, "data");
            dataDirectoryWithVersion = new File(expandedLevelOpsPluginDir, "data-with-version");

            String existingGuid = UUID.randomUUID().toString();
            io.jenkins.plugins.propelo.commons.utils.FileUtils.createDirectoryRecursively(dataDirectory);
            jenkinsInstanceGuidFileInDataDirectory = new File(dataDirectory, JENKINS_INSTANCE_GUID_FILE);
            Files.write(jenkinsInstanceGuidFileInDataDirectory.toPath(), existingGuid.getBytes(UTF_8), StandardOpenOption.CREATE);

            JenkinsInstanceGuidService service = new JenkinsInstanceGuidService(expandedLevelOpsPluginDir, dataDirectory, dataDirectoryWithVersion);
            String guid = service.createOrReturnInstanceGuid();
            Assert.assertEquals(existingGuid, guid);
            File jenkinsInstanceGuidFileInExpandedPluginPath = new File(expandedLevelOpsPluginDir, JENKINS_INSTANCE_GUID_FILE);
            Assert.assertTrue(jenkinsInstanceGuidFileInExpandedPluginPath.exists());
            Assert.assertEquals(existingGuid, new String(Files.readAllBytes(jenkinsInstanceGuidFileInExpandedPluginPath.toPath()), UTF_8));
            for(int i=0; i < 10; i++){
                Assert.assertEquals(guid, service.createOrReturnInstanceGuid());
            }
        } finally {
            if (expandedLevelOpsPluginDir != null){
                FileUtils.deleteDirectory(expandedLevelOpsPluginDir);
            }
            if (dataDirectory != null){
                FileUtils.deleteDirectory(dataDirectory);
            }
            if (dataDirectoryWithVersion != null){
                FileUtils.deleteDirectory(dataDirectoryWithVersion);
            }
        }
    }

    @Test
    public void testCopyJenkinsInstanceGuidFileToDataDirectoryWithVersionNewInstall() throws IOException {
        File expandedLevelOpsPluginDir = null;
        File dataDirectory = null;
        File dataDirectoryWithVersion = null;
        try {
            expandedLevelOpsPluginDir = Files.createTempDirectory("testUnzip").toFile();
            dataDirectory = new File(expandedLevelOpsPluginDir, "data");
            dataDirectoryWithVersion = new File(expandedLevelOpsPluginDir, "data-with-version");

            JenkinsInstanceGuidService service = new JenkinsInstanceGuidService(expandedLevelOpsPluginDir, dataDirectory, dataDirectoryWithVersion);
            String guid = service.createOrReturnInstanceGuid();
            Assert.assertTrue(StringUtils.isNotBlank(guid));

            for(int i=0; i<10; i++) {
                boolean success = service.copyJenkinsInstanceGuidFileToDataDirectoryWithVersion();
                Assert.assertTrue(success);
                File jenkinsInstanceFileInDataDirWithVersion = new File(dataDirectoryWithVersion, JENKINS_INSTANCE_GUID_FILE);
                Assert.assertTrue(jenkinsInstanceFileInDataDirWithVersion.exists());
                Assert.assertEquals(guid, new String(Files.readAllBytes(jenkinsInstanceFileInDataDirWithVersion.toPath()), UTF_8));
            }
        } finally {
            if (expandedLevelOpsPluginDir != null){
                FileUtils.deleteDirectory(expandedLevelOpsPluginDir);
            }
            if (dataDirectory != null){
                FileUtils.deleteDirectory(dataDirectory);
            }
            if (dataDirectoryWithVersion != null){
                FileUtils.deleteDirectory(dataDirectoryWithVersion);
            }
        }
    }

    @Test
    public void testCopyJenkinsInstanceGuidFileToDataDirectoryWithVersionExistingDataDirectory() throws IOException {
        File jenkinsInstanceGuidFileInDataDirectory = null;
        File expandedLevelOpsPluginDir = null;
        File dataDirectory = null;
        File dataDirectoryWithVersion = null;
        try {
            expandedLevelOpsPluginDir = Files.createTempDirectory("testUnzip").toFile();
            dataDirectory = new File(expandedLevelOpsPluginDir, "data");
            dataDirectoryWithVersion = new File(expandedLevelOpsPluginDir, "data-with-version");

            String existingGuid = UUID.randomUUID().toString();
            io.jenkins.plugins.propelo.commons.utils.FileUtils.createDirectoryRecursively(dataDirectory);
            jenkinsInstanceGuidFileInDataDirectory = new File(dataDirectory, JENKINS_INSTANCE_GUID_FILE);
            Files.write(jenkinsInstanceGuidFileInDataDirectory.toPath(), existingGuid.getBytes(UTF_8), StandardOpenOption.CREATE);

            JenkinsInstanceGuidService service = new JenkinsInstanceGuidService(expandedLevelOpsPluginDir, dataDirectory, dataDirectoryWithVersion);
            String guid = service.createOrReturnInstanceGuid();
            Assert.assertEquals(existingGuid, guid);

            File jenkinsInstanceGuidFileInExpandedPluginPath = new File(expandedLevelOpsPluginDir, JENKINS_INSTANCE_GUID_FILE);
            Assert.assertTrue(jenkinsInstanceGuidFileInExpandedPluginPath.exists());
            Assert.assertEquals(existingGuid, new String(Files.readAllBytes(jenkinsInstanceGuidFileInExpandedPluginPath.toPath()), UTF_8));

            for(int i=0; i<10; i++) {
                boolean success = service.copyJenkinsInstanceGuidFileToDataDirectoryWithVersion();
                Assert.assertTrue(success);
                File jenkinsInstanceFileInDataDirWithVersion = new File(dataDirectoryWithVersion, JENKINS_INSTANCE_GUID_FILE);
                Assert.assertTrue(jenkinsInstanceFileInDataDirWithVersion.exists());
                Assert.assertEquals(existingGuid, new String(Files.readAllBytes(jenkinsInstanceFileInDataDirWithVersion.toPath()), UTF_8));
            }
        } finally {
            if (expandedLevelOpsPluginDir != null){
                FileUtils.deleteDirectory(expandedLevelOpsPluginDir);
            }
            if (dataDirectory != null){
                FileUtils.deleteDirectory(dataDirectory);
            }
            if (dataDirectoryWithVersion != null){
                FileUtils.deleteDirectory(dataDirectoryWithVersion);
            }
        }
    }

}