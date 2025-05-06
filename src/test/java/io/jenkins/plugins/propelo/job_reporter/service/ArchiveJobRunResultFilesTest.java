package io.jenkins.plugins.propelo.job_reporter.service;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

class ArchiveJobRunResultFilesTest {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Test
    void test() throws URISyntaxException, IOException {
        URL testFileUrl = this.getClass().getClassLoader().getResource("test-file.txt");
        File testFile = new File(testFileUrl.toURI());
        LOGGER.log(Level.SEVERE, "{0}", testFile.getName());
        LOGGER.log(Level.SEVERE, "{0}", FilenameUtils.removeExtension(testFile.getName()));
        LOGGER.log(Level.SEVERE, "{0}", FilenameUtils.getExtension(testFile.getName()));
        LOGGER.log(Level.SEVERE, "{0}", testFile.getAbsolutePath());
        LOGGER.log(Level.SEVERE, "{0}", testFile.getCanonicalPath());
    }
}