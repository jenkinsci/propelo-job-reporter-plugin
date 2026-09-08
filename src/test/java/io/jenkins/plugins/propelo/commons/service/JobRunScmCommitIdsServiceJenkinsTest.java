package io.jenkins.plugins.propelo.commons.service;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.TaskListener;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.jenkins.plugins.propelo.commons.plugins.Common.SEI_SCM_COMMIT_IDS;

public class JobRunScmCommitIdsServiceJenkinsTest {

    private static final String SHA_ONE = "08d386c3b05997a04bf1ffdc0948af0f44b5bd2d";
    private static final String SHA_TWO = "37482a7fcf68e1181fe3b999da2372aa1d85220a";

    @ClassRule
    public static JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldResolveExplicitCommitIdsFromBuildParameter() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-explicit");
        FreeStyleBuild build = project.scheduleBuild2(0, new ParametersAction(
                new StringParameterValue(SEI_SCM_COMMIT_IDS, SHA_ONE + "," + SHA_TWO))).get();

        List<String> commitIds = service.resolveExplicitCommitIds(build);
        Assert.assertEquals(Arrays.asList(SHA_ONE, SHA_TWO), commitIds);
    }

    @Test
    public void shouldResolvePipelineEnvironmentOverrideFromCurrentRunAction() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-pipeline-override");
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        build.addAction(new PipelineEnvironmentOverrideTestAction(
                Map.of(SEI_SCM_COMMIT_IDS, SHA_ONE)));

        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.resolveExplicitCommitIds(build));
    }

    @Test
    public void shouldPreferPipelineEnvironmentOverrideOverBuildParameter() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-override-precedence");
        FreeStyleBuild build = project.scheduleBuild2(0, new ParametersAction(
                new StringParameterValue(SEI_SCM_COMMIT_IDS, SHA_TWO))).get();
        build.addAction(new PipelineEnvironmentOverrideTestAction(
                Map.of(SEI_SCM_COMMIT_IDS, SHA_ONE)));

        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.resolveExplicitCommitIds(build));
    }

    @Test
    public void shouldIgnoreGlobalEnvironmentWhenNoRunAttachedSourceExists() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-inherited-env");
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        build.addAction(new InheritedEnvironmentContributingTestAction(SEI_SCM_COMMIT_IDS, SHA_ONE));

        Assert.assertEquals(SHA_ONE, build.getEnvironment(TaskListener.NULL).get(SEI_SCM_COMMIT_IDS));

        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        Assert.assertTrue(service.resolveExplicitCommitIds(build).isEmpty());
    }

    @Test
    public void shouldResolveExplicitCommitIdsPerRunWithoutCrossRunLeakage() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject projectOne = jenkins.createFreeStyleProject("sei-scm-run-one");
        FreeStyleProject projectTwo = jenkins.createFreeStyleProject("sei-scm-run-two");

        FreeStyleBuild buildOne = projectOne.scheduleBuild2(0, new ParametersAction(
                new StringParameterValue(SEI_SCM_COMMIT_IDS, SHA_ONE))).get();
        FreeStyleBuild buildTwo = projectTwo.scheduleBuild2(0, new ParametersAction(
                new StringParameterValue(SEI_SCM_COMMIT_IDS, SHA_TWO))).get();

        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.resolveExplicitCommitIds(buildOne));
        Assert.assertEquals(Collections.singletonList(SHA_TWO), service.resolveExplicitCommitIds(buildTwo));
    }

    @Test
    public void shouldResolvePipelineEnvironmentOverridePerRunWithoutCrossRunLeakage() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject projectOne = jenkins.createFreeStyleProject("sei-scm-override-run-one");
        FreeStyleProject projectTwo = jenkins.createFreeStyleProject("sei-scm-override-run-two");

        FreeStyleBuild buildOne = projectOne.scheduleBuild2(0).get();
        buildOne.addAction(new PipelineEnvironmentOverrideTestAction(
                Map.of(SEI_SCM_COMMIT_IDS, SHA_ONE)));
        FreeStyleBuild buildTwo = projectTwo.scheduleBuild2(0).get();
        buildTwo.addAction(new PipelineEnvironmentOverrideTestAction(
                Map.of(SEI_SCM_COMMIT_IDS, SHA_TWO)));

        Assert.assertEquals(Collections.singletonList(SHA_ONE), service.resolveExplicitCommitIds(buildOne));
        Assert.assertEquals(Collections.singletonList(SHA_TWO), service.resolveExplicitCommitIds(buildTwo));
    }

    @Test
    public void shouldReturnEmptyWhenNoParameterOrValuePresent() throws Exception {
        JobRunScmCommitIdsService service = new JobRunScmCommitIdsService();
        FreeStyleProject project = jenkins.createFreeStyleProject("sei-scm-no-param");
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        Assert.assertTrue(service.resolveExplicitCommitIds(build).isEmpty());
        Assert.assertTrue(service.resolveExplicitCommitIds(null).isEmpty());
    }
}
