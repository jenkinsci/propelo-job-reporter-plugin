package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * End-of-stage phase marker captured by {@code seiReportPhase} for single-pipeline CI/CD correlation.
 * Lists are always mutable {@link ArrayList}s so Jenkins XStream can persist this on {@link hudson.model.Run}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhaseEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("environment")
    private String environment;

    @JsonProperty("stage_name")
    private String stageName;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("result")
    private String result;

    @JsonProperty("scm_commit_ids")
    private List<String> scmCommitIds;

    @JsonProperty("artifacts")
    private List<CiCdJobRunArtifact> artifacts;

    /** Required for Jenkins XStream persistence. */
    public PhaseEvent() {
        this.scmCommitIds = new ArrayList<>();
        this.artifacts = new ArrayList<>();
    }

    public PhaseEvent(String phase, String environment, String stageName, long startTime, long endTime,
                      String result, List<String> scmCommitIds, List<CiCdJobRunArtifact> artifacts) {
        this.phase = phase;
        this.environment = environment;
        this.stageName = stageName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
        this.scmCommitIds = scmCommitIds == null ? new ArrayList<>() : new ArrayList<>(scmCommitIds);
        this.artifacts = artifacts == null ? new ArrayList<>() : new ArrayList<>(artifacts);
    }

    public String getPhase() {
        return phase;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getStageName() {
        return stageName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getResult() {
        return result;
    }

    public List<String> getScmCommitIds() {
        return scmCommitIds;
    }

    public List<CiCdJobRunArtifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhaseEvent that = (PhaseEvent) o;
        return startTime == that.startTime
                && endTime == that.endTime
                && Objects.equals(phase, that.phase)
                && Objects.equals(environment, that.environment)
                && Objects.equals(stageName, that.stageName)
                && Objects.equals(result, that.result)
                && Objects.equals(scmCommitIds, that.scmCommitIds)
                && Objects.equals(artifacts, that.artifacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phase, environment, stageName, startTime, endTime, result, scmCommitIds, artifacts);
    }

    @Override
    public String toString() {
        return "PhaseEvent{"
                + "phase='" + phase + '\''
                + ", environment='" + environment + '\''
                + ", stageName='" + stageName + '\''
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", result='" + result + '\''
                + ", scmCommitIds=" + scmCommitIds
                + ", artifacts=" + artifacts
                + '}';
    }
}
