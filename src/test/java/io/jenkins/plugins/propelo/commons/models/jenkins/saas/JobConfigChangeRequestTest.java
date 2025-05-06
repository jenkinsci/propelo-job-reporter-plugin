package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.JobConfigChangeType;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobConfigChangeRequestTest {
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    @Test
    void test() throws JsonProcessingException {
        JobConfigChangeRequest test = new JobConfigChangeRequest("pipeline-1", "master", null, "pipeline-1/branches/master", "pipeline-1/master",
                "abcd", "abcd", "url", null, null, JobConfigChangeType.CHANGED, 100L, "v", "v");
        String data = MAPPER.writeValueAsString(test);
        assertNotNull(data);
    }
}