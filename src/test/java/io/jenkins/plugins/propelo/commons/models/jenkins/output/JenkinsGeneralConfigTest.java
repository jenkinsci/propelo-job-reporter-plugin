package io.jenkins.plugins.propelo.commons.models.jenkins.output;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JenkinsGeneralConfigTest {

    @Test
    void testSecurityRealmParse() {
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.ACTIVE_DIRECTORY, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.plugins.active_directory.ActiveDirectorySecurityRealm"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.DELEGATE_TO_SERVLET_CONTAINER, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.security.LegacySecurityRealm"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.JENKINS_OWN_DATABASE, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.security.HudsonPrivateSecurityRealm"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.LDAP, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.security.LDAPSecurityRealm"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.UNIX_USER_GROUP_DATABASE, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.security.PAMSecurityRealm"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.OTHER, JenkinsGeneralConfig.SECURITY_REALM.parseRealm("hudson.security.unknown"));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.OTHER, JenkinsGeneralConfig.SECURITY_REALM.parseRealm(null));
        assertEquals(JenkinsGeneralConfig.SECURITY_REALM.OTHER, JenkinsGeneralConfig.SECURITY_REALM.parseRealm(""));
    }

    @Test
    void testAuthorizationTypeParse() {
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ANYONE_CAN_DO_ANYTHING, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("hudson.security.AuthorizationStrategy$Unsecured"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LEGACY_MODE, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("hudson.security.LegacyAuthorizationStrategy"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.LOGGED_IN_USERS_CAN_DO_ANYTHING, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("hudson.security.FullControlOnceLoggedInAuthorizationStrategy"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.MATRIX_BASED_SECURITY, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("hudson.security.GlobalMatrixAuthorizationStrategy"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.PROJECT_BASED_MATRIX_AUTHORIZATION_STRATEGY, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("hudson.security.ProjectMatrixAuthorizationStrategy"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.ROLE_BASED_STRATEGY, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization("com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy"));
        assertEquals(JenkinsGeneralConfig.AUTHORIZATION_TYPE.OTHER, JenkinsGeneralConfig.AUTHORIZATION_TYPE.parseAuthorization(""));
    }

    @Test
    void testJNLPProtocols() {
        JenkinsGeneralConfig.JNLPProtocols jnlpProtocols = JenkinsGeneralConfig.JNLPProtocols.builder().build();
        assertNotNull(jnlpProtocols);
        assertFalse(jnlpProtocols.isJnlp1ProtocolEnabled());
        assertFalse(jnlpProtocols.isJnlp2ProtocolEnabled());
        assertFalse(jnlpProtocols.isJnlp3ProtocolEnabled());
        assertTrue(jnlpProtocols.isJnlp4ProtocolEnabled());
    }
}