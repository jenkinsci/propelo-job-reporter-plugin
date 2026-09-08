package io.jenkins.plugins.propelo.commons.service;

import hudson.model.InvisibleAction;
import org.jenkinsci.plugins.workflow.support.actions.EnvironmentAction;

import java.util.Map;

class PipelineEnvironmentOverrideTestAction extends InvisibleAction implements EnvironmentAction.IncludingOverrides {

    private final Map<String, String> overriddenEnvironment;

    PipelineEnvironmentOverrideTestAction(Map<String, String> overriddenEnvironment) {
        this.overriddenEnvironment = overriddenEnvironment;
    }

    @Override
    public Map<String, String> getOverriddenEnvironment() {
        return overriddenEnvironment;
    }
}
