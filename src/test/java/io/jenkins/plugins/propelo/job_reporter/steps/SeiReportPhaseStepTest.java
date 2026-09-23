package io.jenkins.plugins.propelo.job_reporter.steps;

import io.jenkins.plugins.propelo.commons.models.jenkins.saas.CiCdJobRunArtifact;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.PhaseEvent;
import io.jenkins.plugins.propelo.job_reporter.extensions.PropeloStageMarkerAction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeiReportPhaseStepTest {

    @Test
    public void validate_acceptsCiPhase() {
        SeiReportPhaseStep.validate("CI", null, 1_725_868_800_000L);
    }

    @Test
    public void validate_requiresEnvironmentForCd() {
        try {
            SeiReportPhaseStep.validate("CD", null, 1_725_868_800_000L);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("environment"));
        }
    }

    @Test
    public void validate_requiresStartTime() {
        try {
            SeiReportPhaseStep.validate("CI", null, null);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("startTime"));
        }
    }

    @Test
    public void normalizeCommits_forcesEmptyForCd() {
        List<String> commits = SeiReportPhaseStep.normalizeCommits(
                "CD", Arrays.asList("abc", "def"));
        Assert.assertTrue(commits.isEmpty());
    }

    @Test
    public void normalizeCommits_keepsCiCommits() {
        List<String> commits = SeiReportPhaseStep.normalizeCommits(
                "CI", Arrays.asList(" a1b2 ", "", "e5f6"));
        Assert.assertEquals(Arrays.asList("a1b2", "e5f6"), commits);
    }

    @Test
    public void convertArtifacts_mapsContractFields() {
        Map<String, Object> artifact = new HashMap<>();
        artifact.put("type", "jar");
        artifact.put("location", "gs://demo-artifacts");
        artifact.put("name", "my-service/my-service.jar");
        artifact.put("qualifier", "87");
        artifact.put("hash", "abc123");
        artifact.put("input", false);
        artifact.put("output", true);

        List<CiCdJobRunArtifact> converted = SeiReportPhaseStep.convertArtifacts(
                Collections.singletonList(artifact));
        Assert.assertEquals(1, converted.size());
        Assert.assertEquals("jar", converted.get(0).getType());
        Assert.assertEquals("gs://demo-artifacts", converted.get(0).getLocation());
        Assert.assertEquals(Boolean.FALSE, converted.get(0).getInput());
        Assert.assertEquals(Boolean.TRUE, converted.get(0).getOutput());
        Assert.assertEquals("abc123", converted.get(0).getHash());
    }

    @Test
    public void markerAction_appendsAndSnapshots() {
        PropeloStageMarkerAction action = new PropeloStageMarkerAction();
        Assert.assertTrue(action.isEmpty());

        PhaseEvent ci = new PhaseEvent("CI", null, "CI", 100L, 200L, "SUCCESS",
                Arrays.asList("a1"), Collections.emptyList());
        PhaseEvent cd = new PhaseEvent("CD", "prod", "CD", 300L, 400L, "SUCCESS",
                Collections.emptyList(), Collections.emptyList());
        action.append(ci);
        action.append(cd);

        List<PhaseEvent> snapshot = action.snapshot();
        Assert.assertEquals(2, snapshot.size());
        Assert.assertEquals("CI", snapshot.get(0).getPhase());
        Assert.assertEquals("CD", snapshot.get(1).getPhase());
        Assert.assertEquals("prod", snapshot.get(1).getEnvironment());
    }
}
