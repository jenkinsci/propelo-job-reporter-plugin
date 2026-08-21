package io.jenkins.plugins.propelo.commons.service;


import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JobRunGitChangesServiceTest {
    @Test
    public void test() throws URISyntaxException, IOException {
        JobRunGitChangesService jobRunGitChangesService = new JobRunGitChangesService(null, null);
        File testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/1").toURI());
        Assert.assertEquals(Arrays.asList("changelog.xml"), names(jobRunGitChangesService.getChangeLogFiles(testDir)));

        testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/2").toURI());
        Assert.assertEquals(Arrays.asList("changelog1044854799909823881.xml"), names(jobRunGitChangesService.getChangeLogFiles(testDir)));

        testDir = new File(this.getClass().getClassLoader().getResource("git_changes_dir/3").toURI());
        Assert.assertEquals(Arrays.asList("changelog.xml", "changelog1044854799909823881.xml"),
                names(jobRunGitChangesService.getChangeLogFiles(testDir)));

        testDir = new File(new File(this.getClass().getClassLoader().getResource("git_changes_dir").toURI()), "doesNotExist");
        Assert.assertTrue(jobRunGitChangesService.getChangeLogFiles(testDir).isEmpty());

        testDir = null;
        Assert.assertTrue(jobRunGitChangesService.getChangeLogFiles(testDir).isEmpty());

        testDir = Files.createTempDirectory("empty").toFile();
        Assert.assertTrue(jobRunGitChangesService.getChangeLogFiles(testDir).isEmpty());
        testDir.delete();
    }

    @Test
    public void shouldAccumulateCommitIdsFromAllChangelogsAndSkipEmptyFile() throws URISyntaxException {
        JobRunGitChangesService jobRunGitChangesService = new JobRunGitChangesService(null, null);
        File testDir = new File(this.getClass().getClassLoader().getResource("git_change_logs_multi").toURI());

        List<String> fileNames = names(jobRunGitChangesService.getChangeLogFiles(testDir));
        Assert.assertEquals(Arrays.asList(
                "changelog15309913775270793127.xml",
                "changelog8352143283638003279.xml",
                "changelog9191194392632421749.xml"), fileNames);

        List<String> commitIds = jobRunGitChangesService.parseGitCommitsFromBuildDirectory(testDir);
        Assert.assertEquals(Arrays.asList(
                "37482a7fcf68e1181fe3b999da2372aa1d85220a",
                "08d386c3b05997a04bf1ffdc0948af0f44b5bd2d",
                "2c257ff889e91316bdf9ae5ce384cedde80b9ee2"), commitIds);
    }

    private static List<String> names(List<File> files) {
        return files.stream().map(File::getName).collect(Collectors.toList());
    }

}
