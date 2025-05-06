package io.jenkins.plugins.propelo.commons.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobNameDetailsTest {
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    @Test
    void test() throws IOException {
        JobNameDetails jobNameDetails = new JobNameDetails("leetcode2", "master", "BBMaven1New/jobs/leetcode2/branches/master", "module-name", "BBMaven1New/leetcode2/master");
        String serialized = MAPPER.writeValueAsString(jobNameDetails);
        JobNameDetails actual = MAPPER.readValue(serialized, JobNameDetails.class);
        assertEquals(jobNameDetails, actual);
    }

}