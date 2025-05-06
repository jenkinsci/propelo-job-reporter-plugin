package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobRunClearanceCheckTest {
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    @Test
    void test() throws JsonProcessingException {
        List<JobRunParam> params = new ArrayList<>();
        params.add(new JobRunParam("String", "key", "val"));
        JobRunClearanceCheck jobRunClearanceCheck = new JobRunClearanceCheck("Pipe 1", "viraj-jenkins", params, "https://github.com/testadmin1-levelops/openapi-generator.git", null, UUID.randomUUID().toString(), "Jenkins US1", null);
        String payload = MAPPER.writeValueAsString(jobRunClearanceCheck);
        assertNotNull(payload);
    }

}