package io.jenkins.plugins.propelo.commons.service;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.jenkins.plugins.propelo.commons.plugins.Common.SEI_SCM_COMMIT_IDS;

public class JobRunScmCommitsFallbackServiceTest {

    private static final String SHA_ONE = "08d386c3b05997a04bf1ffdc0948af0f44b5bd2d";
    private static final String SHA_TWO = "37482a7fcf68e1181fe3b999da2372aa1d85220a";

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldParseSingleSha() {
        JobRunScmCommitsFallbackService service = new JobRunScmCommitsFallbackService();
        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.parseCommitIdsFromRawValue(SHA_ONE));
    }

    @Test
    public void shouldParseCommaAndWhitespaceSeparatedListsAndDedupe() {
        JobRunScmCommitsFallbackService service = new JobRunScmCommitsFallbackService();
        String raw = SHA_ONE + ", " + SHA_TWO + "\n" + SHA_ONE + ";" + SHA_TWO;
        Assert.assertEquals(Arrays.asList(SHA_ONE, SHA_TWO), service.parseCommitIdsFromRawValue(raw));
    }

    @Test
    public void shouldRejectInvalidValuesAndIgnoreBlankInput() {
        JobRunScmCommitsFallbackService service = new JobRunScmCommitsFallbackService();
        Assert.assertTrue(service.parseCommitIdsFromRawValue(null).isEmpty());
        Assert.assertTrue(service.parseCommitIdsFromRawValue("   ").isEmpty());
        Assert.assertTrue(service.parseCommitIdsFromRawValue("not-a-sha, abc").isEmpty());
        Assert.assertEquals(Collections.singletonList(SHA_ONE),
                service.parseCommitIdsFromRawValue("deadbeef, " + SHA_ONE + ", 08d386c"));
    }

    @Test
    public void shouldResolveFallbackCommitIdsFromBuildParameter() throws Exception {
        JobRunScmCommitsFallbackService service = new JobRunScmCommitsFallbackService();
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-fallback");
        FreeStyleBuild build = project.scheduleBuild2(0, new ParametersAction(
                new StringParameterValue(SEI_SCM_COMMIT_IDS, SHA_ONE + "," + SHA_TWO))).get();

        List<String> commitIds = service.resolveFallbackCommitIds(build);
        Assert.assertEquals(Arrays.asList(SHA_ONE, SHA_TWO), commitIds);
    }
}
