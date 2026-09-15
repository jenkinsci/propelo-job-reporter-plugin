package io.jenkins.plugins.propelo.commons.service;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves the real SCM branch name for multibranch jobs.
 * Jenkins stores an encoded filesystem name under {@code branches/}; SEI needs the SCM head name.
 */
public final class ScmBranchNameResolver {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String ENV_CHANGE_BRANCH = "CHANGE_BRANCH";
    private static final String ENV_BRANCH_NAME = "BRANCH_NAME";

    private ScmBranchNameResolver() {
        throw new IllegalAccessError("Utility class");
    }

    public static String resolveBranchName(Run<?, ?> build) {
        if (build == null) {
            return null;
        }
        Job<?, ?> job = build.getParent();
        if (job == null) {
            return null;
        }

        // Prefer the SCM head: it is cheap and does not require pipeline execution state.
        String branchName = resolveFromScmHead(job);
        if (StringUtils.isNotBlank(branchName)) {
            return branchName;
        }

        return resolveFromEnvironment(build);
    }

    private static String resolveFromEnvironment(Run<?, ?> build) {
        try {
            EnvVars environment = build.getEnvironment(TaskListener.NULL);
            if (environment == null) {
                return null;
            }
            String changeBranch = environment.get(ENV_CHANGE_BRANCH);
            if (StringUtils.isNotBlank(changeBranch)) {
                return changeBranch;
            }
            return environment.get(ENV_BRANCH_NAME);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.FINE, "Interrupted while reading build environment for branch name", interruptedException);
            return null;
        } catch (IOException ioException) {
            LOGGER.log(Level.FINE, "Unable to read build environment for branch name", ioException);
            return null;
        }
    }

    private static String resolveFromScmHead(Job<?, ?> job) {
        String jobFullName = (job != null) ? job.getFullName() : "unknown";
        try {
            SCMHead head = SCMHead.HeadByItem.findHead(job);
            if (head == null) {
                return null;
            }
            if (head instanceof ChangeRequestSCMHead2) {
                String originName = ((ChangeRequestSCMHead2) head).getOriginName();
                if (StringUtils.isNotBlank(originName)) {
                    return originName;
                }
            }
            return head.getName();
        } catch (RuntimeException runtimeException) {
            LOGGER.log(Level.FINE, "Unable to resolve SCM head for job=" + jobFullName, runtimeException);
            return null;
        }
    }
}
