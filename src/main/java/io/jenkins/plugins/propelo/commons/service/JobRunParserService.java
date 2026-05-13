package io.jenkins.plugins.propelo.commons.service;

import com.google.common.base.Objects;
import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.security.ACL;
import hudson.triggers.SCMTrigger;
import io.jenkins.plugins.propelo.commons.models.JobNameDetails;
import io.jenkins.plugins.propelo.commons.models.JobRunDetail;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.models.JobTrigger;
import jenkins.model.Jenkins;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.jenkins.plugins.propelo.commons.service.JobFullNameConverter.convertJobFullNameToJobNormalizedFullName;

public class JobRunParserService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    public static final Pattern PARAM_TYPE_REGEX = Pattern.compile("\\((.*)\\)(.*)", Pattern.DOTALL);
    private static final Set<String> PARAM_TYPES_TO_CAPTURE = new HashSet<>(Arrays.asList("BooleanParameterValue", "TextParameterValue", "StringParameterValue"));
    private static final Pattern PATTERN_JOBS_JOB_BRANCHES_BRANCH = Pattern.compile("^.*\\/jobs\\/(.*)\\/branches\\/(.*)$");
    private static final Pattern PATTERN_JOBS_JOB_MODULES_MODULE = Pattern.compile("^.*\\/jobs\\/(.*)\\/modules\\/(.*)$");
    private static final Pattern PATTERN_JOB_BRANCHES_BRANCH = Pattern.compile("^(.*)\\/branches\\/(.*)$");
    private static final Pattern PATTERN_JOB_MODULES_BRANCH = Pattern.compile("^(.*)\\/modules\\/(.*)$");
    private static final Pattern PATTERN_JOBS_JOB = Pattern.compile("^.*\\/jobs\\/(.*)$");

    public JobRunParserService() {
    }
    private String extractJobRelativePath(File jobDir, File hudsonHome) throws IOException {
        if(hudsonHome == null){
            throw new IOException("Hudson Home is null, aborting buildConfigHistoryFile!");
        }
        final String jenkinsRootDir = hudsonHome.getPath();
        LOGGER.finest("JobRunParserService.extractJobRelativePath jenkinsRootDir = " + jenkinsRootDir);

        final File jenkinsJobsDir = new File(jenkinsRootDir, "jobs");
        LOGGER.finest("JobRunParserService.extractJobRelativePath jenkinsJobsDir = " + jenkinsJobsDir);

        if (!jobDir.getAbsolutePath().startsWith(jenkinsJobsDir.getAbsolutePath())) {
            throw new IOException(
                    "Trying to get job dir for object outside of Jenkins: "
                            + jobDir.getAbsolutePath());
        }

        String jobRelativePath = jobDir.getAbsolutePath().substring(jenkinsJobsDir.getAbsolutePath().length()+ 1);
        LOGGER.finest("JobRunParserService.extractJobRelativePath jobRelativePath = " + jobRelativePath);
        return jobRelativePath;
    }

    protected JobNameDetails parseJobRelativePath(String jobRelativePath){
        Matcher matcher = PATTERN_JOBS_JOB_BRANCHES_BRANCH.matcher(jobRelativePath);
        if(matcher.matches()){
            String jobName = matcher.group(1);
            String branchName = matcher.group(2);
            return new JobNameDetails(jobName, branchName, jobRelativePath, null, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
        }

        matcher = PATTERN_JOBS_JOB_MODULES_MODULE.matcher(jobRelativePath);
        if(matcher.matches()){
            String jobName = matcher.group(1);
            String moduleName = matcher.group(2);
            return new JobNameDetails(jobName, null, jobRelativePath, moduleName, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
        }

        matcher = PATTERN_JOB_BRANCHES_BRANCH.matcher(jobRelativePath);
        if(matcher.matches()){
            String jobName = matcher.group(1);
            String branchName = matcher.group(2);
            return new JobNameDetails(jobName, branchName, jobRelativePath, null, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
        }

        matcher = PATTERN_JOB_MODULES_BRANCH.matcher(jobRelativePath);
        if(matcher.matches()){
            String jobName = matcher.group(1);
            String moduleName = matcher.group(2);
            return new JobNameDetails(jobName, null, jobRelativePath, moduleName, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
        }

        matcher = PATTERN_JOBS_JOB.matcher(jobRelativePath);
        if(matcher.matches()){
            String jobName = matcher.group(1);
            return new JobNameDetails(jobName, null, jobRelativePath, null, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
        }
        return new JobNameDetails(jobRelativePath, null, jobRelativePath, null, convertJobFullNameToJobNormalizedFullName(jobRelativePath));
    }

    /*
    jobDir example: /var/jenkins_home/jobs/Pipe3
     */
    public JobNameDetails parseJobNameBranchNameJobFullPath(File jobDir, File hudsonHome) throws IOException {
        String jobRelativePath = extractJobRelativePath(jobDir, hudsonHome);
        if(StringUtils.isBlank(jobRelativePath)) {
            throw new IOException("Failed to extract jobRelativePath, jobRelativePath is null or empty!");
        }
        return parseJobRelativePath(jobRelativePath);
    }

    private List<JobRunParam> parseParameters(Run build) {
        LOGGER.finest("JobRunParserService::parseParameters Starting");
        ParametersAction parameters = build.getAction(ParametersAction.class);
        
        if (parameters == null) {
            LOGGER.finest("ShowParametersBuildAction parameters = null");
            return Collections.EMPTY_LIST;
        }

        LOGGER.finest("ShowParametersBuildAction parameters = " + parameters.getParameters().toString());
        List<JobRunParam> jobRunParams = new ArrayList<>();
        for(ParameterValue p : parameters.getParameters()){
            if (p == null) {
                continue;
            }
            Matcher matcher = PARAM_TYPE_REGEX.matcher(p.toString());
            if(!matcher.matches()){
                LOGGER.finest("ShowParametersBuildAction matcher.matches = false");
                continue;
            }
            String type = matcher.group(1);
            if (StringUtils.isBlank(type)){
                LOGGER.finest("ShowParametersBuildAction type is null or empty");
                continue;
            }
            LOGGER.finest("ShowParametersBuildAction type = " + type);
            if(!PARAM_TYPES_TO_CAPTURE.contains(type)){
                LOGGER.finest("ShowParametersBuildAction type is not valid for capture. type: " + type);
                continue;
            }

            String value = (p.getValue() != null) ? p.getValue().toString() : null;
            jobRunParams.add(new JobRunParam(type, p.getName(), value));
        }
        LOGGER.finest("JobRunParserService::parseParameters Ending");
        return jobRunParams;
    }

    private Job extractJob (Cause.UpstreamCause upstreamCause) {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if(jenkins == null){
            return null;
        }
        Job job = jenkins.getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
        return job;
    }
    
    private Run getUpStreamBuild (Run build){
        // If build has been triggered form an upstream build, get UserCause from there to set user build variables
        Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause) build.getCause(Cause.UpstreamCause.class);
        LOGGER.finest("JobRunParserService::getCurrentUser before upstreamCause loop");
        while (upstreamCause != null) {
            LOGGER.finest("upstreamCause != null");
            Job job = extractJob(upstreamCause);
            if (job != null) {
                LOGGER.finest("job != null");
                LOGGER.finest("job = " + job.toString());
                Run newBuild = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
                if (newBuild != null) {
                    LOGGER.finest("newBuild != null");
                    upstreamCause = (Cause.UpstreamCause) build.getCause(Cause.UpstreamCause.class);
                    build = newBuild;
                } else {
                    LOGGER.finest("newBuild == null");
                    upstreamCause = null;
                }
            } else {
                LOGGER.finest("job == null");
                upstreamCause = null;
            }
        }
        LOGGER.finest("JobRunParserService::getCurrentUser after upstreamCause loop");
        LOGGER.finest("build = " + build.toString());
        return build;
    }

    private String getCurrentUser(Run build){
        build = getUpStreamBuild(build);

        // set BUILD_USER_NAME to fixed value if the build was triggered by a change in the scm
        LOGGER.finest("Before scmTriggerCause");
        SCMTrigger.SCMTriggerCause scmTriggerCause = (SCMTrigger.SCMTriggerCause) build.getCause(SCMTrigger.SCMTriggerCause.class);
        LOGGER.finest("After scmTriggerCause");
        if (scmTriggerCause != null) {
            LOGGER.finest("JobRunParserService::getCurrentUser currentUser = SCMTrigger");
            return "SCMTrigger";
        }

        return getUserCauseUser(build);
    }

    private String getUserCauseUser(final Run build){
        /* Try to use UserIdCause to get & set jenkins user build variables */
        LOGGER.finest("Before userIdCause");
        Cause.UserIdCause userIdCause = (Cause.UserIdCause) build.getCause(Cause.UserIdCause.class);
        LOGGER.finest("After userIdCause");
        if(userIdCause != null) {
            LOGGER.finest("JobRunParserService::getCurrentUser currentUser = " + userIdCause.getUserId());
            return userIdCause.getUserId();
        }

        // Try to use deprecated UserCause to get & set jenkins user build variables
        LOGGER.finest("Before deprecated userIdCause");
        Cause.UserCause userCause = (Cause.UserCause) build.getCause(Cause.UserCause.class);
        LOGGER.finest("After deprecated userIdCause");
        if(userCause != null) {
            LOGGER.finest("JobRunParserService::getCurrentUser currentUser = " + userCause.getUserName());
            return userCause.getUserName();
        }
        LOGGER.finest("JobRunParserService::getCurrentUser currentUser = " + ACL.SYSTEM_USERNAME);
        return ACL.SYSTEM_USERNAME;
    }

    private String getUserCauseUser(final Cause cause){
        // TODO: merge both methods if safe to do so...
        if (cause == null) {
            LOGGER.finest("JobRunParserService::getUserCauseUser currentUser = " + ACL.SYSTEM_USERNAME);
            return ACL.SYSTEM_USERNAME;
        }
        /* Try to use UserIdCause to get & set jenkins user build variables */
        switch(cause.getClass().getSimpleName()){
            case "UserIdCause":
                Cause.UserIdCause userIdCause = (Cause.UserIdCause) cause;
                LOGGER.finest("JobRunParserService::getUserCauseUser currentUser = " + userIdCause.getUserId());
                return (String) ObjectUtils.defaultIfNull(userIdCause.getUserId(), ACL.ANONYMOUS_USERNAME);
            case "UserCause":
                Cause.UserCause userCause = (Cause.UserCause) cause;
                LOGGER.finest("JobRunParserService::getUserCauseUser currentUser = " + userCause.getUserName());
                return (String) ObjectUtils.defaultIfNull(userCause.getUserName(), ACL.ANONYMOUS_USERNAME);
            default:
                LOGGER.finest("JobRunParserService::getUserCauseUser currentUser = " + ACL.SYSTEM_USERNAME);
                return ACL.SYSTEM_USERNAME;
        }
    }

    /*
    Will return null in case of errors.
     */
    public JobRunDetail parseJobRun(Run build, File hudsonHome){
        LOGGER.finest("JobRunParserService::parseJobRun Starting");
        if (build == null){
            return null;
        }
        LOGGER.log( Level.FINEST, "run.getRootDir() = {0}", build.getRootDir());
        LOGGER.log(Level.FINEST, "run.getParent() = {0}", build.getParent());
        LOGGER.log(Level.FINEST, "run.getParent().getRootDir() = {0}", build.getParent().getRootDir());
        File jobDir = build.getParent().getRootDir();

        JobNameDetails jobNameDetails;
        try {
            jobNameDetails = parseJobNameBranchNameJobFullPath(jobDir, hudsonHome);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error parsing job name, branch name, job full path!", e);
            return null;
        }
        List<JobRunParam> jobRunParams = parseParameters(build);
        String currentUser = getCurrentUser(build);
        LOGGER.finest("currentUser = " + currentUser);
        long startTime = build.getTimestamp().getTimeInMillis();
        String result = (build.getResult() != null) ? build.getResult().toString() : null;
        long duration = build.getDuration();
        long buildNumber = build.getNumber();
        Set<JobTrigger> triggerChain = buildTriggerChain(build, hudsonHome);
        JobRunDetail jobRunDetail = new JobRunDetail(jobNameDetails.getJobName(), jobRunParams, currentUser, startTime,
                result, duration, buildNumber, null, jobNameDetails.getBranchName(), jobNameDetails.getJobFullName(),
                jobNameDetails.getModuleName(), jobNameDetails.getJobNormalizedFullName(), triggerChain);
        LOGGER.finest("JobRunParserService::parseJobRun Ending");
        return jobRunDetail;
    }



    private JobTrigger buildTriggerChain(final Object cause, File hudsonHome) {
        String triggerId = "";
        final String triggerType = cause.getClass().getSimpleName();
        switch(triggerType){
            case "UpstreamCause":
            case "BuildUpstreamCause":
                Run upstream = ((Cause.UpstreamCause) cause).getUpstreamRun();
                try {
                    if (upstream == null || upstream.getParent() == null || upstream.getParent().getRootDir() == null) {
                        return null;
                    }
                    triggerId = parseJobNameBranchNameJobFullPath(upstream.getParent().getRootDir(), hudsonHome).getJobFullName();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error parsing job name, branch name, job full path! Using upstream build name '" + upstream.getFullDisplayName() + "'", e);
                    triggerId = upstream.getFullDisplayName();
                }
                return new JobTrigger(triggerId, triggerType, String.valueOf(upstream.getNumber()), buildTriggerChain(upstream, hudsonHome));
            case "UserCause":
                triggerId = getUserCauseUser((Cause.UserCause) cause);
                break;
            case "UserIdCause":
                triggerId = getUserCauseUser((Cause.UserIdCause) cause);
                break;
            case "RemoteCause":
                triggerId = ((Cause.RemoteCause) cause).getAddr();
                break;
            case "GitHubPushCause":
                LOGGER.info("GitHubPushCause, handling it as the SCMTriggerCause");
            case "SCMTriggerCause":
                triggerId = "SCMTrigger";
                break;
            default:
                LOGGER.warning("Using unknown trigger id for unsupported trigger type: " + triggerType);
                triggerId = "unknown";
        }
        return new JobTrigger(triggerId, triggerType);
    }

    @SuppressWarnings("unchecked")
    private Set<JobTrigger> buildTriggerChain(final Run build, File hudsonHome) {
        if (build == null) {
            return Collections.emptySet();
        }
        Set<JobTrigger> triggerChain = new HashSet<>();
        for(Object cause:build.getCauses()){
            JobTrigger trigger = buildTriggerChain(cause, hudsonHome);
            if (trigger == null) {
                LOGGER.warning("Unable to construct a trigger instance form the cause: " + cause);
                continue;
            }
            triggerChain.add(trigger);
        }
        return triggerChain;
    }
}
