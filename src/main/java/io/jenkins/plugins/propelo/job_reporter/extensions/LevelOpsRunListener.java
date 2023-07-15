package io.jenkins.plugins.propelo.job_reporter.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.propelo.commons.models.JobRunCompleteData;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.service.GenericRequestService;
import io.jenkins.plugins.propelo.commons.service.JenkinsConfigSCMService;
import io.jenkins.plugins.propelo.commons.service.JenkinsInstanceGuidService;
import io.jenkins.plugins.propelo.commons.service.JobLogsService;
import io.jenkins.plugins.propelo.commons.service.JobRunCompleteNotificationService;
import io.jenkins.plugins.propelo.commons.service.JobRunGitChangesService;
import io.jenkins.plugins.propelo.commons.service.JobRunParserService;
import io.jenkins.plugins.propelo.commons.service.JobRunPerforceChangesService;
import io.jenkins.plugins.propelo.commons.service.JobSCMService;
import io.jenkins.plugins.propelo.commons.service.LevelOpsPluginConfigService;
import io.jenkins.plugins.propelo.commons.service.ProxyConfigService;
import io.jenkins.plugins.propelo.commons.utils.DateUtils;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import io.jenkins.plugins.propelo.job_reporter.service.JobRunCodeCoverageResultsService;
import io.jenkins.plugins.propelo.job_reporter.service.JobRunCodeCoverageXmlResultsService;
import io.jenkins.plugins.propelo.job_reporter.service.JobRunCompleteDataService;
import io.jenkins.plugins.propelo.job_reporter.service.JobRunPostBuildPublisherResultsService;
import io.jenkins.plugins.propelo.job_reporter.service.JobRunTestResultsService;
import jenkins.model.Jenkins;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.propelo.commons.plugins.Common.JOBS_DATA_DIR_NAME;
import static io.jenkins.plugins.propelo.commons.plugins.Common.RUN_HISTORY_COMPLETE_DATA_DIRECTORY;
import static io.jenkins.plugins.propelo.commons.plugins.Common.RUN_HISTORY_COMPLETE_DATA_ZIP_FILE;

@Extension
public class LevelOpsRunListener extends RunListener<Run> {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final PropeloPluginImpl plugin = PropeloPluginImpl.getInstance();
    private static final ObjectMapper mapper = JsonUtils.buildObjectMapper();
    private static final ObjectMapper xmlMapper = new XmlMapper();
    private static final String JOB_SUCCESSFUL = "SUCCESS";

    public LevelOpsRunListener() {
    }


    private String getJenkinsInstanceUrl() {
        return Jenkins.get().getRootUrl();
    }

    private String getJenkinsInstanceGuid() {
        return new JenkinsInstanceGuidService(plugin.getExpandedLevelOpsPluginDir(), plugin.getDataDirectory(), plugin.getDataDirectoryWithVersion()).createOrReturnInstanceGuid();
    }

    private File buildJobRunCompleteDataDirectory(final String jobFullName, final Long jobRunNumber) throws IOException {
        File jobRunCompleteDataDirectory = Paths.get(plugin.getDataDirectoryWithRotation().getAbsolutePath(), JOBS_DATA_DIR_NAME, jobFullName, String.format(RUN_HISTORY_COMPLETE_DATA_DIRECTORY, jobRunNumber)).toFile();
        return jobRunCompleteDataDirectory;
        //return FileUtils.createDirectoryRecursively(jobRunCompleteDataDirectory);
    }

    private File buildJobRunCompleteDataZipFile(final String jobFullName, final Long jobRunNumber) {
        File jobRunCompleteDataZipFile = Paths.get(plugin.getDataDirectoryWithRotation().getAbsolutePath(), JOBS_DATA_DIR_NAME, jobFullName, String.format(RUN_HISTORY_COMPLETE_DATA_ZIP_FILE, jobRunNumber)).toFile();
        return jobRunCompleteDataZipFile;
    }

    private JobRunCompleteData gatherJobRunCompleteData(Run run, JobRunDetail jobRunDetail) {
        File jobRunCompleteDataDirectory;
        try {
            jobRunCompleteDataDirectory = buildJobRunCompleteDataDirectory(jobRunDetail.getJobFullName(), jobRunDetail.getBuildNumber());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating job run complete data directory, cannot gather complete data", e);
            return null;
        }

        final ProxyConfigService.ProxyConfig proxyConfig = ProxyConfigService.generateConfigFromJenkinsProxyConfiguration(Jenkins.getInstanceOrNull());

        File jobRunCompleteDataZipFile = buildJobRunCompleteDataZipFile(jobRunDetail.getJobFullName(), jobRunDetail.getBuildNumber());
        JobRunCompleteDataService jobRunCompleteDataService = new JobRunCompleteDataService(plugin, mapper, proxyConfig);
        JobRun jobRun = jobRunCompleteDataService.gatherJobRunData(jobRunDetail, jobRunCompleteDataDirectory);
        LOGGER.log(Level.FINEST, "jobRun = {0}", jobRun);

        JobRunTestResultsService jobRunTestResultsService = new JobRunTestResultsService();
        LOGGER.log(Level.FINEST, "gatherJobRunTestResults starting");
        jobRunTestResultsService.gatherJobRunTestResults(run, jobRunCompleteDataDirectory);
        LOGGER.log(Level.FINEST, "gatherJobRunTestResults completed");

        JobRunCodeCoverageResultsService jobRunCodeCoverageResultsService = new JobRunCodeCoverageResultsService();
        LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageResults starting");
        jobRunCodeCoverageResultsService.gatherJobRunCodeCoverageResults(run, jobRunCompleteDataDirectory);
        LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageResults completed");

        LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageResults for copying files from job to data directory started");
        JobRunPostBuildPublisherResultsService jobRunPostBuildPublisherResultsService = new JobRunPostBuildPublisherResultsService();
        boolean publishedResultsGathered = jobRunPostBuildPublisherResultsService.gatherJobRunCodeCoverageResults(run, jobRunCompleteDataDirectory);
        LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageResults for copying files from job to data directory completed");
        if (!publishedResultsGathered) {
            JobRunCodeCoverageXmlResultsService jobRunCodeCoverageXmlResultsService = new JobRunCodeCoverageXmlResultsService();
            LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageXmlResults starting");
            jobRunCodeCoverageXmlResultsService.gatherJobRunCodeCoverageXmlResults(run, plugin.getBullseyeXmlResultPaths(), jobRunCompleteDataDirectory);
            LOGGER.log(Level.FINEST, "gatherJobRunCodeCoverageXmlResults completed");
        }
        return new JobRunCompleteData(jobRun, jobRunCompleteDataDirectory, jobRunCompleteDataZipFile);
    }

    @Override
    public void onFinalized(Run run) {
        try {
            LOGGER.finer("LevelOpsRunListener.onCompleted");
            if (plugin.getLevelOpsApiKey() != null && plugin.isExpandedLevelOpsPluginPathNullOrEmpty()) {
                LOGGER.log(Level.SEVERE, "Propelo Plugin Directory is invalid, cannot process job run completed event! path: " + plugin.getLevelOpsPluginPath());
                return;
            }
            if (StringUtils.isBlank(plugin.getLevelOpsApiKey().getPlainText())) {
                LOGGER.log(Level.FINE, "Propelo Api Key is null or empty, will not collect data");
                return;
            }

            if (run == null) {
                LOGGER.finest("Incomplete input received (run can't be null)");
                return;
            }
            LOGGER.log(Level.FINEST, "run = {0}", run);

            JobRunParserService jobRunParserService = new JobRunParserService();
            JobRunDetail jobRunDetail = jobRunParserService.parseJobRun(run, plugin.getHudsonHome());
            if (jobRunDetail == null) {
                return;
            }
            LOGGER.log(Level.FINE, "Starting processing complete event jobFullName={0}, build number = {1}", new Object[]{jobRunDetail.getJobFullName(), jobRunDetail.getBuildNumber()});
            LOGGER.finest("jobRunDetail = " + jobRunDetail);
            JobRunGitChangesService jobRunGitChangesService = new JobRunGitChangesService(mapper, plugin.getDataDirectoryWithRotation());
            List<String> scmCommitIds = jobRunGitChangesService.parseGitCommitsForRun(run, jobRunDetail);
            if (scmCommitIds == null)
                scmCommitIds = new ArrayList<>();
            LOGGER.log(Level.FINEST, "scmCommitIds = {0}", scmCommitIds);

            JobRunPerforceChangesService jobRunPerforceChangesService = new JobRunPerforceChangesService(xmlMapper);
            List<String> perforceCommitIds = jobRunPerforceChangesService.parsePerforceCommitsForRun(run);
            if (perforceCommitIds == null)
                perforceCommitIds = new ArrayList<>();
            JenkinsConfigSCMService jenkinsConfigSCMService = new JenkinsConfigSCMService();
            JobSCMService jobSCMService = new JobSCMService(jenkinsConfigSCMService);
            JenkinsConfigSCMService.SCMResult scmResult = jobSCMService.parseSCMConfigForJob(plugin.getHudsonHome(), jobRunDetail.getJobFullName());
            LOGGER.log(Level.FINEST,"scmResult = {0}", scmResult);

            JobRunCompleteData jobRunCompleteData = gatherJobRunCompleteData(run, jobRunDetail);
            scmCommitIds.addAll(perforceCommitIds);
            JobLogsService jobLogsService = new JobLogsService();
            UUID failedLogFileUUID = null;

            if (jobRunCompleteData != null && !jobRunDetail.getResult().equalsIgnoreCase(JOB_SUCCESSFUL) && jobRunCompleteData.getJobRun() == null) {
                //jobRun == null, if BlueOcean is not installed
                failedLogFileUUID = jobLogsService.getLogFailedString(run, jobRunCompleteData.getCompleteDataDirectory());
            }

            ProxyConfigService.ProxyConfig proxyConfig = ProxyConfigService.generateConfigFromJenkinsProxyConfiguration(Jenkins.getInstanceOrNull());

            performJobRunCompleteNotification(jobRunDetail, scmResult.getUrl(), scmResult.getUserName(),
                    getJenkinsInstanceGuid(), plugin.getJenkinsInstanceName(), getJenkinsInstanceUrl(),
                    jobRunCompleteData, scmCommitIds, failedLogFileUUID, proxyConfig);
            // deleting data directory
            deleteJobRunDataCompleteDirectoryContents();
            //performJobRunCompleteNotification fn logs have this.
            //LOGGER.log(Level.INFO, "Completed processing complete event jobFullName={0}, build number = {1}", new Object[]{jobRunDetail.getJobFullName(), jobRunDetail.getBuildNumber()});
        } catch (Exception e) {
            // TODO: Delete data files after having retried a few configurable number of times
            LOGGER.log(Level.SEVERE, "Error in RunListener onCompleted!", e);
            reportError(ExceptionUtils.getFullStackTrace(e));
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Deletes data transmitted to propelo
     */
    private void deleteJobRunDataCompleteDirectoryContents() {
        LOGGER.log(Level.FINEST, "Delete Job Run Complete Data Directory (date-$date) starting");
        File dataDirectoryWithVersion = plugin.getDataDirectoryWithVersion();
        if (dataDirectoryWithVersion == null || !dataDirectoryWithVersion.exists()) {
            LOGGER.log(Level.FINE, "Skipping job run complete data directory delete... data directory = {0}", dataDirectoryWithVersion);
            return;
        }
        String todayDirName = DateUtils.getDateFormattedDirName();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(
                dataDirectoryWithVersion.toPath(),
                (entry) -> {
                    boolean use = !entry.getFileName().toString().equalsIgnoreCase(todayDirName);
                    LOGGER.log(Level.FINER, "Filtering files... accept {0}? {1}", new Object[]{todayDirName, use});
                    return use;
                })) {
            ds.forEach(item -> {
                LOGGER.log(Level.FINE, "Deleting old historic report: {0}", item.toString());
                FileUtils.deleteQuietly(item.toFile());
            });

        } catch (SecurityException | IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> {
                return String.format("Unable to delete all files from the historic reports in the directory '%s' other than %s", dataDirectoryWithVersion.toPath(), todayDirName);
            });
        }
    }

    private void performJobRunCompleteNotification(JobRunDetail jobRunDetail, String scmUrl, String scmUserId, String jenkinsInstanceGuid, String jenkinsInstanceName,
                                                   String jenkinsInstanceUrl, JobRunCompleteData jobRunCompleteData, List<String> scmCommitIds, UUID failedLogFileUUID, final ProxyConfigService.ProxyConfig proxyConfig) {

        LOGGER.finest("Send Job Runs Completed Notifications to Propelo is true, performing job run complete notification");
        if (StringUtils.isBlank(jenkinsInstanceGuid)) {
            LOGGER.severe("Jenkins Instance UID is not valid... make sure that the Propelo's work directory provided in the settings page is accessible and writeable by the user running the Jenkins process... The job report will be sent but very likely it won't be usable until the issue in this Jenkins instance is fixed. Please contact Propelo's support to get further assistance.");
        }

        JobRunCompleteNotificationService jobRunCompleteNotificationService = new JobRunCompleteNotificationService(LevelOpsPluginConfigService.getInstance().getLevelopsConfig().getApiUrl(), mapper);
        List<String> runIds;
        try {
            String jobFullName = (jobRunDetail != null) ? jobRunDetail.getJobFullName() : null;
            long jobRunNumber = (jobRunDetail != null) ? jobRunDetail.getBuildNumber() : 0;
            runIds = jobRunCompleteNotificationService.submitJobRunCompleteRequest(plugin.getLevelOpsApiKey().getPlainText(), jobRunDetail,
                    scmUrl, scmUserId, jenkinsInstanceGuid, jenkinsInstanceName, jenkinsInstanceUrl, plugin.isTrustAllCertificates(), jobRunCompleteData,
                    scmCommitIds, failedLogFileUUID, proxyConfig);
            LOGGER.log(Level.FINE, "Successfully submitted job run complete event to LevelOps, jobFullName = {0}, jobRunNumber = {1}, runIds = {2}", new Object[]{jobFullName, jobRunNumber, runIds});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending job run complete event!", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending job run complete event!", e);
        }
    }

    private void reportError(final String payload) {
        GenericRequestService genericRequestService = new GenericRequestService(LevelOpsPluginConfigService.getInstance().getLevelopsConfig().getApiUrl(), mapper);
        try {
            genericRequestService.performGenericRequest(plugin.getLevelOpsApiKey().getPlainText(), "pluginErrorReport", payload, plugin.isTrustAllCertificates(), null, ProxyConfigService.generateConfigFromJenkinsProxyConfiguration(Jenkins.getInstanceOrNull()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unabled to report errors back to Propelo....", e);
        }
    }
}