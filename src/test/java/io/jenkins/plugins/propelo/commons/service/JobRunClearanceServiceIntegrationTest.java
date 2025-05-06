package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.jenkins.plugins.propelo.commons.plugins.Common.API_URL_LOCAL;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobRunClearanceServiceIntegrationTest {

    @Disabled
    @Test
    void testSubmit() throws IOException {
        String apiKey = "eyJrZXkiOiJGakhiQHo3czV0cV9NempQcVFuZ0BzXkFedll1MG9hM19rSnBxaTMmZjhDSTFRWlp3OCIsImlkIjoiNDBlNThjNWMtYjJmOS00NjY0LWFmNDYtOGU0ZDlhN2YyZjVlIiwiY29tcGFueSI6ImZvbyJ9";
        List<JobRunParam> jobRunParams = new ArrayList<>();
        jobRunParams.add(new JobRunParam("StringParameterValue", "env_name", "UAT"));
        jobRunParams.add(new JobRunParam("TextParameterValue", "boker_ids", "broker1\nbroker2\nbroker3"));
        JobRunDetail jobRunDetail = new JobRunDetail("Pipe 1", jobRunParams, "viraj", System.currentTimeMillis(),
                "SUCCESS", 1234L, 9, null, null, "Pipe 1", null, "Pipe 1", null);

        JobRunClearanceService jobRunClearanceService = new JobRunClearanceService(API_URL_LOCAL, JsonUtils.buildObjectMapper());
        List<String> runIds = jobRunClearanceService.submitJobRunClearanceRequest(apiKey, jobRunDetail, "https://github.com/testadmin1-levelops/openapi-generator.git", null, UUID.randomUUID().toString(), "Jenkins US1", false, null);
        assertNotNull(runIds);
    }
}