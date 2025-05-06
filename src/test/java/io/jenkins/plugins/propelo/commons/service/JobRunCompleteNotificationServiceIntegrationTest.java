package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.JobRunCompleteData;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.jenkins.plugins.propelo.commons.plugins.Common.API_URL_LOCAL;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobRunCompleteNotificationServiceIntegrationTest {

    @Disabled
    @Test
    void testSubmit() throws IOException {
        List<String> productIds = new ArrayList<>();
        productIds.add("71");
        List<String> scmCommitIds = new ArrayList<>();
        scmCommitIds.add("scm-commit-id-1");
        File completeDataZipFile = null;
        try {
            completeDataZipFile = File.createTempFile("tmp", ".zip");
            String apiKey = "eyJrZXkiOiJGakhiQHo3czV0cV9NempQcVFuZ0BzXkFedll1MG9hM19rSnBxaTMmZjhDSTFRWlp3OCIsImlkIjoiNDBlNThjNWMtYjJmOS00NjY0LWFmNDYtOGU0ZDlhN2YyZjVlIiwiY29tcGFueSI6ImZvbyJ9";
            List<JobRunParam> jobRunParams = new ArrayList<>();
            jobRunParams.add(new JobRunParam("StringParameterValue", "env_name", "UAT"));
            jobRunParams.add(new JobRunParam("TextParameterValue", "boker_ids", "broker1\nbroker2\nbroker3"));
            JobRunDetail jobRunDetail = new JobRunDetail("Pipe 1", jobRunParams, "viraj", System.currentTimeMillis(),
                    "SUCCESS", 1234L, 9, null, null, "Pipe 1", null, "Pipe 1", null);
            JobRun jobRun = new JobRun();
            JobRunCompleteData jobRunCompleteData = new JobRunCompleteData(jobRun, null, completeDataZipFile);
            JobRunCompleteNotificationService jobRunCompleteNotificationService = new JobRunCompleteNotificationService(API_URL_LOCAL, JsonUtils.buildObjectMapper());
            List<String> runIds = jobRunCompleteNotificationService.submitJobRunCompleteRequest(apiKey, jobRunDetail,
                    "https://github.com/testadmin1-levelops/openapi-generator.git", null, UUID.randomUUID().toString(),
                    "Jenkins US1", "https://jenkins.dev.levelops.io/", false, jobRunCompleteData, scmCommitIds, null, null);
            assertNotNull(runIds);
        } finally {
            if ((completeDataZipFile != null) && (completeDataZipFile.exists())) {
                completeDataZipFile.delete();
            }
        }
    }
}