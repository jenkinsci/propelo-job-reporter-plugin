package io.jenkins.plugins.propelo.commons.service;

import org.junit.Assert;
import org.junit.Test;

public class JobFullNameConverterBranchTest {

    @Test
    public void shouldReplaceEncodedBranchSuffixInNormalizedName() {
        String normalized = "SEI-MB/SYSID-DEMO/digital-internal-transfer-data-service/feature-maac-mute.l94old";
        String actual = JobFullNameConverter.replaceEncodedBranchInNormalizedName(
                normalized, "feature-maac-mute.l94old", "feature/maac-mute");
        Assert.assertEquals(
                "SEI-MB/SYSID-DEMO/digital-internal-transfer-data-service/feature/maac-mute", actual);
    }

    @Test
    public void shouldLeaveNormalizedNameUnchangedWhenBranchMatches() {
        String normalized = "pipeline-1/master";
        String actual = JobFullNameConverter.replaceEncodedBranchInNormalizedName(normalized, "master", "master");
        Assert.assertEquals(normalized, actual);
    }

    @Test
    public void shouldReplaceEncodedBranchInJobFullName() {
        String jobFullName = "SEI-MB/jobs/SYSID-DEMO-GH-BUILD/jobs/digital-internal-transfer-data-service/branches/feature-maac-mute.l94old";
        String actual = JobFullNameConverter.replaceEncodedBranchInJobFullName(
                jobFullName, "feature-maac-mute.l94old", "feature/maac-mute");
        Assert.assertEquals(
                "SEI-MB/jobs/SYSID-DEMO-GH-BUILD/jobs/digital-internal-transfer-data-service/branches/feature%2Fmaac-mute",
                actual);
    }

    @Test
    public void shouldReplaceEncodedBranchInNestedJobFullName() {
        String jobFullName = "SEI-MB/jobs/SYSID-DEMO-GH-BUILD/jobs/digital-internal-transfer-data-service/branches/feature-maac-mute.l94old/jobs/sub-job";
        String actual = JobFullNameConverter.replaceEncodedBranchInJobFullName(
                jobFullName, "feature-maac-mute.l94old", "feature/maac-mute");
        Assert.assertEquals(
                "SEI-MB/jobs/SYSID-DEMO-GH-BUILD/jobs/digital-internal-transfer-data-service/branches/feature%2Fmaac-mute/jobs/sub-job",
                actual);
    }
}
