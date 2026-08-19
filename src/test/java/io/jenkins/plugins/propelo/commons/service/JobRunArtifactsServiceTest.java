package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.CiCdJobRunArtifact;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class JobRunArtifactsServiceTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldParseNestedArtifactManifest() throws Exception {
        ObjectMapper mapper = JsonUtils.buildObjectMapper();
        JobRunArtifactsService service = new JobRunArtifactsService(mapper);
        File manifest = resource("artifacts/artifact-manifest-ci.json");

        List<CiCdJobRunArtifact> artifacts = new ArrayList<>();
        service.collectFromFile(manifest, artifacts);

        Assert.assertEquals(1, artifacts.size());
        CiCdJobRunArtifact artifact = artifacts.get(0);
        Assert.assertEquals("sei-verify-demo/app", artifact.getName());
        Assert.assertEquals("1.0.0-42", artifact.getQualifier());
        Assert.assertEquals(Boolean.TRUE, artifact.getOutput());
        Assert.assertEquals(Boolean.FALSE, artifact.getInput());
        Assert.assertTrue(service.isCi(artifacts));
        Assert.assertFalse(service.isCd(artifacts));
    }

    @Test
    public void shouldParseTopLevelArtifactsArray() throws Exception {
        ObjectMapper mapper = JsonUtils.buildObjectMapper();
        JobRunArtifactsService service = new JobRunArtifactsService(mapper);
        File manifest = resource("artifacts/sei-artifacts-cd.json");

        List<CiCdJobRunArtifact> artifacts = new ArrayList<>();
        service.collectFromFile(manifest, artifacts);

        Assert.assertEquals(1, artifacts.size());
        Assert.assertTrue(service.isCd(artifacts));
        Assert.assertFalse(service.isCi(artifacts));
    }

    @Test
    public void shouldParseSingleArchivedArtifactForRunWithoutDuplicates() throws Exception {
        ObjectMapper mapper = JsonUtils.buildObjectMapper();
        JobRunArtifactsService service = new JobRunArtifactsService(mapper);

        FreeStyleProject project = jenkins.createFreeStyleProject("sei-verify-ci");
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        File archiveDir = build.getArtifactsDir();
        archiveDir.mkdirs();
        Files.copy(
                resource("artifacts/artifact-manifest-ci.json").toPath(),
                new File(archiveDir, JobRunArtifactsService.ARTIFACT_MANIFEST_FILE).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        List<CiCdJobRunArtifact> artifacts = service.parseArtifactsForRun(build);

        Assert.assertEquals("One archived manifest must yield exactly one artifact", 1, artifacts.size());
        CiCdJobRunArtifact artifact = artifacts.get(0);
        Assert.assertEquals("sei-verify-demo/app", artifact.getName());
        Assert.assertEquals(Boolean.TRUE, artifact.getOutput());
        Assert.assertTrue(service.isCi(artifacts));
        Assert.assertFalse(service.isCd(artifacts));
    }

    private static File resource(String path) throws Exception {
        URL url = JobRunArtifactsServiceTest.class.getClassLoader().getResource(path);
        Assert.assertNotNull(url);
        return new File(url.toURI());
    }
}
