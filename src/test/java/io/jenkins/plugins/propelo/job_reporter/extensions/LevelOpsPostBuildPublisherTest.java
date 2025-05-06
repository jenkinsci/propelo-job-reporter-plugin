package io.jenkins.plugins.propelo.job_reporter.extensions;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LevelOpsPostBuildPublisherTest {

    @Test
    void cleanProjectNameTest() {
        List<String> projectNames = Arrays.asList(
                ".Test",
                "..Test",
                "../.Test",
                "../Test",
                "../../../Test",
                "../../../..Test"
        );
        for (String projectName : projectNames) {
            assertEquals("_Test", LevelOpsPostBuildPublisher.sanatizeFileName(projectName));
        }
    }
}
