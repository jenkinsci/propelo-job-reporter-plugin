package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Job;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Node;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Organization;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Step;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BlueOceanRestClientIntegrationTest {
    private static final boolean USE_LOCAL_JENKINS = true;
    private static final boolean DO_NOT_TRUST_ALL_CERTS = false;
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();
    private static final ProxyConfigService.ProxyConfig NO_PROXY = ProxyConfigService.ProxyConfig.NO_PROXY;
    private static final String HOST_NAME;
    private static final String USER_NAME;
    private static final String API_TOKEN;

    static {
        if (USE_LOCAL_JENKINS) {
            HOST_NAME = "http://localhost:8080";
            USER_NAME = "testread";
            API_TOKEN = "11e00b76119f44a5d1f08a50414f0d804d";
        } else {
            HOST_NAME = "https://jenkins.dev.levelops.io";
            USER_NAME = "admin";
            API_TOKEN = "111aab545fca4a69f00125af8b03df9fef";
        }
    }

    @Disabled
    @Test
    void testHostDoesNotExist() {
        BlueOceanRestClient client = new BlueOceanRestClient("http://doesnotexist:8080", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            fail("Expecting UnknownHostException!!");
        } catch (UnknownHostException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("nodename nor servname provided, or not known"));
        } catch (IOException e) {
            fail("Expecting UnknownHostException!!");
        }
    }

    @Disabled
    @Test
    void testWrongSSL() {
        BlueOceanRestClient client = new BlueOceanRestClient("https://localhost:8080", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            fail("Expecting SSLException!!");
        } catch (SSLException e) {
            String msg = e.getMessage();
            assertTrue(msg.startsWith("Unsupported or unrecognized SSL message"));
        } catch (IOException e) {
            fail("Expecting UnknownHostException!!");
        }
    }

    @Disabled
    @Test
    void testWrongSSL2() {
        BlueOceanRestClient client = new BlueOceanRestClient("http://jenkins.dev.levelops.io", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            fail("Expecting SSLException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            assertTrue(msg.startsWith("Response not successful: 403"));
        }
    }

    @Disabled
    @Test
    void testWrongUrlNotFound() {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME + "/does-not-exist", USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            fail("Expecting IOException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            assertTrue(msg.startsWith("Response not successful: 404"));
        }
    }

    @Disabled
    @Test
    void testWrongUserToken() {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, "Invalid_Api_Token", DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = null;
        try {
            organizations = client.getOrganizations();
            fail("Expecting IOException!!");
        } catch (IOException e) {
            String msg = e.getMessage();
            assertTrue(msg.startsWith("Response not successful: 401"));
        }
    }

    @Disabled
    @Test
    void testGetOrganizations() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Organization> organizations = client.getOrganizations();
        assertTrue(CollectionUtils.isNotEmpty(organizations));
        assertEquals(1, organizations.size());
        assertEquals(new Organization("Jenkins", "jenkins"), organizations.get(0));
    }

    @Disabled
    @Test
    void testGetJob() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        Job job = client.getJob("jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master");
        assertNotNull(job);
    }

    @Disabled
    @Test
    void testGetJobUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        Job job = client.getJobUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/");
        assertNotNull(job);
    }

    @Disabled
    @Test
    void testGetJobRun() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        JobRun jobRun = client.getJobRun("jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master", 6L);
        assertNotNull(jobRun);
    }

    @Disabled
    @Test
    void testGetJobRunUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        JobRun jobRun = client.getJobRunUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        assertNotNull(jobRun);
    }

    @Disabled
    @Test
    void testGetJobRunLogUsingLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunLogUsingLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        assertNotNull(log);
    }

    @Disabled
    @Test
    void testGetJobRunNodes() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodes("jenkins", "pipeline-2/master", 6L);
        assertNotNull(nodes);
        assertEquals(4, nodes.size());
    }

    @Disabled
    @Test
    void testGetJobRunNodesUsingJobRunLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodesUsingJobRunLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/");
        assertNotNull(nodes);
        assertEquals(4, nodes.size());
    }

    @Disabled
    @Test
    void testGetJobRunNodesUsingJobRunNodesLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Node> nodes = client.getJobRunNodesUsingJobRunNodesLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/nodes/");
        assertNotNull(nodes);
        assertEquals(4, nodes.size());
    }

    @Disabled
    @Test
    void testGetJobRunNodeLog() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeLog("jenkins", "Folder1/Folder2/BBMaven1New/leetcode/master", 6L, "13");
        assertNotNull(log);
    }

    @Disabled
    @Test
    void testGetJobRunNodeLogUsingJobRunLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeLogUsingJobRunLink("/blue/rest/organizations/jenkins/pipelines/pipeline-int-2/branches/master/runs/6/", "13");
        assertNotNull(log);
    }

    @Disabled
    @Test
    void testGetJobRunNodeStepsUsingJobRunNodeLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        List<Step> steps = client.getJobRunNodeStepsUsingJobRunNodeLink("/blue/rest/organizations/jenkins/pipelines/TestBlueOcean/branches/master/runs/20/nodes/13/");
        assertNotNull(steps);
    }

    @Disabled
    @Test
    void testGetJobRunNodeStepLogUsingJobRunNodeStepLink() throws IOException {
        BlueOceanRestClient client = new BlueOceanRestClient(HOST_NAME, USER_NAME, API_TOKEN, DO_NOT_TRUST_ALL_CERTS, MAPPER, NO_PROXY);
        String log = client.getJobRunNodeStepLogUsingJobRunNodeStepLink("/blue/rest/organizations/jenkins/pipelines/TestBlueOcean/branches/master/runs/20/nodes/13/steps/7/");
        assertNotNull(log);
    }
}