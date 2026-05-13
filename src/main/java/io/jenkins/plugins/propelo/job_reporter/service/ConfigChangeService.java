package io.jenkins.plugins.propelo.job_reporter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.User;
import hudson.security.ACL;
import io.jenkins.plugins.propelo.commons.models.JobConfigChange;
import io.jenkins.plugins.propelo.commons.models.JobConfigChangeType;
import io.jenkins.plugins.propelo.commons.models.JobNameDetails;
import io.jenkins.plugins.propelo.commons.service.JenkinsConfigSCMService;
import io.jenkins.plugins.propelo.commons.service.JenkinsInstanceGuidService;
import io.jenkins.plugins.propelo.commons.service.JobConfigChangeNotificationService;
import io.jenkins.plugins.propelo.commons.service.JobRunParserService;
import io.jenkins.plugins.propelo.commons.service.JobSCMService;
import io.jenkins.plugins.propelo.commons.service.JobSCMStorageService;
import io.jenkins.plugins.propelo.commons.service.ProxyConfigService;
import io.jenkins.plugins.propelo.commons.utils.MimickedUser;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hudson.init.InitMilestone.COMPLETED;

public class ConfigChangeService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final PropeloPluginImpl plugin;
    private final JobSCMStorageService jobSCMStorageService;
    private final JobRunParserService jobRunParserService;
    private final ObjectMapper mapper;

    public ConfigChangeService(PropeloPluginImpl plugin, JobSCMStorageService jobSCMStorageService, JobRunParserService jobRunParserService, ObjectMapper mapper) {
        this.plugin = plugin;
        this.jobSCMStorageService = jobSCMStorageService;
        this.jobRunParserService = jobRunParserService;
        this.mapper = mapper;
    }

    public void processConfigDelete(File configFile) {
        LOGGER.finest("ConfigChangeService.processConfigDelete starting");
        processConfigChangeEvent(configFile, JobConfigChangeType.DELETED);
        LOGGER.finest("ConfigChangeService.processConfigDelete starting");
    }

    public void processConfigRename(File configFile, String oldName, String newName) {
        LOGGER.finest("ConfigChangeService.processConfigRename starting");
        jobSCMStorageService.logSCM(plugin.getHudsonHome(), configFile);
        processConfigChangeEvent(configFile, JobConfigChangeType.RENAMED);
        LOGGER.finest("ConfigChangeService.processConfigRename starting");
    }

    public void processConfigCreate(File configFile)  {
        LOGGER.finest("ConfigChangeService.processConfigCreate starting");
        jobSCMStorageService.logSCM(plugin.getHudsonHome(), configFile);
        processConfigChangeEvent(configFile, JobConfigChangeType.CREATED);
        LOGGER.finest("ConfigChangeService.processConfigCreate ending");
    }

    public void processConfigChange(File configFile)  {
        LOGGER.finest("ConfigChangeService.processConfigChange starting");
        jobSCMStorageService.logSCM(plugin.getHudsonHome(), configFile);
        processConfigChangeEvent(configFile, JobConfigChangeType.CHANGED);
        LOGGER.finest("ConfigChangeService.processConfigChange ending");
    }

    private void performJobConfigChangeNotification(JobConfigChange jobConfigChange, String scmUrl, String scmUserId, String jenkinsInstanceGuid, String jenkinsInstanceName, String jenkinsInstanceUrl) {
        LOGGER.finest("Send Job Config Change Notifications to Propelo is true, performing job config change notification");
        ProxyConfigService.ProxyConfig proxyConfig = ProxyConfigService.generateConfigFromJenkinsProxyConfiguration(Jenkins.getInstanceOrNull());

        JobConfigChangeNotificationService jobConfigChangeNotificationService = new JobConfigChangeNotificationService(plugin.getEffectiveLevelOpsApiUrl(),mapper);
        List<String> runIds = null;
        try {
            runIds = jobConfigChangeNotificationService.submitJobConfigChangeRequest(plugin.getLevelOpsApiKey().getPlainText(),
                    jobConfigChange, scmUrl, scmUserId, jenkinsInstanceGuid, jenkinsInstanceName, jenkinsInstanceUrl, plugin.isTrustAllCertificates(), proxyConfig);
            LOGGER.log(Level.FINE, "Successfully submitted job config change event to LevelOps, jobFullName = {0}, configChangeType = {1}, runIds = {2}", new Object[] {jobConfigChange.getJobNameDetails().getJobFullName(), jobConfigChange.getChangeType(), runIds});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending job config change event!", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending job config change event!", e);
        }
        return;
    }

    private JobConfigChange processConfigChangeEvent(File configFile, JobConfigChangeType jobConfigChangeType){
        if(configFile == null){
            LOGGER.log(Level.FINEST, "configFile is null, cannot save Job Full Name Details!");
            return null;
        }
        File jobDir = configFile.getParentFile();
        JobNameDetails jobNameDetails = null;
        try {
            jobNameDetails = jobRunParserService.parseJobNameBranchNameJobFullPath(jobDir, plugin.getHudsonHome());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error parsing Job Full Name Details!", e);
            return null;
        }
        if(jobNameDetails == null) {
            LOGGER.log(Level.FINEST, "jobNameDetails is null, cannot save Job Full Name Details!");
            return null;
        }
        LOGGER.log(Level.FINEST, "jobNameDetails = {0}", jobNameDetails);

        MimickedUser currentUser = getCurrentUser();
        if(ACL.SYSTEM_USERNAME.equals(currentUser.getId())){
            LOGGER.log(Level.FINEST, "For config change the user is SYSTEM user, will not save this change! operation = {0} configFile = {1}", new Object[] {jobConfigChangeType, configFile});
            return null;
        }
        LOGGER.log(Level.FINEST, "currentUser = {0}", currentUser);

        JobConfigChange jobConfigChange = new JobConfigChange(jobNameDetails, jobConfigChangeType, System.currentTimeMillis(), currentUser.getId(), currentUser.getFullName());
        LOGGER.log(Level.FINEST, "jobConfigChange = {0}", jobConfigChange);

        JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
        JobSCMService jobSCMService = new JobSCMService(jenkinsConfigSCMService);
        JenkinsConfigSCMService.SCMResult scmResult = jobSCMService.parseSCMConfigForJob(plugin.getHudsonHome(), jobNameDetails.getJobFullName());
        LOGGER.log(Level.FINEST,"scmResult = {0}", scmResult);

        performJobConfigChangeNotification(jobConfigChange, scmResult.getUrl(), scmResult.getUserName(), getJenkinsInstanceGuid(), plugin.getJenkinsInstanceName(), getJenkinsInstanceUrl());

        return jobConfigChange;
    }

    private MimickedUser getCurrentUser(){
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if(jenkins == null){
            LOGGER.log(Level.FINEST, "jenkins == null");
            return new MimickedUser(ACL.SYSTEM_USERNAME, ACL.SYSTEM_USERNAME);
        }
        if(COMPLETED == jenkins.getInitLevel()) {
            LOGGER.log(Level.FINEST, "jenkins.getInitLevel() is COMPLETED, returning actual user");
            return new MimickedUser(User.current());
        } else {
            LOGGER.log(Level.FINEST, "jenkins.getInitLevel() is NOT COMPLETED, defaulting to SYSTEM user");
            return new MimickedUser(ACL.SYSTEM_USERNAME, ACL.SYSTEM_USERNAME);
        }
    }

    private String getJenkinsInstanceUrl() {
        return Jenkins.get().getRootUrl();
    }

    private String getJenkinsInstanceGuid(){
        JenkinsInstanceGuidService jenkinsInstanceGuidService = new JenkinsInstanceGuidService(plugin.getExpandedLevelOpsPluginDir(), plugin.getDataDirectory(), plugin.getDataDirectoryWithVersion());
        return jenkinsInstanceGuidService.createOrReturnInstanceGuid();
    }
}
