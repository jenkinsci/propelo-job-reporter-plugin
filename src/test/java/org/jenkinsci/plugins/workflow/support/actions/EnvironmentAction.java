package org.jenkinsci.plugins.workflow.support.actions;

import hudson.model.Action;

import java.util.Map;

/**
 * Test fixture mirroring the workflow-support EnvironmentAction binary name for reflective resolution tests.
 */
public interface EnvironmentAction extends Action {

    interface IncludingOverrides extends EnvironmentAction {
        Map<String, String> getOverriddenEnvironment();
    }
}
