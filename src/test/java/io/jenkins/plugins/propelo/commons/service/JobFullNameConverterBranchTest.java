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
}
