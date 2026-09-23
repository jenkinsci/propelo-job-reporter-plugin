package io.jenkins.plugins.propelo.job_reporter.steps;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.CiCdJobRunArtifact;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.PhaseEvent;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import io.jenkins.plugins.propelo.job_reporter.extensions.PropeloStageMarkerAction;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * End-of-stage marker step for single-pipeline CI/CD correlation.
 * Usage: {@code seiReportPhase(phase: 'CI', startTime: ..., scmCommitIds: [...], artifacts: [...])}
 */
public class SeiReportPhaseStep extends Step implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SeiReportPhaseStep.class.getName());
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();

    private final String phase;
    private String environment;
    private String stageName;
    private Long startTime;
    private List<String> scmCommitIds;
    private List<Map<String, Object>> artifacts;
    private String result;

    @DataBoundConstructor
    public SeiReportPhaseStep(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return phase;
    }

    public String getEnvironment() {
        return environment;
    }

    @DataBoundSetter
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStageName() {
        return stageName;
    }

    @DataBoundSetter
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Long getStartTime() {
        return startTime;
    }

    @DataBoundSetter
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public List<String> getScmCommitIds() {
        return scmCommitIds;
    }

    @DataBoundSetter
    public void setScmCommitIds(List<String> scmCommitIds) {
        this.scmCommitIds = scmCommitIds;
    }

    public List<Map<String, Object>> getArtifacts() {
        return artifacts;
    }

    @DataBoundSetter
    public void setArtifacts(List<Map<String, Object>> artifacts) {
        this.artifacts = artifacts;
    }

    public String getResult() {
        return result;
    }

    @DataBoundSetter
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(this, context);
    }

    static String normalizePhase(String phase) {
        if (StringUtils.isBlank(phase)) {
            return null;
        }
        return phase.trim().toUpperCase(Locale.ROOT);
    }

    static void validate(String phase, String environment, Long startTime) {
        String normalized = normalizePhase(phase);
        if (normalized == null || (!"CI".equals(normalized) && !"CD".equals(normalized))) {
            throw new IllegalArgumentException("seiReportPhase: phase must be 'CI' or 'CD'");
        }
        if ("CD".equals(normalized) && StringUtils.isBlank(environment)) {
            throw new IllegalArgumentException("seiReportPhase: environment is required when phase is 'CD'");
        }
        if (startTime == null || startTime <= 0L) {
            throw new IllegalArgumentException("seiReportPhase: startTime (epoch millis) is required");
        }
    }

    static List<CiCdJobRunArtifact> convertArtifacts(List<Map<String, Object>> rawArtifacts) {
        List<CiCdJobRunArtifact> converted = new ArrayList<>();
        if (rawArtifacts == null || rawArtifacts.isEmpty()) {
            return converted;
        }
        for (Map<String, Object> raw : rawArtifacts) {
            if (raw == null) {
                continue;
            }
            converted.add(new CiCdJobRunArtifact(
                    asBoolean(raw.get("input")),
                    asBoolean(raw.get("output")),
                    asString(raw.get("type")),
                    asString(raw.get("location")),
                    asString(raw.get("name")),
                    asString(raw.get("qualifier")),
                    asString(raw.get("hash")),
                    null
            ));
        }
        return converted;
    }

    static List<String> normalizeCommits(String phase, List<String> scmCommitIds) {
        List<String> normalized = new ArrayList<>();
        if (!"CI".equals(normalizePhase(phase))) {
            return normalized;
        }
        if (scmCommitIds == null || scmCommitIds.isEmpty()) {
            return normalized;
        }
        for (String commitId : scmCommitIds) {
            if (StringUtils.isNotBlank(commitId)) {
                normalized.add(commitId.trim());
            }
        }
        return normalized;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 1L;
        private final SeiReportPhaseStep step;

        protected Execution(SeiReportPhaseStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            validate(step.getPhase(), step.getEnvironment(), step.getStartTime());

            Run<?, ?> run = getContext().get(Run.class);
            if (run == null) {
                throw new IllegalStateException("seiReportPhase: Run context is required");
            }

            long endTime = System.currentTimeMillis();
            String normalizedPhase = normalizePhase(step.getPhase());
            String resolvedResult = step.getResult();
            if (StringUtils.isBlank(resolvedResult) && run.getResult() != null) {
                resolvedResult = run.getResult().toString();
            }

            PhaseEvent event = new PhaseEvent(
                    normalizedPhase,
                    step.getEnvironment(),
                    step.getStageName(),
                    step.getStartTime(),
                    endTime,
                    resolvedResult,
                    normalizeCommits(normalizedPhase, step.getScmCommitIds()),
                    convertArtifacts(step.getArtifacts())
            );

            // Serialize get-or-create on the Run so parallel stages share one action instance.
            PropeloStageMarkerAction action;
            synchronized (run) {
                action = run.getAction(PropeloStageMarkerAction.class);
                if (action == null) {
                    action = new PropeloStageMarkerAction();
                    run.addAction(action);
                }
            }
            action.append(event);

            TaskListener listener = getContext().get(TaskListener.class);
            String eventJson;
            try {
                eventJson = MAPPER.writeValueAsString(event);
            } catch (Exception e) {
                eventJson = String.valueOf(event);
                LOGGER.log(Level.WARNING, "Failed to serialize phase event payload for logging", e);
            }
            if (listener != null) {
                listener.getLogger().println(String.format(
                        "seiReportPhase: recorded %s phase marker (start=%d, end=%d)",
                        normalizedPhase, event.getStartTime(), event.getEndTime()));
                listener.getLogger().println("seiReportPhase: payload=" + eventJson);
            }
            LOGGER.log(Level.INFO, "Recorded phase marker for run {0}: {1}", new Object[]{run, eventJson});
            return null;
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> context = new HashSet<>();
            context.add(Run.class);
            context.add(TaskListener.class);
            return context;
        }

        @Override
        public String getFunctionName() {
            return "seiReportPhase";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "SEI Report Phase (CI/CD correlation marker)";
        }
    }
}
