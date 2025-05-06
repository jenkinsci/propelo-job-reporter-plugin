package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.JobNameDetails;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobRunParserServiceTest {

    @Test
    void test() {
        Map<String, JobNameDetails> expectedList = new HashMap<>();
        expectedList.put("Pipe2", new JobNameDetails("Pipe2", null, "Pipe2", null, "Pipe2"));
        expectedList.put("Pipe3", new JobNameDetails("Pipe3", null, "Pipe3", null, "Pipe3"));
        expectedList.put("service-1", new JobNameDetails("service-1", null, "service-1", null, "service-1"));
        expectedList.put("service-1/branches/master", new JobNameDetails("service-1", "master", "service-1/branches/master", null, "service-1/master"));
        expectedList.put("BBMaven2", new JobNameDetails("BBMaven2", null, "BBMaven2", null, "BBMaven2"));
        expectedList.put("pipeline-2", new JobNameDetails("pipeline-2", null, "pipeline-2", null, "pipeline-2"));
        expectedList.put("pipeline-2/branches/master", new JobNameDetails("pipeline-2", "master", "pipeline-2/branches/master", null, "pipeline-2/master"));
        expectedList.put("pipeline-int-1", new JobNameDetails("pipeline-int-1", null, "pipeline-int-1", null, "pipeline-int-1"));
        expectedList.put("pipeline-int-1/branches/master", new JobNameDetails("pipeline-int-1", "master", "pipeline-int-1/branches/master", null, "pipeline-int-1/master"));
        expectedList.put("openapi-generator", new JobNameDetails("openapi-generator", null, "openapi-generator", null, "openapi-generator"));
        expectedList.put("openapi-generator/modules/com.wordnik$swagger-codegen_2.9.1", new JobNameDetails("openapi-generator", null, "openapi-generator/modules/com.wordnik$swagger-codegen_2.9.1", "com.wordnik$swagger-codegen_2.9.1", "openapi-generator/com.wordnik$swagger-codegen_2.9.1"));
        expectedList.put("Pipe1.1", new JobNameDetails("Pipe1.1", null, "Pipe1.1", null, "Pipe1.1"));
        expectedList.put("Folder1", new JobNameDetails("Folder1", null, "Folder1", null, "Folder1"));
        expectedList.put("Folder1/jobs/Folder2", new JobNameDetails("Folder2", null, "Folder1/jobs/Folder2", null, "Folder1/Folder2"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New", new JobNameDetails("BBMaven1New", null, "Folder1/jobs/Folder2/jobs/BBMaven1New", null, "Folder1/Folder2/BBMaven1New"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode", new JobNameDetails("leetcode", null, "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode", null, "Folder1/Folder2/BBMaven1New/leetcode"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode/branches/master", new JobNameDetails("leetcode", "master", "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode/branches/master", null, "Folder1/Folder2/BBMaven1New/leetcode/master"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode2", new JobNameDetails("leetcode2", null, "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode2", null, "Folder1/Folder2/BBMaven1New/leetcode2"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode2/branches/master", new JobNameDetails("leetcode2", "master", "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode2/branches/master", null, "Folder1/Folder2/BBMaven1New/leetcode2/master"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode3", new JobNameDetails("leetcode3", null, "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode3", null, "Folder1/Folder2/BBMaven1New/leetcode3"));
        expectedList.put("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode3/branches/master", new JobNameDetails("leetcode3", "master", "Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode3/branches/master", null, "Folder1/Folder2/BBMaven1New/leetcode3/master"));
        expectedList.put("pipeline-int-2", new JobNameDetails("pipeline-int-2", null, "pipeline-int-2", null, "pipeline-int-2"));
        expectedList.put("pipeline-int-2/branches/master", new JobNameDetails("pipeline-int-2", "master", "pipeline-int-2/branches/master", null, "pipeline-int-2/master"));
        expectedList.put("Stream-Maven-2", new JobNameDetails("Stream-Maven-2", null, "Stream-Maven-2", null, "Stream-Maven-2"));
        expectedList.put("pipeline-1", new JobNameDetails("pipeline-1", null, "pipeline-1", null, "pipeline-1"));
        expectedList.put("pipeline-1/branches/master", new JobNameDetails("pipeline-1", "master", "pipeline-1/branches/master", null, "pipeline-1/master"));
        expectedList.put("Stream-Maven-Delete-7", new JobNameDetails("Stream-Maven-Delete-7", null, "Stream-Maven-Delete-7", null, "Stream-Maven-Delete-7"));
        expectedList.put("BBMaven1New", new JobNameDetails("BBMaven1New", null, "BBMaven1New", null, "BBMaven1New"));
        expectedList.put("BBMaven1New/jobs/leetcode", new JobNameDetails("leetcode", null, "BBMaven1New/jobs/leetcode", null, "BBMaven1New/leetcode"));
        expectedList.put("BBMaven1New/jobs/leetcode/branches/master", new JobNameDetails("leetcode", "master", "BBMaven1New/jobs/leetcode/branches/master", null, "BBMaven1New/leetcode/master"));
        expectedList.put("BBMaven1New/jobs/leetcode2", new JobNameDetails("leetcode2", null, "BBMaven1New/jobs/leetcode2", null, "BBMaven1New/leetcode2"));
        expectedList.put("BBMaven1New/jobs/leetcode2/branches/master", new JobNameDetails("leetcode2", "master", "BBMaven1New/jobs/leetcode2/branches/master", null, "BBMaven1New/leetcode2/master"));
        expectedList.put("BBMaven1New/jobs/leetcode3", new JobNameDetails("leetcode3", null, "BBMaven1New/jobs/leetcode3", null, "BBMaven1New/leetcode3"));
        expectedList.put("BBMaven1New/jobs/leetcode3/branches/master", new JobNameDetails("leetcode3", "master", "BBMaven1New/jobs/leetcode3/branches/master", null, "BBMaven1New/leetcode3/master"));

        JobRunParserService service = new JobRunParserService();
        for (String key : expectedList.keySet()) {
            JobNameDetails actual = service.parseJobRelativePath(key);
            JobNameDetails expected = expectedList.get(key);
            assertEquals(expected, actual, actual.toString());
        }
    }

}