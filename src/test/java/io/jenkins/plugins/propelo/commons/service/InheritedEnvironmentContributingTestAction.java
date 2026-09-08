package io.jenkins.plugins.propelo.commons.service;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.model.InvisibleAction;

class InheritedEnvironmentContributingTestAction extends InvisibleAction implements EnvironmentContributingAction {

    private final String environmentKey;
    private final String environmentValue;

    InheritedEnvironmentContributingTestAction(String environmentKey, String environmentValue) {
        this.environmentKey = environmentKey;
        this.environmentValue = environmentValue;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars environment) {
        environment.put(environmentKey, environmentValue);
    }
}
