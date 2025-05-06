package io.jenkins.plugins.propelo.commons.service;

import io.jenkins.plugins.propelo.commons.models.jenkins.output.JenkinsGeneralConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JenkinsConfigServiceTest {

    private Path slaveToMasterSecurityKillSwitchPath;
    private Path locationConfigurationPath;

    @BeforeEach
    void setUp() throws Exception {
        slaveToMasterSecurityKillSwitchPath = new File(getClass().getClassLoader().getResource("configs/agent-to-master-access-control/slave-to-master-security-kill-switch-disabled").toURI()).toPath();
        locationConfigurationPath = new File(getClass().getClassLoader().getResource("configs/location-configuration/jenkins.model.JenkinsLocationConfiguration-local.xml").toURI()).toPath();
    }

    @Test
    void testSecurityRealm1() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/security-realm/jenkins-config-1.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.ACTIVE_DIRECTORY, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ROLE_BASED_STRATEGY, config.getAuthorizationType());
    }

    @Test
    void testSecurityRealm2() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/security-realm/jenkins-config-2.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.DELEGATE_TO_SERVLET_CONTAINER, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
    }

    @Test
    void testSecurityRealm3() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/security-realm/jenkins-config-3.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
    }

    @Test
    void testSecurityRealm4() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/security-realm/jenkins-config-4.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.LDAP, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ROLE_BASED_STRATEGY, config.getAuthorizationType());
    }

    @Test
    void testSecurityRealm5() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/security-realm/jenkins-config-5.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.UNIX_USER_GROUP_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ROLE_BASED_STRATEGY, config.getAuthorizationType());
    }

    @Test
    void testAuthorization1() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-1.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ANYONE_CAN_DO_ANYTHING, config.getAuthorizationType());
    }

    @Test
    void testAuthorization2() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-2.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LEGACY_MODE, config.getAuthorizationType());
    }

    @Test
    void testAuthorization3() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-3.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
    }

    @Test
    void testAuthorization4() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-4.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.MATRIX_BASED_SECURITY, config.getAuthorizationType());
    }

    @Test
    void testAuthorization5() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-5.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.PROJECT_BASED_MATRIX_AUTHORIZATION_STRATEGY, config.getAuthorizationType());
    }

    @Test
    void testAuthorization6() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/authorization/jenkins-config-6.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.UNIX_USER_GROUP_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ROLE_BASED_STRATEGY, config.getAuthorizationType());
    }

    @Test
    void testCSRFEnabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/csrf/jenkins-config-csrf-enabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertTrue(config.getCsrf().isPreventCSRF());
        assertEquals("hudson.security.csrf.DefaultCrumbIssuer", config.getCsrf().getCrumbIssuer());
        assertEquals(true, config.getCsrf().getExcludeClientIPFromCrumb());
    }

    @Test
    void testCSRFDisabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/csrf/jenkins-config-csrf-disabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
    }

    @Test
    void testSlaveAgentPortFixed() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/slave-agent-ports/jenkins-config-slave-agent-port-fixed.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
    }

    @Test
    void testSlaveAgentPortRandom() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/slave-agent-ports/jenkins-config-slave-agent-port-random.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.RANDOM, config.getSlaveAgentPort().getPortType());
        assertNull(config.getSlaveAgentPort().getPortNumber());
    }

    @Test
    void testSlaveAgentPortDisabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/slave-agent-ports/jenkins-config-slave-agent-port-disabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.DISABLED, config.getSlaveAgentPort().getPortType());
        assertNull(config.getSlaveAgentPort().getPortNumber());
    }

    @Test
    void testJNLPProtocolsInsecureDisabledSecureDisabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-disabled-secure-disabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertFalse(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
    }

    @Test
    void testJNLPProtocolsInsecureDisabledSecureEnabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-disabled-secure-enabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertFalse(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
    }

    @Test
    void testJNLPProtocolsInsecureEnabledSecureDisabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-enabled-secure-disabled.xml");
        File configFile = new File(url.toURI());
        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertTrue(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
    }

    @Test
    void testJNLPProtocolsInsecureEnabledSecureEnabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-enabled-secure-enabled.xml");
        File configFile = new File(url.toURI());

        URL killSwitchUrl = this.getClass().getClassLoader().getResource("configs/agent-to-master-access-control/slave-to-master-security-kill-switch-enabled");
        File killSwitchFile = new File(killSwitchUrl.toURI());


        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), killSwitchFile.toPath(), locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertTrue(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
        assertFalse(config.isAgentToMasterAccessControlEnabled());
    }

    @Test
    void testSlaveToMasterSecurityKillSwitchDisabled() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-enabled-secure-enabled.xml");
        File configFile = new File(url.toURI());

        URL killSwitchUrl = this.getClass().getClassLoader().getResource("configs/agent-to-master-access-control/slave-to-master-security-kill-switch-disabled");
        File killSwitchFile = new File(killSwitchUrl.toURI());

        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), killSwitchFile.toPath(), locationConfigurationPath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertTrue(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
        assertTrue(config.isAgentToMasterAccessControlEnabled());
    }

    @Test
    void testLocationConfigurationLocal() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-enabled-secure-disabled.xml");
        File configFile = new File(url.toURI());

        Path locationConfigFilePath = new File(this.getClass().getClassLoader().getResource("configs/location-configuration/jenkins.model.JenkinsLocationConfiguration-local.xml").toURI()).toPath();

        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigFilePath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertTrue(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
        assertNotNull(config.getLocatorConfig());
        assertNull(config.getLocatorConfig().getAdminEmailAddress());
        assertEquals("http://localhost:8080/", config.getLocatorConfig().getJenkinsUrl());
    }

    @Test
    void testLocationConfigurationDev() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("configs/jnlp/jenkins-config-jnlp-insecure-enabled-secure-disabled.xml");
        File configFile = new File(url.toURI());

        Path locationConfigFilePath = new File(this.getClass().getClassLoader().getResource("configs/location-configuration/jenkins.model.JenkinsLocationConfiguration-dev.xml").toURI()).toPath();

        JenkinsConfigService service = new JenkinsConfigService();
        JenkinsGeneralConfig config = service.readConfig(configFile.toPath(), slaveToMasterSecurityKillSwitchPath, locationConfigFilePath);
        assertNotNull(config);
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, config.getSecurityRealm());
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, config.getAuthorizationType());
        assertFalse(config.getCsrf().isPreventCSRF());
        assertNull(config.getCsrf().getCrumbIssuer());
        assertNull(config.getCsrf().getExcludeClientIPFromCrumb());
        assertNotNull(config.getSlaveAgentPort());
        assertEquals(JenkinsGeneralConfig.PORT_TYPE.FIXED, config.getSlaveAgentPort().getPortType());
        assertEquals(50000, config.getSlaveAgentPort().getPortNumber());
        assertNotNull(config.getJnlpProtocols());
        assertTrue(config.getJnlpProtocols().isJnlp1ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp2ProtocolEnabled());
        assertTrue(config.getJnlpProtocols().isJnlp3ProtocolEnabled());
        assertFalse(config.getJnlpProtocols().isJnlp4ProtocolEnabled());
        assertNotNull(config.getLocatorConfig());
        assertEquals("viraj@levelops.io", config.getLocatorConfig().getAdminEmailAddress());
        assertEquals("https://jenkins.dev.levelops.io/", config.getLocatorConfig().getJenkinsUrl());
    }

}