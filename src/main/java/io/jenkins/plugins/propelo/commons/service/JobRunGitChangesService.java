package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.Run;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.utils.FileUtils;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.jenkins.plugins.propelo.commons.plugins.Common.JOBS_DATA_DIR_NAME;
import static io.jenkins.plugins.propelo.commons.plugins.Common.RUN_GIT_CHANGES_HISTORY_FILE;
import static io.jenkins.plugins.propelo.commons.plugins.Common.UTF_8;

public class JobRunGitChangesService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final Pattern CHANGE_FILE_NAME_PATTERN = Pattern.compile("changelog.*\\.xml");
    private final ObjectMapper objectMapper;
    private final File dataDirectoryWithRotation;

    public JobRunGitChangesService(ObjectMapper objectMapper, File dataDirectoryWithRotation) {
        this.objectMapper = objectMapper;
        this.dataDirectoryWithRotation = dataDirectoryWithRotation;
    }

    protected List<File> getChangeLogFiles(File buildDirectory) {
        if (buildDirectory == null) {
            LOGGER.log(Level.FINEST, "buildDirectory is null!");
            return Collections.emptyList();
        }
        if (!buildDirectory.exists()) {
            LOGGER.log(Level.FINEST, "buildDirectory does not exist! {0}", buildDirectory.getAbsolutePath());
            return Collections.emptyList();
        }
        File[] children = buildDirectory.listFiles();
        if (children == null) {
            LOGGER.log(Level.FINEST, "buildDirectory children is null!");
            return Collections.emptyList();
        }
        List<File> changeLogFiles = new ArrayList<>();
        for (File currentChild : children) {
            if (currentChild == null || !currentChild.isFile()) {
                continue;
            }
            String fileName = currentChild.getName();
            if (StringUtils.isBlank(fileName)) {
                continue;
            }
            Matcher matcher = CHANGE_FILE_NAME_PATTERN.matcher(fileName);
            if (!matcher.matches()) {
                continue;
            }
            changeLogFiles.add(currentChild);
        }
        changeLogFiles.sort(Comparator.comparing(File::getName));
        return changeLogFiles;
    }

    public List<String> parseGitCommitsFromBuildDirectory(File buildDirectory) {
        List<File> changeLogFiles = getChangeLogFiles(buildDirectory);
        if (changeLogFiles.isEmpty()) {
            LOGGER.log(Level.FINEST, "change file not found!");
            return new ArrayList<>();
        }
        JobRunGitChangesParserService parserService = new JobRunGitChangesParserService();
        Set<String> uniqueCommitIds = new LinkedHashSet<>();
        for (File changeLogFile : changeLogFiles) {
            if (changeLogFile.length() == 0) {
                LOGGER.log(Level.FINE, "Skipping empty changelog file {0}", changeLogFile.getAbsolutePath());
                continue;
            }
            List<String> fileCommitIds = parserService.parseGitChangeCommitIds(changeLogFile);
            int added = 0;
            if (fileCommitIds != null) {
                int before = uniqueCommitIds.size();
                uniqueCommitIds.addAll(fileCommitIds);
                added = uniqueCommitIds.size() - before;
            }
            LOGGER.log(Level.FINE, "Parsed {0} commit ids from changelog {1} ({2} new)",
                    new Object[]{fileCommitIds == null ? 0 : fileCommitIds.size(), changeLogFile.getName(), added});
        }
        return new ArrayList<>(uniqueCommitIds);
    }

    public List<String> parseGitCommitsForRun(Run build, JobRunDetail jobRunDetail) {
        if (build == null) {
            LOGGER.log(Level.FINEST, "jobRunGitChangesFile is null");
            return null;
        }
        return parseGitCommitsFromBuildDirectory(build.getRootDir());
    }

    public List<String> parseAndSaveGitCommitsForRun(Run build, JobRunDetail jobRunDetail) {
        List<String> commitIds = parseGitCommitsForRun(build, jobRunDetail);
        if ((commitIds == null) || (commitIds.size() == 0)) {
            LOGGER.finest("JobRunGitChangesService changeCommitIds is null or empty!!");
            return commitIds;
        }
        File runGitChangesHistoryFile = null;
        try {
            runGitChangesHistoryFile = buildAndCreateRunGitChangesFilePath(jobRunDetail.getJobFullName());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error creating run git changes history file !!", e);
            return commitIds;
        }

        long buildNumber = build.getNumber();
        JobRunGitChanges jobRunGitChanges = new JobRunGitChanges(buildNumber, commitIds);
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(jobRunGitChanges);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.WARNING, "Error serializing run git changes!!", e);
            return commitIds;
        }
        try {
            Files.write(runGitChangesHistoryFile.toPath(), payload.getBytes(UTF_8));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error writing run git changes!!", e);
            return commitIds;
        }
        return commitIds;
    }

    private File buildAndCreateRunGitChangesFilePath(String jobFullName) throws IOException {
        File buildRunMessageFile = Paths.get(dataDirectoryWithRotation.getAbsolutePath(), JOBS_DATA_DIR_NAME, jobFullName, RUN_GIT_CHANGES_HISTORY_FILE).toFile();
        return FileUtils.createFileRecursively(buildRunMessageFile);
    }

    public static class JobRunGitChanges {
        @JsonProperty("build_number")
        private long buildNumber;
        @JsonProperty("commit_ids")
        private List<String> commitIds;

        public JobRunGitChanges() {
        }

        public JobRunGitChanges(long buildNumber, List<String> commitIds) {
            this.buildNumber = buildNumber;
            this.commitIds = commitIds;
        }

        public long getBuildNumber() {
            return buildNumber;
        }

        public void setBuildNumber(long buildNumber) {
            this.buildNumber = buildNumber;
        }

        public List<String> getCommitIds() {
            return commitIds;
        }

        public void setCommitIds(List<String> commitIds) {
            this.commitIds = commitIds;
        }
    }



}
