package io.jenkins.plugins.propelo.commons.models.jenkins.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JenkinsConfigTest {

    @Test
    void testSerialization() throws JsonProcessingException {
        JenkinsConfig.SecurityRealm sr = new JenkinsConfig.SecurityRealm();
        sr.setData("hudson.plugins.active_directory.ActiveDirectorySecurityRealm");

        JenkinsConfig.AuthorizationStrategy auth = new JenkinsConfig.AuthorizationStrategy();
        auth.setData("hudson.security.AuthorizationStrategy$Unsecured");

        JenkinsConfig config = new JenkinsConfig();
        config.setSecurityRealm(sr);
        config.setAuthorizationStrategy(auth);

        JenkinsConfig.EnabledDisabledAgentProtocols enabledAgentProtocols = new JenkinsConfig.EnabledDisabledAgentProtocols();
        enabledAgentProtocols.setString(Arrays.asList("JNLP-connect", "JNLP2-connect", "JNLP3-connect"));

        JenkinsConfig.EnabledDisabledAgentProtocols disabledAgentProtocols = new JenkinsConfig.EnabledDisabledAgentProtocols();
        disabledAgentProtocols.setString(List.of("JNLP4-connect"));

        config.setEnabledAgentProtocols(enabledAgentProtocols);
        config.setDisabledAgentProtocols(disabledAgentProtocols);

        XmlMapper xmlMapper = new XmlMapper();
        String serialized = xmlMapper.writeValueAsString(config);
        assertNotNull(serialized);
    }

    @Test
    @Disabled("Not implemented")
    void testDeSerialization() {

    }
}
