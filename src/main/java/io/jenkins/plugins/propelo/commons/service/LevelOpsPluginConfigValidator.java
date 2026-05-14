package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.util.FormValidation;
import hudson.util.Secret;
import io.jenkins.plugins.propelo.commons.models.HeartbeatRequest;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.GenericResponse;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelOpsPluginConfigValidator {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final ObjectMapper mapper = JsonUtils.buildObjectMapper();

    static boolean isLevelOpsApiKeyEncrypted(String levelOpsApiKey) {
        if ((levelOpsApiKey.startsWith("{")) && (levelOpsApiKey.endsWith("}"))) {
            return true;
        }
        return false;
    }

    public static FormValidation performApiKeyValidation(String levelOpsApiUrl, Secret levelOpsApiKey, boolean trustAllCertificates,
                                                         String jenkinsInstanceGuid, String instanceName,
                                                         String pluginVersionString, final ProxyConfigService.ProxyConfig proxyConfig) {
        if ((levelOpsApiKey == null) || (levelOpsApiKey.getPlainText().length() == 0)) {
            return FormValidation.error("Propelo Api Key should not be null or empty.");
        }
        String levelOpsApiKeyStr = levelOpsApiKey.getPlainText();
        LOGGER.log(Level.FINEST, "propeloApiKey = {0}", levelOpsApiKeyStr);
        //The levelopsApiKey we received was encrypted, cannot check
        if (isLevelOpsApiKeyEncrypted(levelOpsApiKeyStr)) {
            return FormValidation.ok();
        }
        GenericRequestService genericRequestService = new GenericRequestService(levelOpsApiUrl, mapper);
        String hbRequestPayload;
        try {
            hbRequestPayload = mapper.writeValueAsString(createHeartBeatRequest(System.currentTimeMillis(),
                    jenkinsInstanceGuid, levelOpsApiUrl, instanceName, pluginVersionString));
            GenericResponse genericResponse = genericRequestService.performGenericRequest(levelOpsApiKeyStr,
                    "JenkinsHeartbeat", hbRequestPayload, trustAllCertificates, null, proxyConfig);
            return FormValidation.ok();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "JsonProcessingException!!", e);
            String msg = e.getMessage();
            if (msg.startsWith("Response not successful: 401")) {
                return FormValidation.error("Propelo api key is not valid.");
            }
            return FormValidation.error("Validation failed! Error " + msg);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException!!", e);
            String msg = e.getMessage();
            if (msg.startsWith("Response not successful: 401")) {
                return FormValidation.error("Propelo api key is not valid.");
            } else if (msg.startsWith("Response not successful: 403")) {
                return FormValidation.error("SSL Exception connecting to jenkins api. Please check with Propelo Support! : " + levelOpsApiUrl);
            } else {
                return FormValidation.error("Validation failed! Error " + msg);
            }
        }
    }

    public static FormValidation validateBullseyeXmlResultPaths(String bullseyeXmlResultPaths) {
        return FormValidation.ok();
    }

    public static HeartbeatRequest createHeartBeatRequest(long timeInMillis, String instanceGuid, String apiUrl,
                                                          String instanceName, String pluginVersionString) {
        HeartbeatRequest.InstanceDetails instanceDetails = new HeartbeatRequest.InstanceDetails(Jenkins.VERSION, pluginVersionString, timeInMillis,
                apiUrl, instanceName);
        return new HeartbeatRequest(instanceGuid,
                TimeUnit.MILLISECONDS.toSeconds(timeInMillis), instanceDetails);
    }

}
