package io.jenkins.plugins.propelo.job_reporter.extensions;

import io.jenkins.plugins.propelo.commons.models.jenkins.saas.PhaseEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class LevelOpsRunListenerPhaseEventsTest {

    @Test
    public void hasCiPhaseWithCommits_falseWhenOnlyCdMarkers() {
        PhaseEvent cd = new PhaseEvent(
                "CD", "staging", "deploy", 1L, 2L, "SUCCESS",
                Collections.emptyList(), Collections.emptyList());
        Assert.assertFalse(LevelOpsRunListener.hasCiPhaseWithCommits(Collections.singletonList(cd)));
    }

    @Test
    public void hasCiPhaseWithCommits_falseWhenCiHasNoCommits() {
        PhaseEvent ci = new PhaseEvent(
                "CI", null, "build", 1L, 2L, "SUCCESS",
                Collections.emptyList(), Collections.emptyList());
        Assert.assertFalse(LevelOpsRunListener.hasCiPhaseWithCommits(Collections.singletonList(ci)));
    }

    @Test
    public void hasCiPhaseWithCommits_trueWhenCiCarriesCommits() {
        PhaseEvent ci = new PhaseEvent(
                "CI", null, "build", 1L, 2L, "SUCCESS",
                Collections.singletonList("abc123"), Collections.emptyList());
        PhaseEvent cd = new PhaseEvent(
                "CD", "prod", "deploy", 3L, 4L, "SUCCESS",
                Collections.emptyList(), Collections.emptyList());
        Assert.assertTrue(LevelOpsRunListener.hasCiPhaseWithCommits(Arrays.asList(ci, cd)));
    }

    @Test
    public void hasCiPhaseWithCommits_falseForNullOrEmpty() {
        Assert.assertFalse(LevelOpsRunListener.hasCiPhaseWithCommits(null));
        Assert.assertFalse(LevelOpsRunListener.hasCiPhaseWithCommits(Collections.emptyList()));
    }
}
