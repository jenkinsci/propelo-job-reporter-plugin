package io.jenkins.plugins.propelo.commons.service;

import hudson.model.Action;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import org.apache.commons.lang.StringUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static io.jenkins.plugins.propelo.commons.plugins.Common.SEI_SCM_COMMIT_IDS;

/**
 * Resolves explicit scm commit ids from SEI_SCM_COMMIT_IDS on the current Jenkins run
 * and merges them with discovered native changelog/Perforce commit ids.
 */
public class JobRunScmCommitIdsService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final Pattern FULL_GIT_SHA_PATTERN = Pattern.compile("[A-Fa-f0-9]{40}");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("[,;\\s]+");
    private static final int MAX_EXPLICIT_COMMIT_IDS = 100;
    private static final String PIPELINE_ENVIRONMENT_OVERRIDE_INTERFACE =
            "org.jenkinsci.plugins.workflow.support.actions.EnvironmentAction$IncludingOverrides";
    private static final String GET_OVERRIDDEN_ENVIRONMENT_METHOD = "getOverriddenEnvironment";
    private static final String SOURCE_PIPELINE_ENVIRONMENT_OVERRIDE = "pipeline environment override";
    private static final String SOURCE_BUILD_PARAMETER = "build parameter";
    private static final String PIPELINE_OVERRIDE_READ_FAILURE_MESSAGE =
            "Failed to read explicit scm commit ids from pipeline environment override for current job run; falling back to build parameter";

    public List<String> resolveExplicitCommitIds(Run<?, ?> run) {
        if (run == null) {
            return Collections.emptyList();
        }
        String rawValue = readFromPipelineEnvironmentOverride(run);
        String source = SOURCE_PIPELINE_ENVIRONMENT_OVERRIDE;
        if (StringUtils.isBlank(rawValue)) {
            rawValue = readFromBuildParameter(run);
            source = SOURCE_BUILD_PARAMETER;
        }
        if (StringUtils.isBlank(rawValue)) {
            LOGGER.log(Level.FINE,
                    "No explicit {0} current-run Pipeline environment override or build parameter found for current job run",
                    SEI_SCM_COMMIT_IDS);
            return Collections.emptyList();
        }
        List<String> commitIds = parseCommitIdsFromRawValue(rawValue);
        LOGGER.log(Level.FINE, "Resolved {0} explicit scm commit ids from {1} {2} for current job run",
                new Object[]{commitIds.size(), source, SEI_SCM_COMMIT_IDS});
        return commitIds;
    }

    public List<String> mergeCommitIds(List<String> discoveredCommitIds, List<String> explicitCommitIds) {
        Set<String> mergedUniqueCommitIds = new LinkedHashSet<>();
        if (discoveredCommitIds != null) {
            for (String commitId : discoveredCommitIds) {
                if (StringUtils.isNotBlank(commitId)) {
                    mergedUniqueCommitIds.add(commitId);
                }
            }
        }
        if (explicitCommitIds != null) {
            for (String commitId : explicitCommitIds) {
                if (StringUtils.isNotBlank(commitId)) {
                    mergedUniqueCommitIds.add(commitId);
                }
            }
        }
        return new ArrayList<>(mergedUniqueCommitIds);
    }

    List<String> parseCommitIdsFromRawValue(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return Collections.emptyList();
        }
        String[] tokens = SPLIT_PATTERN.split(rawValue.trim());
        Set<String> uniqueCommitIds = new LinkedHashSet<>();
        int rejectedCount = 0;
        for (String token : tokens) {
            if (StringUtils.isBlank(token)) {
                continue;
            }
            String commitId = token.trim();
            if (!FULL_GIT_SHA_PATTERN.matcher(commitId).matches()) {
                rejectedCount++;
                continue;
            }
            if (uniqueCommitIds.size() >= MAX_EXPLICIT_COMMIT_IDS) {
                LOGGER.log(Level.WARNING, "Ignoring additional explicit {0} values after reaching cap of {1}",
                        new Object[]{SEI_SCM_COMMIT_IDS, MAX_EXPLICIT_COMMIT_IDS});
                break;
            }
            uniqueCommitIds.add(commitId);
        }
        if (rejectedCount > 0) {
            LOGGER.log(Level.WARNING, "Rejected {0} invalid explicit {1} value(s); full 40-character hex SHAs are required",
                    new Object[]{rejectedCount, SEI_SCM_COMMIT_IDS});
        }
        return new ArrayList<>(uniqueCommitIds);
    }

    private String readFromPipelineEnvironmentOverride(Run<?, ?> run) {
        for (Action action : run.getAllActions()) {
            if (action == null || !implementsExactInterface(action.getClass(), PIPELINE_ENVIRONMENT_OVERRIDE_INTERFACE)) {
                continue;
            }
            String value = readOverriddenEnvironmentValue(action);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String readOverriddenEnvironmentValue(Action action) {
        try {
            Method getOverriddenEnvironment = action.getClass().getMethod(GET_OVERRIDDEN_ENVIRONMENT_METHOD);
            Object overriddenEnvironment = getOverriddenEnvironment.invoke(action);
            if (!(overriddenEnvironment instanceof Map)) {
                return null;
            }
            Object value = ((Map<?, ?>) overriddenEnvironment).get(SEI_SCM_COMMIT_IDS);
            return value == null ? null : value.toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.WARNING, PIPELINE_OVERRIDE_READ_FAILURE_MESSAGE, e);
            return null;
        }
    }

    private boolean implementsExactInterface(Class<?> type, String interfaceName) {
        if (type == null) {
            return false;
        }
        if (type.isInterface() && interfaceName.equals(type.getName())) {
            return true;
        }
        for (Class<?> implementedInterface : type.getInterfaces()) {
            if (implementsExactInterface(implementedInterface, interfaceName)) {
                return true;
            }
        }
        return implementsExactInterface(type.getSuperclass(), interfaceName);
    }

    private String readFromBuildParameter(Run<?, ?> run) {
        ParametersAction parameters = run.getAction(ParametersAction.class);
        if (parameters == null) {
            return null;
        }
        ParameterValue parameter = parameters.getParameter(SEI_SCM_COMMIT_IDS);
        if (parameter == null || parameter.getValue() == null) {
            return null;
        }
        return parameter.getValue().toString();
    }
}
