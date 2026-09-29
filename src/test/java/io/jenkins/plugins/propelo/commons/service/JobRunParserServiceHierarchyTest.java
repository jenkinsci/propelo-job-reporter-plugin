package io.jenkins.plugins.propelo.commons.service;

import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.propelo.commons.models.JobNameDetails;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JobRunParserServiceHierarchyTest {

    private static final String REAL_MULTIBRANCH_PROJECT_NAME = "digital-linked-account-management-service";
    private static final String MANGLED_MULTIBRANCH_PROJECT_NAME = "digital-li.1cfav7par.ent-service";
    private static final String REAL_FOLDER_NAME = "enterprise-application-platform-team-folder";
    private static final String MANGLED_FOLDER_NAME = "enterprise-ap.abc123xyz.team-folder";
    private static final String PLAIN_JOB_NAME = "deploy-service";
    private static final String REAL_BRANCH_NAME = "feature/my-branch";
    private static final String ENCODED_BRANCH_LEAF = "feature%2Fmy-branch";

    @Test
    public void shouldUseRealMultibranchProjectNameWhenPathIsMangled() {
        Run build = mockBuildWithParent();
        JobNameDetails pathDerived = new JobNameDetails(
                MANGLED_MULTIBRANCH_PROJECT_NAME,
                ENCODED_BRANCH_LEAF,
                MANGLED_MULTIBRANCH_PROJECT_NAME + "/branches/" + ENCODED_BRANCH_LEAF,
                null,
                MANGLED_MULTIBRANCH_PROJECT_NAME + "/" + REAL_BRANCH_NAME);

        JobRunParserService service = new TestableJobRunParserService(
                Arrays.asList(REAL_MULTIBRANCH_PROJECT_NAME, ENCODED_BRANCH_LEAF),
                REAL_BRANCH_NAME);
        JobNameDetails actual = service.resolveFromItemHierarchy(build, pathDerived);

        Assert.assertEquals(REAL_MULTIBRANCH_PROJECT_NAME, actual.getJobName());
        Assert.assertEquals(REAL_BRANCH_NAME, actual.getBranchName());
        Assert.assertEquals(
                REAL_MULTIBRANCH_PROJECT_NAME + "/branches/" + ENCODED_BRANCH_LEAF,
                actual.getJobFullName());
        Assert.assertEquals(
                REAL_MULTIBRANCH_PROJECT_NAME + "/" + REAL_BRANCH_NAME,
                actual.getJobNormalizedFullName());
    }

    @Test
    public void shouldUseRealFolderNameForNestedPlainJobWhenPathIsMangled() {
        Run build = mockBuildWithParent();
        JobNameDetails pathDerived = new JobNameDetails(
                PLAIN_JOB_NAME,
                null,
                MANGLED_FOLDER_NAME + "/jobs/" + PLAIN_JOB_NAME,
                null,
                MANGLED_FOLDER_NAME + "/" + PLAIN_JOB_NAME);

        JobRunParserService service = new TestableJobRunParserService(
                Arrays.asList(REAL_FOLDER_NAME, PLAIN_JOB_NAME),
                null);
        JobNameDetails actual = service.resolveFromItemHierarchy(build, pathDerived);

        Assert.assertEquals(PLAIN_JOB_NAME, actual.getJobName());
        Assert.assertEquals(REAL_FOLDER_NAME + "/jobs/" + PLAIN_JOB_NAME, actual.getJobFullName());
        Assert.assertEquals(REAL_FOLDER_NAME + "/" + PLAIN_JOB_NAME, actual.getJobNormalizedFullName());
    }

    @Test
    public void shouldUseRealModuleNameWhenPathIsMangled() {
        String realProjectName = "openapi-generator";
        String mangledProjectName = "openapi-gen.abc123xyz.rator";
        String moduleName = "com.wordnik$swagger-codegen_2.9.1";

        Run build = mockBuildWithParent();
        JobNameDetails pathDerived = new JobNameDetails(
                mangledProjectName,
                null,
                mangledProjectName + "/modules/" + moduleName,
                moduleName,
                mangledProjectName + "/" + moduleName);

        JobRunParserService service = new TestableJobRunParserService(
                Arrays.asList(realProjectName, moduleName),
                null);
        JobNameDetails actual = service.resolveFromItemHierarchy(build, pathDerived);

        Assert.assertEquals(realProjectName, actual.getJobName());
        Assert.assertEquals(moduleName, actual.getModuleName());
        Assert.assertEquals(realProjectName + "/modules/" + moduleName, actual.getJobFullName());
        Assert.assertEquals(realProjectName + "/" + moduleName, actual.getJobNormalizedFullName());
    }

    @Test
    public void shouldFallbackToPathDerivedWhenHierarchyStructureMismatches() {
        Run build = mockBuildWithParent();
        JobNameDetails pathDerived = new JobNameDetails(
                "pipeline-1",
                "master",
                "pipeline-1/branches/master",
                null,
                "pipeline-1/master");

        JobRunParserService service = new TestableJobRunParserService(
                Collections.singletonList("pipeline-1"),
                null);
        JobNameDetails actual = service.resolveFromItemHierarchy(build, pathDerived);

        Assert.assertSame(pathDerived, actual);
    }

    @Test
    public void shouldFallbackToPathBranchNameWhenScmBranchResolverReturnsBlank() {
        Run build = mockBuildWithParent();
        JobNameDetails pathDerived = new JobNameDetails(
                MANGLED_MULTIBRANCH_PROJECT_NAME,
                "master",
                MANGLED_MULTIBRANCH_PROJECT_NAME + "/branches/master",
                null,
                MANGLED_MULTIBRANCH_PROJECT_NAME + "/master");

        JobRunParserService service = new TestableJobRunParserService(
                Arrays.asList(REAL_MULTIBRANCH_PROJECT_NAME, "master"),
                null);
        JobNameDetails actual = service.resolveFromItemHierarchy(build, pathDerived);

        Assert.assertEquals(REAL_MULTIBRANCH_PROJECT_NAME, actual.getJobName());
        Assert.assertEquals("master", actual.getBranchName());
    }

    private static Run mockBuildWithParent() {
        Run build = Mockito.mock(Run.class);
        Job job = Mockito.mock(Job.class);
        Mockito.when(build.getParent()).thenReturn(job);
        return build;
    }

    private static final class TestableJobRunParserService extends JobRunParserService {
        private final List<String> hierarchyParts;
        private final String scmBranchName;

        private TestableJobRunParserService(List<String> hierarchyParts, String scmBranchName) {
            this.hierarchyParts = hierarchyParts;
            this.scmBranchName = scmBranchName;
        }

        @Override
        protected Jenkins getJenkinsInstance() {
            return Mockito.mock(Jenkins.class);
        }

        @Override
        List<String> collectItemHierarchyParts(hudson.model.Item leafItem, Jenkins jenkins) {
            return hierarchyParts;
        }

        @Override
        protected String resolveScmBranchName(Run build) {
            return scmBranchName;
        }
    }
}
