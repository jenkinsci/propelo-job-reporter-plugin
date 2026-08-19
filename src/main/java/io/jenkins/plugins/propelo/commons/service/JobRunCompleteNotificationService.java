package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.JobRunCompleteData;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.CiCdJobRunArtifact;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.GenericResponse;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.JobRunCompleteRequest;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.JobRunCompleteResponse;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobRunCompleteNotificationService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String JENKINS_PLUGIN_JOB_RUN_COMPLETE = "JenkinsPluginJobRunComplete";

    private final String apiUrl;
    private final ObjectMapper mapper;

    public JobRunCompleteNotificationService(String apiUrl, ObjectMapper mapper) {
        this.apiUrl = apiUrl;
        this.mapper = mapper;
    }

    private File writeJobRunCompleteDataZipFile(JobRunCompleteData jobRunCompleteData) {
        LOGGER.log(Level.FINEST, "JobRunCompleteNotificationService writeJobRunCompleteDataZipFile starting.");
        if (jobRunCompleteData == null) {
            LOGGER.log(Level.FINEST, "jobRunCompleteData is null");
            return null;
        }
        File completeDataZipFile = jobRunCompleteData.getCompleteDataZipFile();
        LOGGER.log(Level.FINEST, "JobRunCompleteNotificationService completeDataZipFile ={0}", completeDataZipFile);
        File completeDataDir = jobRunCompleteData.getCompleteDataDirectory();
        LOGGER.log(Level.FINEST, "JobRunCompleteNotificationService completeDataDir ={0}", completeDataDir);

        if ((completeDataZipFile == null) || (completeDataDir == null) || (!completeDataDir.exists())) {
            LOGGER.log(Level.FINEST, "completeDataZipFile is null or completeDataDir is null or does not exist!");
            return null;
        }
        ZipService zipService = new ZipService();
        try {
            LOGGER.log(Level.FINEST, "zipping complete data dir starting");
            zipService.zipDirectory(completeDataDir, completeDataZipFile);
            LOGGER.log(Level.FINEST, "zipping complete data dir completed");
            return completeDataZipFile;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error zipping complete data dir!", e);
            return null;
        }
    }

    public List<String> submitJobRunCompleteRequest(final String apiKey, JobRunDetail jobRunDetail, String scmUrl, String scmUserId,
                                                    String jenkinsInstanceGuid, String jenkinsInstanceName, String jenkinsInstanceUrl, final boolean trustAllCertificates,
                                                    JobRunCompleteData jobRunCompleteData,
                                                    List<String> scmCommitIds, List<CiCdJobRunArtifact> artifacts, Boolean ci, Boolean cd,
                                                    UUID failedLogFileUUID, final ProxyConfigService.ProxyConfig proxyConfig) throws IOException {
        File jobRunCompleteDataDirectory = null;
        File jobRunCompleteDataZipFile = null;
        try {
            JobRun jobRun = (jobRunCompleteData == null) ? null : jobRunCompleteData.getJobRun();
            jobRunCompleteDataDirectory = (jobRunCompleteData == null) ? null : jobRunCompleteData.getCompleteDataDirectory();
            if (jobRunCompleteData != null && failedLogFileUUID != null) {
                jobRun = new JobRun(jobRunDetail, failedLogFileUUID);
            }
            JobRunCompleteRequest jobRunCompleteRequest = new JobRunCompleteRequest(jobRunDetail.getJobName(), jobRunDetail.getUserId(),
                    jobRunDetail.getJobRunParams(), scmUrl, scmUserId, jobRunDetail.getStartTime(), jobRunDetail.getResult(), jobRunDetail.getDuration(), jobRunDetail.getBuildNumber(), jenkinsInstanceGuid, jenkinsInstanceName, jenkinsInstanceUrl, jobRun,
                    jobRunDetail.getJobFullName(), jobRunDetail.getJobNormalizedFullName(), jobRunDetail.getBranchName(), jobRunDetail.getModuleName(), scmCommitIds, jobRunDetail.getTriggerChain(),
                    artifacts, ci, cd);

            String payload;
            try {
                payload = mapper.writeValueAsString(jobRunCompleteRequest);
            } catch (JsonProcessingException e) {
                throw new IOException("Error converting JobRunCompleteNotification to json!!", e);
            }
            LOGGER.finest("payload = " + payload);

            LOGGER.log(Level.FINEST, "writeJobRunCompleteDataZipFile starting");
            jobRunCompleteDataZipFile = writeJobRunCompleteDataZipFile(jobRunCompleteData);
            LOGGER.log(Level.FINEST, "writeJobRunCompleteDataZipFile completed");

            GenericRequestService genericRequestService = new GenericRequestService(apiUrl, mapper);
            LOGGER.log(Level.FINEST, "perform Generic Request starting");
            GenericResponse genericResponse = genericRequestService.performGenericRequest(apiKey, JENKINS_PLUGIN_JOB_RUN_COMPLETE, payload, trustAllCertificates, jobRunCompleteDataZipFile, proxyConfig);
            LOGGER.log(Level.FINEST, "perform Generic Request completed, {0}", genericResponse);
            JobRunCompleteResponse jobRunCompleteResponse = mapper.readValue(genericResponse.getPayload(), JobRunCompleteResponse.class);
            if (jobRunCompleteResponse.getErrorMessage() != null) {
                LOGGER.log(Level.INFO, "Job could not be completed, Error from Server : " + jobRunCompleteResponse.getErrorMessage());
            }
            return jobRunCompleteResponse.getRunIds();
        } finally {
            if ((jobRunCompleteDataZipFile != null) && (jobRunCompleteDataZipFile.exists())) {
                if (jobRunCompleteDataZipFile.delete()) {
                    LOGGER.log(Level.FINEST, "Successfully deleted job run complete data zip file {0}", jobRunCompleteDataZipFile);
                } else {
                    LOGGER.log(Level.WARNING, "Failed to delete job run complete data zip file {0}", jobRunCompleteDataZipFile);
                }
            }
            if ((jobRunCompleteDataDirectory != null) && (jobRunCompleteDataDirectory.exists())) {
                LOGGER.log(Level.FINEST, "Delete Job Run Complete Data Directory starting");
                try {
                    FileUtils.deleteDirectory(jobRunCompleteDataDirectory);
                    LOGGER.log(Level.FINEST, "Delete Job Run Complete Data Directory completed");
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Delete Job Run Complete Data Directory failed", e);
                }
            } else {
                LOGGER.log(Level.FINEST, "jobRunCompleteDataDirectory is null or does not exist cannot delete");
            }
        }
    }
}
