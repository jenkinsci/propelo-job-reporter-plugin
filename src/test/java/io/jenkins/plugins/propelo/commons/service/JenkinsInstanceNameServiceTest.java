package io.jenkins.plugins.propelo.commons.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JenkinsInstanceNameServiceTest {

    @Test
    void testCreateOrReturnInstanceName() throws IOException {
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("testCreateOrReturnInstanceName").toFile();
            JenkinsInstanceNameService service = new JenkinsInstanceNameService(tempDir);
            for (int i = 0; i < 10; i++) {
                String e = "Jenkisns US" + i;
                String a = service.createOrUpdateInstanceName(e);
                assertEquals(e, a);
            }
        } finally {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir);
            }
        }
    }

    @Test
    void testCreateOrReturnInstanceNameNullOrEmpty() throws IOException {
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("testCreateOrReturnInstanceNameNullOrEmpty").toFile();
            JenkinsInstanceNameService service = new JenkinsInstanceNameService(tempDir);
            assertNull(service.createOrUpdateInstanceName(null));
            assertEquals("", service.createOrUpdateInstanceName(""));
        } finally {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir);
            }
        }
    }

}