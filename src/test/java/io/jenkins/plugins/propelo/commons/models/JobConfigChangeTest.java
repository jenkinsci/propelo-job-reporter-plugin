package io.jenkins.plugins.propelo.commons.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobConfigChangeTest {
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    @Test
    void test() throws IOException {
        JobNameDetails jobNameDetails = new JobNameDetails("pipeline-1", "master", "pipeline-1/branches/master", null, "pipeline-1/master");
        JobConfigChange jobConfigChange = new JobConfigChange(jobNameDetails, JobConfigChangeType.CHANGED, System.currentTimeMillis(), "viraj", "Viraj Ajgaonkar");
        String text = MAPPER.writeValueAsString(jobConfigChange);
        assertNotNull(text);
    }
}