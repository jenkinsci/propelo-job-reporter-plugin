package io.jenkins.plugins.propelo.commons.service;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static io.jenkins.plugins.propelo.commons.plugins.Common.SEI_SCM_COMMIT_IDS;

/**
 * Opt-in fallback for scm_commit_ids when Jenkins changelog*.xml yields none.
 * Reads SEI_SCM_COMMIT_IDS from the run environment first, then from a build parameter.
 */
public class JobRunScmCommitsFallbackService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final Pattern FULL_GIT_SHA_PATTERN = Pattern.compile("[A-Fa-f0-9]{40}");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("[,;\\s]+");
    private static final int MAX_FALLBACK_COMMIT_IDS = 100;

    public List<String> resolveFallbackCommitIds(Run<?, ?> run) {
        if (run == null) {
            return Collections.emptyList();
        }
        String rawValue = readFromEnvironment(run);
        String source = "environment";
        if (StringUtils.isBlank(rawValue)) {
            rawValue = readFromParameter(run);
            source = "parameter";
        }
        if (StringUtils.isBlank(rawValue)) {
            LOGGER.log(Level.FINE, "No {0} environment variable or build parameter found", SEI_SCM_COMMIT_IDS);
            return Collections.emptyList();
        }
        List<String> commitIds = parseCommitIdsFromRawValue(rawValue);
        LOGGER.log(Level.FINE, "Resolved {0} fallback scm commit ids from {1} {2}",
                new Object[]{commitIds.size(), source, SEI_SCM_COMMIT_IDS});
        return commitIds;
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
            if (uniqueCommitIds.size() >= MAX_FALLBACK_COMMIT_IDS) {
                LOGGER.log(Level.WARNING, "Ignoring additional {0} values after reaching cap of {1}",
                        new Object[]{SEI_SCM_COMMIT_IDS, MAX_FALLBACK_COMMIT_IDS});
                break;
            }
            uniqueCommitIds.add(commitId);
        }
        if (rejectedCount > 0) {
            LOGGER.log(Level.WARNING, "Rejected {0} invalid {1} value(s); full 40-character hex SHAs are required",
                    new Object[]{rejectedCount, SEI_SCM_COMMIT_IDS});
        }
        return new ArrayList<>(uniqueCommitIds);
    }

    private String readFromEnvironment(Run<?, ?> run) {
        try {
            EnvVars environment = run.getEnvironment(TaskListener.NULL);
            if (environment == null) {
                return null;
            }
            return environment.get(SEI_SCM_COMMIT_IDS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while reading {0} from environment", SEI_SCM_COMMIT_IDS);
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read " + SEI_SCM_COMMIT_IDS + " from environment", e);
            return null;
        }
    }

    private String readFromParameter(Run<?, ?> run) {
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
