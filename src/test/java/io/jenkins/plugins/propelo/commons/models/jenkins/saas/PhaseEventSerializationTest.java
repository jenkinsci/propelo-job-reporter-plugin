package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class PhaseEventSerializationTest {

    @Test
    public void serializesPhaseEventsOnJobRunCompleteRequest() throws Exception {
        ObjectMapper mapper = JsonUtils.buildObjectMapper();
        JobRunCompleteRequest request = new JobRunCompleteRequest(
                "my-service-pipeline", "deploy-bot", null, "https://github.com/org/my-service.git", null,
                1_725_868_800_000L, "SUCCESS", 900_000L, 87L,
                "abc-123", "jenkins-prod", "https://jenkins.example.com/", null,
                "team-a/my-service-pipeline", "team-a/my-service-pipeline", null, null,
                Collections.emptyList(), null, Collections.emptyList(), Boolean.TRUE, Boolean.TRUE);

        CiCdJobRunArtifact artifact = new CiCdJobRunArtifact(
                Boolean.FALSE, Boolean.TRUE, "jar", "gs://demo-artifacts",
                "my-service/my-service.jar", "87", "hash-1", null);
        PhaseEvent ci = new PhaseEvent(
                "CI", null, "CI", 1_725_868_800_000L, 1_725_869_100_000L, "SUCCESS",
                Arrays.asList("a1b2c3d4", "e5f6g7h8"), Collections.singletonList(artifact));
        PhaseEvent cd = new PhaseEvent(
                "CD", "prod", "CD", 1_725_869_300_000L, 1_725_869_700_000L, "SUCCESS",
                Collections.emptyList(),
                Collections.singletonList(new CiCdJobRunArtifact(
                        Boolean.TRUE, Boolean.FALSE, "jar", "gs://demo-artifacts",
                        "my-service/my-service.jar", "87", "hash-1", null)));
        request.setPhaseEvents(Arrays.asList(ci, cd));

        String json = mapper.writeValueAsString(request);
        JsonNode root = mapper.readTree(json);
        Assert.assertTrue(root.has("phase_events"));
        Assert.assertEquals(2, root.get("phase_events").size());
        Assert.assertEquals("CI", root.get("phase_events").get(0).get("phase").asText());
        Assert.assertEquals("CD", root.get("phase_events").get(1).get("phase").asText());
        Assert.assertEquals("prod", root.get("phase_events").get(1).get("environment").asText());
        Assert.assertEquals("a1b2c3d4", root.get("phase_events").get(0).get("scm_commit_ids").get(0).asText());
        Assert.assertEquals(0, root.get("phase_events").get(1).get("scm_commit_ids").size());
    }
}
