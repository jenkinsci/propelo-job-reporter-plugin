package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Job;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Node;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Organization;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Step;
import io.jenkins.plugins.propelo.commons.service.BlueOceanRestClient;
import io.jenkins.plugins.propelo.commons.service.ProxyConfigService;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class BlueOceanRestClientIntegrationTest {
    private static final boolean USE_LOCAL_JENKINS = true;
    private static final boolean DO_NOT_TRUST_ALL_CERTS = false;
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();
    private static final ProxyConfigService.ProxyConfig NO_PROXY = ProxyConfigService.ProxyConfig.NO_PROXY;
    private static final String HOST_NAME;
    private static final String USER_NAME;
    private static final String API_TOKEN;
    static {
        if(USE_LOCAL_JENKINS){
            HOST_NAME = "http://localhost:8080";
            USER_NAME = "testread";
            API_TOKEN = "11e00b76119f44a5d1f08a50414f0d804d";
        } else {
            HOST_NAME = "https://jenkins.dev.levelops.io";
            USER_NAME = "admin";
            API_TOKEN = "111aab545fca4a69f00125af8b03df9fef";
        }
    }

    @Ignore
    @Test
    public void testHostDoesNotExist() {
        BlueOceanRestClient client = new BlueOceanRestClient("http://doesnotexist:8080", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            Assert.fail("Expecting UnknownHostException!!");
        } catch (UnknownHostException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.contains("nodename nor servname provided, or not known"));
        } catch (IOException e) {
            Assert.fail("Expecting UnknownHostException!!");
        }
    }

    @Ignore
    @Test
    public void testWrongSSL() {
        BlueOceanRestClient client = new BlueOceanRestClient("https://localhost:8080", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            Assert.fail("Expecting SSLException!!");
        } catch (SSLException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.startsWith("Unsupported or unrecognized SSL message"));
        } catch (IOException e) {
            Assert.fail("Expecting UnknownHostException!!");
        }
    }

    @Ignore
    @Test
    public void testWrongSSL2() {
        BlueOceanRestClient client = new BlueOceanRestClient("http://jenkins.dev.levelops.io", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            Assert.fail("Expecting SSLException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.startsWith("Response not successful: 403"));
        }
    }

    @Ignore
    @Test
    public void testWrongUrlNotFound() {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME + "/does-not-exist", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            Assert.fail("Expecting IOException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.startsWith("Response not successful: 404"));
        }
    }

    @Ignore
    @Test
    public void testWrongUserToken() {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, "Invalid_Api_Token", DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            Assert.fail("Expecting IOException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.startsWith("Response not successful: 401"));
        }
    }

    @Ignore
    @Test
    public void testGetOrganizations() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = client.getOrganizations();
        Assert.assertTrue(CollectionUtils.isNotEmpty(organizations));
        Assert.assertEquals(1, organizations.size());
        Assert.assertEquals(new Organization("Jenkins", "jenkins"), organizations.get(0));
    }

    @Ignore
    @Test
    public void testGetJob() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        Job job = client.getJob( "jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master");
        Assert.assertNotNull(job);
    }

    @Ignore
    @Test
    public void testGetJobUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        Job job = client.getJobUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/");
        Assert.assertNotNull(job);
    }

    @Ignore
    @Test
    public void testGetJobRun() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        JobRun jobRun = client.getJobRun( "jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master", 6L);
        Assert.assertNotNull(jobRun);
    }

    @Ignore
    @Test
    public void testGetJobRunUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        JobRun jobRun = client.getJobRunUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        Assert.assertNotNull(jobRun);
    }

    @Ignore
    @Test
    public void testGetJobRunLogUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunLogUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        Assert.assertNotNull(log);
    }

    @Ignore
    @Test
    public void testGetJobRunNodes() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodes( "jenkins", "pipeline-2/master", 6L);
        Assert.assertNotNull(nodes);
        Assert.assertEquals(4, nodes);
    }

    @Ignore
    @Test
    public void testGetJobRunNodesUsingJobRunLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodesUsingJobRunLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        Assert.assertNotNull(nodes);
        Assert.assertEquals(4, nodes);
    }

    @Ignore
    @Test
    public void testGetJobRunNodesUsingJobRunNodesLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodesUsingJobRunNodesLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/nodes/");
        Assert.assertNotNull(nodes);
        Assert.assertEquals(4, nodes);
    }

    @Ignore
    @Test
    public void testGetJobRunNodeLog() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeLog( "jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master", 6L,"13");
        Assert.assertNotNull(log);
    }

    @Ignore
    @Test
    public void testGetJobRunNodeLogUsingJobRunLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeLogUsingJobRunLink( "/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/","13");
        Assert.assertNotNull(log);
    }

    @Ignore
    @Test
    public void testGetJobRunNodeStepsUsingJobRunNodeLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Step> steps = client.getJobRunNodeStepsUsingJobRunNodeLink( "/blue/rest/organizations/jenkins/pipelines/TestBlueOcean/branches/master/runs/20/nodes/13/");
        Assert.assertNotNull(steps);
    }

    @Ignore
    @Test
    public void testGetJobRunNodeStepLogUsingJobRunNodeStepLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeStepLogUsingJobRunNodeStepLink( "/blue/rest/organizations/jenkins/pipelines/TestBlueOcean/branches/master/runs/20/nodes/13/steps/7/");
        Assert.assertNotNull(log);
    }
}