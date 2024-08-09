package io.jenkins.plugins.propelo.job_reporter.extensions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Log4j2;
import hudson.Extension;
import hudson.model.AperiodicWork;
import io.jenkins.plugins.propelo.commons.models.HeartbeatRequest;
import io.jenkins.plugins.propelo.commons.models.HeartbeatResponse;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.GenericResponse;
import io.jenkins.plugins.propelo.commons.service.GenericRequestService;
import io.jenkins.plugins.propelo.commons.service.JenkinsInstanceGuidService;
import io.jenkins.plugins.propelo.commons.service.JenkinsStatusService;
import io.jenkins.plugins.propelo.commons.service.JenkinsStatusService.LoadFileException;
import io.jenkins.plugins.propelo.commons.service.LevelOpsPluginConfigService;
import io.jenkins.plugins.propelo.commons.service.ProxyConfigService;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import jenkins.model.Jenkins;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.propelo.commons.models.PropeloJobReporterConfiguration.CONFIGURATION;

@Log4j2
@Extension
public class JenkinsHeartbeatAperiodicWork extends AperiodicWork {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final ObjectMapper mapper = JsonUtils.buildObjectMapper();
    private final PropeloPluginImpl plugin = PropeloPluginImpl.getInstance();
    private final LevelOpsPluginConfigService levelOpsPluginConfigService = new LevelOpsPluginConfigService();

    public JenkinsHeartbeatAperiodicWork() {
    }

    @Override
    public void doAperiodicRun() {
        try {
            if(CONFIGURATION == null || CONFIGURATION.getApplicationType() == null){
                LOGGER.log(Level.FINE, "No configuration found for Propelo Job Reporter's Plugin.");
                return;
            }
            monitorNow(System.currentTimeMillis());
            CONFIGURATION.isRegistered = true;
            JenkinsStatusService.getInstance().markHeartbeat(plugin.getExpandedLevelOpsPluginDir(), true);
        } catch (IOException e) {
            try {
                JenkinsStatusService.getInstance().markHeartbeat(plugin.getExpandedLevelOpsPluginDir(), false);
            } catch (LoadFileException e1) {
                LOGGER.log(Level.WARNING, "Unable to use the Propelo plugin work directory: " + e.getMessage(), e);
            }
            LOGGER.log(Level.WARNING, "doAperiodicRun: Error in sending periodic heartbeat to the server: " + e.getMessage(), e);
        }
    }

    private HeartbeatResponse monitorNow(long timeInMillis) throws IOException {
        LOGGER.log(Level.INFO, " Jenkins Heartbeat Starting periodic monitoring!!");
        HeartbeatRequest hbRequestPayload = createHeartBeatRequest(timeInMillis);
        return sendHeartbeat(hbRequestPayload);
    }

    @NotNull
    private HeartbeatResponse sendHeartbeat(HeartbeatRequest heartbeatRequest) throws IOException {
        String hbRequestPayload;
        try {
            hbRequestPayload = mapper.writeValueAsString(heartbeatRequest);
        } catch (JsonProcessingException e) {
            throw new IOException("Error converting HeartbeatRequest to json!!", e);
        }
        LOGGER.log(Level.INFO, "Heartbeat Request = " + hbRequestPayload);
        GenericRequestService genericRequestService = new GenericRequestService(
                levelOpsPluginConfigService.getLevelopsConfig().getApiUrl(), mapper);

        ProxyConfigService.ProxyConfig proxyConfig = ProxyConfigService.generateConfigFromJenkinsProxyConfiguration(Jenkins.getInstanceOrNull());
        return sendHeartbeat(hbRequestPayload, genericRequestService, plugin, proxyConfig);
    }

    @NotNull
    public HeartbeatResponse sendHeartbeat(String hbRequestPayload, GenericRequestService genericRequestService,
                                           PropeloPluginImpl plugin, final ProxyConfigService.ProxyConfig proxyConfig) throws IOException {
        GenericResponse genericResponse = genericRequestService.performGenericRequest(plugin.getLevelOpsApiKey().getPlainText(),
                "JenkinsHeartbeat", hbRequestPayload, plugin.isTrustAllCertificates(), null, proxyConfig);
        HeartbeatResponse heartbeatResponse = mapper.readValue(genericResponse.getPayload(),
                mapper.getTypeFactory().constructType(HeartbeatResponse.class));
        HeartbeatResponse.CiCdInstanceConfig configuration = heartbeatResponse.getConfiguration();
        if (configuration != null) {
            CONFIGURATION.setBullseyeXmlResultPaths(configuration.getBullseyeReportPaths() != null ? configuration.getBullseyeReportPaths() : "");
            CONFIGURATION.setHeartbeatDuration(configuration.getHeartbeatDuration() != null ? configuration.getHeartbeatDuration() : 60);
            CONFIGURATION.setConfigUpdatedAt(System.currentTimeMillis());
            CONFIGURATION.save();
        }
        LOGGER.log(Level.INFO, "Heartbeat duration is : " + this.plugin.getHeartbeatDuration() +
                " || Heartbeat response : " + genericResponse);
        return heartbeatResponse;
    }

    @Override
    public long getRecurrencePeriod() {
        return TimeUnit.SECONDS.toMillis(plugin.getHeartbeatDuration());
    }

    @Override
    public AperiodicWork getNewInstance() {
        return new JenkinsHeartbeatAperiodicWork();
    }

    private HeartbeatRequest createHeartBeatRequest(long timeInMillis) {
        HeartbeatRequest.InstanceDetails instanceDetails = new HeartbeatRequest.InstanceDetails(
                Jenkins.VERSION, plugin.getPluginVersionString(), plugin.getConfigUpdatedAt(), getJenkinsInstanceUrl(),
                plugin.getJenkinsInstanceName());
        return new HeartbeatRequest(getJenkinsInstanceGuid(),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillis), instanceDetails);
    }

    private String getJenkinsInstanceGuid() {
        JenkinsInstanceGuidService jenkinsInstanceGuidService = new JenkinsInstanceGuidService(
                plugin.getExpandedLevelOpsPluginDir(),
                plugin.getDataDirectory(), plugin.getDataDirectoryWithVersion());
        return jenkinsInstanceGuidService.createOrReturnInstanceGuid();
    }

    private String getJenkinsInstanceUrl() {
        return Jenkins.get().getRootUrl();
    }
}
