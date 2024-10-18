package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.JobRunCompleteData;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.service.JobLogsService;
import io.jenkins.plugins.propelo.commons.service.JobRunCompleteNotificationService;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.jenkins.plugins.propelo.commons.plugins.Common.API_URL_LOCAL;
import static io.jenkins.plugins.propelo.commons.utils.JobUtils.writeLogData;

public class JobRunLogsNotificationServiceTest {
    private static final String EXPECTED_STRING = "This is the temporary log content";
    private static final String separator = System.getProperty("line.separator");

    @Test
    public void testWriteLogData() throws IOException {
        File currentJobRunCompleteDataDirectory = null;
        try {
            currentJobRunCompleteDataDirectory = Files.createTempDirectory("path").toFile();
            UUID uuidOfFileCreated = writeLogData(currentJobRunCompleteDataDirectory, "Temp data");
            Assert.assertNotNull(uuidOfFileCreated);
        } finally {
            if ((currentJobRunCompleteDataDirectory != null) && (currentJobRunCompleteDataDirectory.exists())) {
                currentJobRunCompleteDataDirectory.delete();
            }
        }
    }

    @Ignore
    @Test
    public void testSubmit() throws IOException {
        List<String> productIds = new ArrayList<>();
        productIds.add("71");
        List<String> scmCommitIds = new ArrayList<>();
        scmCommitIds.add("scm-commit-id-1");
        File completeDataZipFile = null;
        File tempFailedLogFile;
        try {
            completeDataZipFile = Files.createTempFile("tmp", ".zip").toFile();
            tempFailedLogFile = writeIntoFile();
            JobLogsService jobLogsService = new JobLogsService();
            String apiKey = "eyJrZXkiOiJqYyRDWkdqY2dqcEx1elNMQjg3MlV1S1d5OTRiWTR1T2EyaTVZc3lYNSZ0MlB3R1kkSyIsImlkIjoiMTY4MGQ3MGQtYzQ2MS00ZWFlLTgxYzYtYjk3ODBlMzRmOGFhIiwiY29tcGFueSI6InNjbXRlbmFudCJ9";
            List<JobRunParam> jobRunParams = new ArrayList<>();
            jobRunParams.add(new JobRunParam("StringParameterValue", "env_name", "UAT"));
            jobRunParams.add(new JobRunParam("TextParameterValue", "boker_ids", "broker1\nbroker2\nbroker3"));
            JobRunDetail jobRunDetail = new JobRunDetail("Sample Pipeline 1", jobRunParams, "sampleUser", System.currentTimeMillis(),
                    "FAILURE", 1234L, 9, null, null, "Sample Pipeline 1", null, "Sample Pipeline 1", null);
            JobRun jobRun = new JobRun();
            JobRunCompleteData jobRunCompleteData = new JobRunCompleteData(jobRun, null, completeDataZipFile);
            JobRunCompleteNotificationService jobRunCompleteNotificationService = new JobRunCompleteNotificationService(API_URL_LOCAL, JsonUtils.buildObjectMapper());
            List<String> runIds = jobRunCompleteNotificationService.submitJobRunCompleteRequest(apiKey, jobRunDetail,
                    "https://github.com/testadmin1-levelops/openapi-generator.git", null, UUID.randomUUID().toString(),
                    "Jenkins US1", "https://jenkins.dev.levelops.io/", false, jobRunCompleteData, scmCommitIds, null, null);
            Assert.assertNotNull(runIds);
        } finally {
            if((completeDataZipFile != null) && (completeDataZipFile.exists())) {
                completeDataZipFile.delete();
            }
        }
    }


    private File writeIntoFile() throws IOException {
        File tempFile = Files.createTempFile("log", null).toFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            bw.append(EXPECTED_STRING);
            bw.append(separator);
        }
        return tempFile;
    }

}
