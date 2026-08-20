package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.propelo.commons.models.JobRunParam;
import io.jenkins.plugins.propelo.commons.models.JobTrigger;
import io.jenkins.plugins.propelo.commons.models.blue_ocean.JobRun;

import java.util.List;
import java.util.Set;

public class JobRunCompleteRequest {
    @JsonProperty("job_name")
    private String jobName;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("job_run_params")
    private List<JobRunParam> jobRunParams;

    @JsonProperty("repo_url")
    private String repoUrl;

    @JsonProperty("scm_user_id")
    private String scmUserId;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("result")
    private String result;

    @JsonProperty("duration")
    private long duration;

    @JsonProperty("build_number")
    private long buildNumber;

    @JsonProperty("jenkins_instance_guid")
    private String jenkinsInstanceGuid;

    @JsonProperty("jenkins_instance_name")
    private String jenkinsInstanceName;

    @JsonProperty("jenkins_instance_url")
    private String jenkinsInstanceUrl;

    @JsonProperty("job_run")
    private JobRun jobRun;

    @JsonProperty("job_full_name")
    private final String jobFullName;

    @JsonProperty("job_normalized_full_name")
    private final String jobNormalizedFullName;

    @JsonProperty("branch_name")
    private final String branchName;

    @JsonProperty("module_name")
    private final String moduleName;

    @JsonProperty("scm_commit_ids")
    private final List<String> scmCommitIds;

    @JsonProperty("trigger_chain")
    private final Set<JobTrigger> triggerChain;

    @JsonProperty("artifacts")
    private final List<CiCdJobRunArtifact> artifacts;

    @JsonProperty("ci")
    private final Boolean ci;

    @JsonProperty("cd")
    private final Boolean cd;


    public JobRunCompleteRequest(String jobName, String userId, List<JobRunParam> jobRunParams, String repoUrl, String scmUserId,
                                 long startTime, String result, long duration, long buildNumber, String jenkinsInstanceGuid, String jenkinsInstanceName, String jenkinsInstanceUrl, JobRun jobRun, String jobFullName, String jobNormalizedFullName, String branchName, String moduleName, List<String> scmCommitIds, Set<JobTrigger> triggerChain,
                                 List<CiCdJobRunArtifact> artifacts, Boolean ci, Boolean cd) {
        this.jobName = jobName;
        this.userId = userId;
        this.jobRunParams = jobRunParams;
        this.repoUrl = repoUrl;
        this.scmUserId = scmUserId;
        this.startTime = startTime;
        this.result = result;
        this.duration = duration;
        this.buildNumber = buildNumber;
        this.jenkinsInstanceGuid = jenkinsInstanceGuid;
        this.jenkinsInstanceName = jenkinsInstanceName;
        this.jenkinsInstanceUrl = jenkinsInstanceUrl;
        this.jobRun = jobRun;
        this.jobFullName = jobFullName;
        this.jobNormalizedFullName = jobNormalizedFullName;
        this.branchName = branchName;
        this.moduleName = moduleName;
        this.scmCommitIds = scmCommitIds;
        this.triggerChain = triggerChain;
        this.artifacts = artifacts;
        this.ci = ci;
        this.cd = cd;
    }

    public String getJobName() {
        return jobName;
    }

    public String getUserId() {
        return userId;
    }

    public List<JobRunParam> getJobRunParams() {
        return jobRunParams;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public String getScmUserId() {
        return scmUserId;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getResult() {
        return result;
    }

    public long getDuration() {
        return duration;
    }

    public long getBuildNumber() {
        return buildNumber;
    }

    public String getJenkinsInstanceGuid() {
        return jenkinsInstanceGuid;
    }

    public String getJenkinsInstanceName() {
        return jenkinsInstanceName;
    }

    public String getJenkinsInstanceUrl() {
        return jenkinsInstanceUrl;
    }

    public JobRun getJobRun() {
        return jobRun;
    }

    public String getJobFullName() {
        return jobFullName;
    }

    public String getJobNormalizedFullName() {
        return jobNormalizedFullName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<String> getScmCommitIds() {
        return scmCommitIds;
    }

    public Set<JobTrigger> getTriggerChain() {
        return triggerChain;
    }

    public List<CiCdJobRunArtifact> getArtifacts() {
        return artifacts;
    }

    public Boolean getCi() {
        return ci;
    }

    public Boolean getCd() {
        return cd;
    }
}
