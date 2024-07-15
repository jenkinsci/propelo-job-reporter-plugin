package io.jenkins.plugins.propelo.job_reporter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Action;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Cause;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Job;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Node;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Organization;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.Step;
import io.jenkins.plugins.propelo.commons.service.BlueOceanRestClient;
import io.jenkins.plugins.propelo.commons.service.JobFullNameConverter;
import io.jenkins.plugins.propelo.commons.service.ProxyConfigService;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.propelo.commons.utils.JobUtils.writeLogData;


public class JobRunCompleteDataService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static int MAX_RECURSION_LEVELS = 50;

    private final PropeloPluginImpl plugin;
    private final ObjectMapper mapper;
    private final ProxyConfigService.ProxyConfig proxyConfig;

    public JobRunCompleteDataService(PropeloPluginImpl plugin, ObjectMapper mapper, final ProxyConfigService.ProxyConfig proxyConfig) {
        this.plugin = plugin;
        this.mapper = mapper;
        this.proxyConfig = proxyConfig;
    }

    //region Get Org Name
    private String getOrganizationName(BlueOceanRestClient restClient){
        List<Organization> organizations = null;
        try {
            organizations = restClient.getOrganizations();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException!!", e);
            return null;
        }
        if (CollectionUtils.isEmpty(organizations)){
            return null;
        }
        //If we get only one org, use that org name
        if(organizations.size() == 1){
            return organizations.get(0).getName();
        }
        //If we get multiple orgs, we check if any org name is jenkins, if jenkins org name exists we use it
        Set<String> orgNames = new HashSet<>();
        for (Organization org : organizations){
            orgNames.add(org.getName());
        }
        if(orgNames.contains("jenkins")) {
            return "jenkins";
        }
        //If we get multiple orgs and org name "jenkins" does not exist, we will use the first org name
        return organizations.get(0).getName();
    }
    //endregion



    /*
    Get Org
    Get Job
    Get Job Run
    Get Job Run Nodes
        If nodes is empty - Get Job Run Logs
        If nodes is not empty
            For each node - Get Job Run Node Logs
            For each node - Get child Job Runs
    Get child Job Runs
     */
    public JobRun gatherJobRunData(JobRunDetail jobRunDetail, File jobRunCompleteDataDirectory){
        if(jobRunDetail == null){
            return null;
        }
        if(StringUtils.isEmpty(plugin.getJenkinsUserName())
                || plugin.getJenkinsUserToken() == null
                || StringUtils.isEmpty(plugin.getJenkinsUserToken().getPlainText())) {
            return null;
        }

        BlueOceanRestClient restClient = new BlueOceanRestClient(plugin.getJenkinsBaseUrl(), plugin.getJenkinsUserName(), plugin.getJenkinsUserToken().getPlainText(), plugin.isTrustAllCertificates(), mapper, proxyConfig);

        //Get Org
        String orgName = getOrganizationName(restClient);
        LOGGER.log(Level.FINER, "orgName = {0}", orgName);
        if(StringUtils.isBlank(orgName)){
            return null;
        }

        String jobNormalizedFullName = JobFullNameConverter.convertJobFullNameToJobNormalizedFullName(jobRunDetail.getJobFullName());
        if(StringUtils.isBlank(jobNormalizedFullName)){
            return null;
        }
        Long jobRunNumber = jobRunDetail.getBuildNumber();

        //Get Job Run
        JobRun jobRun = null;
        try {
            jobRun = restClient.getJobRun(orgName, jobNormalizedFullName, jobRunNumber);
            LOGGER.log(Level.FINEST, "current job run = {0}", jobRun);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting job run!", e);
            return null;
        }

        //Check id cause i.e. upstream build is alraedy complete
        if(CollectionUtils.isNotEmpty(jobRun.getCauses())){
            for(Cause currentCause : jobRun.getCauses()){
                if((StringUtils.isBlank(currentCause.getUpstreamProject())) || (currentCause.getUpstreamBuild() == null)){
                    continue;
                }
                JobRun upStreamJobRun = null;
                try {
                    upStreamJobRun = restClient.getJobRun(orgName, currentCause.getUpstreamProject(), currentCause.getUpstreamBuild());
                    LOGGER.log(Level.FINEST, "upstream job run = {0}", upStreamJobRun);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error getting cause job run!!", e);
                    continue;
                }
                if(!"FINISHED".equals(upStreamJobRun.getState())){
                    LOGGER.log(Level.FINE, "For current job run, upstream job run is not finished, not sending logs for current job run as upstream job run will include it, currentJobFullName = {0}, currentJobNumber = {1}, upStreamJobName = {2}, upStreamJobNumber = {3}", new Object[]{jobRunDetail.getJobFullName(), jobRunDetail.getBuildNumber(), upStreamJobRun.getName(), upStreamJobRun.getId()});
                    return null;
                }
            }
        }
        processJobRun(restClient, jobRun, 1, jobRunCompleteDataDirectory, hasJobFailed(jobRunDetail));
        return jobRun;
    }

    private void processJobRun(BlueOceanRestClient restClient, JobRun jobRun, int level, File currentJobRunCompleteDataDirectory, boolean hasJobFailed) {
        if(level > MAX_RECURSION_LEVELS){
            return;
        }
        assignJobNormalizedFullNameForJobRun(restClient, jobRun);
        LOGGER.log(Level.FINE, "Starting gatherJobRunData for jobNormalizedFullName = {0}, jobRunNumber = {1}", new Object[]{jobRun.getJobNormalizedFullName(), jobRun.getId()});
        List<Node> nodes = getJobNodes(restClient, jobRun);
        LOGGER.log(Level.FINEST, "nodes = {0}", nodes);
        if(CollectionUtils.isEmpty(nodes)) {
            jobRun.setLog(writeLogData(currentJobRunCompleteDataDirectory, getJobRunLogs(restClient, jobRun, hasJobFailed)));
        } else {
            for(Node n : nodes){
                processJobRunNode(restClient, jobRun.getJobNormalizedFullName(), n, level, currentJobRunCompleteDataDirectory);
                jobRun.getStages().add(n);
            }
        }

        //Process child actions
        List<Action> actions = jobRun.getActions();
        if(CollectionUtils.isNotEmpty(actions)){
            for (Action a : actions) {
                JobRun childJobRun = processAction(restClient, a, level +1, currentJobRunCompleteDataDirectory);
                if(childJobRun != null) {
                    jobRun.getChildJobRuns().add(childJobRun);
                }
            }
        }

        int stagesCount = (CollectionUtils.isEmpty(jobRun.getStages())) ?0: jobRun.getStages().size();
        int childJobRunsCount = (CollectionUtils.isEmpty(jobRun.getChildJobRuns())) ? 0 :jobRun.getChildJobRuns().size();
        boolean hasLog = (jobRun.getLog() != null);
        LOGGER.log(Level.FINE, "Fetched jobRun for jobNormalizedFullName = {0}, jobRunNumber = {1}, stagesCount = {2}, childJobRunsCount = {3}, hasLog = {4}", new Object[]{jobRun.getJobNormalizedFullName(), jobRun.getId(), stagesCount, childJobRunsCount, hasLog});
        return;
    }

    private void assignJobNormalizedFullNameForJobRun(BlueOceanRestClient restClient, JobRun jobRun) {
        if ((jobRun == null) || (jobRun.getLinks() == null) || (jobRun.getLinks().getParent() == null) || (StringUtils.isBlank(jobRun.getLinks().getParent().getHref()))) {
            return;
        }
        Job job = null;
        try {
            job = restClient.getJobUsingLink(jobRun.getLinks().getParent().getHref());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting job using link!!", e);
            return;
        }
        jobRun.setJobNormalizedFullName(job.getFullName());
    }

    private JobRun processAction (BlueOceanRestClient restClient, Action a, int level, File currentJobRunCompleteDataDirectory) {
        if((a == null) || (a.getLink() == null) || (StringUtils.isBlank(a.getLink().getHref()))) {
            return null;
        }
        String jobRunHrefLink = a.getLink().getHref();
        JobRun jobRun = null;
        try {
            jobRun = restClient.getJobRunUsingLink(jobRunHrefLink);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting job run using link!!", e);
            return null;
        }
        processJobRun(restClient, jobRun, level +1, currentJobRunCompleteDataDirectory, false);
        return jobRun;
    }

    private List<Step> getJobRunNodeSteps(BlueOceanRestClient restClient, Node n, File currentJobRunCompleteDataDirectory) {
        LOGGER.log(Level.FINEST, "getJobRunNodeSteps starting");
        if((n.getLinks() == null) || (n.getLinks().getSelf() == null) || (StringUtils.isBlank(n.getLinks().getSelf().getHref()))) {
            LOGGER.log(Level.FINEST, "Node Links or Self Link is null or empty!");
            return null;
        }
        List<Step> steps = null;
        try {
            steps = restClient.getJobRunNodeStepsUsingJobRunNodeLink(n.getLinks().getSelf().getHref());
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "Error fetching Steps for Job Run Node", e);
            return null;
        }
        if(CollectionUtils.isEmpty(steps)) {
            return null;
        }
        for(Step currentStep : steps) {
            currentStep.setLog(writeLogData(currentJobRunCompleteDataDirectory, getJobRunNodeStepLogs(restClient, currentStep)));
        }
        LOGGER.log(Level.FINEST, "getJobRunNodeSteps complete steps = {0}", steps);
        return steps;
    }

    private void processJobRunNode(BlueOceanRestClient restClient, String jobNormalizedFullName, Node n, int level, File currentJobRunCompleteDataDirectory){
        if(level > MAX_RECURSION_LEVELS){
            return;
        }

        LOGGER.log(Level.FINE, "Started processing job name = {0} stage = {1}", new Object[]{jobNormalizedFullName, n.getDisplayName()} );

        //For this node, get all steps along with Step Logs
        n.setSteps(getJobRunNodeSteps(restClient, n, currentJobRunCompleteDataDirectory));

        if(CollectionUtils.isEmpty(n.getSteps())) {
            LOGGER.log(Level.FINEST, "Current Node does not have steps, so getting Node logs");
            //For this node, If failed get logs else null logs
            n.setLog(writeLogData(currentJobRunCompleteDataDirectory, getJobRunNodeLogs(restClient, n)));
        } else {
            LOGGER.log(Level.FINEST, "Current Node does have steps, so NOT getting Node logs");
        }

        //Process child actions
        List<Action> actions = n.getActions();
        if(CollectionUtils.isNotEmpty(actions)){
            for(Action a : actions){
                JobRun childJobRun = processAction(restClient, a, level +1, currentJobRunCompleteDataDirectory);
                if (childJobRun != null) {
                    n.getChildJobRuns().add(childJobRun);
                }
            }
        }

        int stepsCount = (CollectionUtils.isEmpty(n.getSteps())) ? 0: n.getSteps().size();
        int childJobRunsCount = (CollectionUtils.isEmpty(n.getChildJobRuns())) ? 0: n.getChildJobRuns().size();
        boolean hasLog = (n.getLog() != null);
        int countStepsWithLogs = 0;
        if(CollectionUtils.isNotEmpty(n.getSteps())) {
            for(Step st : n.getSteps()) {
                if(st.getLog() != null) {
                    countStepsWithLogs++;
                }
            }
        }
        LOGGER.log(Level.FINE, "Completed processing job name = {0} stage = {1}, stepsCount = {2}, childJobRunsCount = {3}, hasLog = {4}, stepsWithLogs = {5}", new Object[]{jobNormalizedFullName, n.getDisplayName(), stepsCount, childJobRunsCount, hasLog, countStepsWithLogs});
    }

    private List<Node> getJobNodes(BlueOceanRestClient restClient, JobRun jobRun) {
        if((jobRun == null) || (jobRun.getLinks() == null) || (jobRun.getLinks().getNodes() == null) || (StringUtils.isBlank(jobRun.getLinks().getNodes().getHref()))) {
            return Collections.emptyList();
        }
        String jobRunNodesHref = jobRun.getLinks().getNodes().getHref();
        try {
            List<Node> nodes = restClient.getJobRunNodesUsingJobRunNodesLink(jobRunNodesHref);
            return nodes;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException!!", e);
            return Collections.emptyList();
        }
    }

    private String getJobRunLogs(BlueOceanRestClient restClient, JobRun jobRun, boolean hasJobFailed) {
        LOGGER.log(Level.FINEST, "getJobRunLogs starting");
        if ((jobRun == null) ||(jobRun.getLinks() == null) || (jobRun.getLinks().getSelf() == null) || (StringUtils.isBlank(jobRun.getLinks().getSelf().getHref()))) {
            LOGGER.log(Level.FINEST, "job run data is incomplete will not fetch logs");
            return null;
        }
        boolean effectiveHasJobFailed = (hasFailed(jobRun.getState(), jobRun.getResult())) || hasJobFailed;
        if(!effectiveHasJobFailed){
            LOGGER.log(Level.FINEST, "job run has not failed");
            return null;
        }
        try {
            String log = restClient.getJobRunLogUsingLink(jobRun.getLinks().getSelf().getHref());
            return log;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching job run logs using link!!", e);
            return null;
        }
    }

    private boolean hasJobFailed(JobRunDetail jobRunDetail){
        String result = (jobRunDetail == null) ? null : jobRunDetail.getResult();
        return (("FAILURE".equals(result)) || ("UNSTABLE".equals(result)) || ("ABORTED".equals(result)));
    }

    private boolean hasFailed(String state, String result){
        return ("FINISHED".equals(state)) && (("FAILURE".equals(result)) || ("UNSTABLE".equals(result)) || ("ABORTED".equals(result)));
    }

    private String getJobRunNodeLogs(BlueOceanRestClient restClient, Node n) {
        if ((n == null) || (n.getLinks() == null) || (n.getLinks().getSelf() == null) || (StringUtils.isBlank(n.getLinks().getSelf().getHref()))) {
            return null;
        }
        if(! hasFailed(n.getState(), n.getResult())){
            return null;
        }
        try {
            String log = restClient.getJobRunNodeLogUsingJobRunNodeLink(n.getLinks().getSelf().getHref());
            return log;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching job run nodr log using link!!", e);
            return null;
        }
    }

    private String getJobRunNodeStepLogs(BlueOceanRestClient restClient, Step s) {
        if ((s == null) || (s.getLinks() == null) || (s.getLinks().getSelf() == null) || (StringUtils.isBlank(s.getLinks().getSelf().getHref()))) {
            return null;
        }
        if(! hasFailed(s.getState(), s.getResult())){
            return null;
        }
        try {
            String log = restClient.getJobRunNodeStepLogUsingJobRunNodeStepLink(s.getLinks().getSelf().getHref());
            return log;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching job run node step logs using link!!", e);
            return null;
        }
    }

}
