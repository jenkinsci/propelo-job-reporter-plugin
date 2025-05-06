package io.jenkins.plugins.propelo.commons.service;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JobRunGitChangesServiceTest {

    @Test
    void test() throws URISyntaxException, IOException {
        JobRunGitChangesService jobRunGitChangesService = new JobRunGitChangesService(null, null);
        File testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/1").toURI());
        assertEquals("changelog.xml", jobRunGitChangesService.getChangeLogFile(testDir).getName());

        testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/2").toURI());
        assertEquals("changelog1044854799909823881.xml", jobRunGitChangesService.getChangeLogFile(testDir).getName());

        testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/3").toURI());
        assertEquals("changelog.xml", jobRunGitChangesService.getChangeLogFile(testDir).getName());

        testDir = new File(new File(this.getClass().getClassLoader().getResource("git_changes_dir").toURI()), "doesNotExist");
        assertNull(jobRunGitChangesService.getChangeLogFile(testDir));

        testDir = null;
        assertNull(jobRunGitChangesService.getChangeLogFile(testDir));

        testDir = Files.createTempDirectory("empty").toFile();
        assertNull(jobRunGitChangesService.getChangeLogFile(testDir));
        testDir.delete();
    }

}