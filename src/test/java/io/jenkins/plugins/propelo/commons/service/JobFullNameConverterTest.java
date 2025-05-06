package io.jenkins.plugins.propelo.commons.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JobFullNameConverterTest {

    @Test
    void test() {
        assertNull(JobFullNameConverter.convertJobFullNameToJobNormalizedFullName(null));
        assertEquals("", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName(""));
        assertEquals("    ", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("    "));

        assertEquals("Pipe2", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("Pipe2"));
        assertEquals("com.wordnik$swagger-codegen_2.9.1", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("com.wordnik$swagger-codegen_2.9.1"));
        assertEquals("Update-commons", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("Update-commons"));
        assertEquals("BBMaven1New/leetcode", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("BBMaven1New/jobs/leetcode"));
        assertEquals("BBMaven1New/leetcode/master", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("BBMaven1New/jobs/leetcode/branches/master"));
        assertEquals("Folder1/Folder2/BBMaven1New/leetcode", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode"));
        assertEquals("Folder1/Folder2/BBMaven1New/leetcode/master", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("Folder1/jobs/Folder2/jobs/BBMaven1New/jobs/leetcode/branches/master"));
        assertEquals("TestBlueOcean", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("TestBlueOcean"));
        assertEquals("TestBlueOcean/master", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("TestBlueOcean/branches/master"));

        assertEquals("pipeline-1", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("pipeline-1"));
        assertEquals("pipeline-1/master", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("pipeline-1/branches/master"));
        assertEquals("pipeline-int-1", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("pipeline-int-1"));
        assertEquals("pipeline-int-1/master", JobFullNameConverter.convertJobFullNameToJobNormalizedFullName("pipeline-int-1/branches/master"));
    }
}