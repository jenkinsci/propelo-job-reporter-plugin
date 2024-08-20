package io.jenkins.plugins.propelo.commons.models;

import hudson.Extension;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;

@Symbol("propelo-job-reporter")
@Extension
public class PropeloJobReporterConfiguration extends GlobalConfiguration {
    private Secret levelOpsApiKey;
    private String levelOpsPluginPath;
    private boolean trustAllCertificates;
    private String productIds;
    private String jenkinsInstanceName;
    public Boolean isRegistered;
    private String jenkinsStatus;
    private String jenkinsUserName;
    private String jenkinsBaseUrl;
    private Secret jenkinsUserToken;
    private long heartbeatDuration;
    private String bullseyeXmlResultPaths;
    private long configUpdatedAt;
    private ApplicationType applicationType;

    public static PropeloJobReporterConfiguration CONFIGURATION = new PropeloJobReporterConfiguration();

    @DataBoundConstructor
    public PropeloJobReporterConfiguration(Secret levelOpsApiKey, String levelOpsPluginPath, @CheckForNull boolean trustAllCertificates, String productIds, String jenkinsInstanceName, Boolean isRegistered, String jenkinsStatus, String jenkinsUserName, String jenkinsBaseUrl, Secret jenkinsUserToken, long heartbeatDuration, String bullseyeXmlResultPaths, long configUpdatedAt, ApplicationType applicationType) {
        this.levelOpsApiKey = levelOpsApiKey;
        this.levelOpsPluginPath = levelOpsPluginPath;
        this.trustAllCertificates = trustAllCertificates;
        this.productIds = productIds;
        this.jenkinsInstanceName = jenkinsInstanceName;
        this.isRegistered = isRegistered;
        this.jenkinsStatus = jenkinsStatus;
        this.jenkinsUserName = jenkinsUserName;
        this.jenkinsBaseUrl = jenkinsBaseUrl;
        this.jenkinsUserToken = jenkinsUserToken;
        this.heartbeatDuration = heartbeatDuration;
        this.bullseyeXmlResultPaths = bullseyeXmlResultPaths;
        this.configUpdatedAt = configUpdatedAt;
        this.applicationType = applicationType;
        CONFIGURATION = this;
    }

    public PropeloJobReporterConfiguration(){
        this.levelOpsApiKey = Secret.fromString("");
        this.levelOpsPluginPath = "${JENKINS_HOME}/levelops-jenkin";
        this.productIds = "";
        this.jenkinsInstanceName = "Jenkins Instance";
        this.isRegistered = false;
        this.jenkinsStatus = "";
        this.jenkinsUserName = "";
        this.jenkinsBaseUrl = "";
        this.jenkinsUserToken = Secret.fromString("");
        this.heartbeatDuration = 60;
        this.trustAllCertificates = false;
        this.bullseyeXmlResultPaths = "";
        this.configUpdatedAt = System.currentTimeMillis();
        this.load();
        CONFIGURATION = this;
    }

    public Secret getLevelOpsApiKey() {
        return levelOpsApiKey;
    }

    @DataBoundSetter
    public void setLevelOpsApiKey(Secret levelOpsApiKey) {
        this.levelOpsApiKey = levelOpsApiKey;
    }

    public String getLevelOpsPluginPath() {
        return levelOpsPluginPath;
    }

    @DataBoundSetter
    public void setLevelOpsPluginPath(String levelOpsPluginPath) {
        this.levelOpsPluginPath = levelOpsPluginPath;
    }

    public boolean isTrustAllCertificates() {
        return trustAllCertificates;
    }

    @DataBoundSetter
    public void setTrustAllCertificates(boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
    }

    public String getProductIds() {
        return productIds;
    }

    @DataBoundSetter
    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public String getJenkinsInstanceName() {
        return jenkinsInstanceName;
    }

    @DataBoundSetter
    public void setJenkinsInstanceName(String jenkinsInstanceName) {
        this.jenkinsInstanceName = jenkinsInstanceName;
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    @DataBoundSetter
    public void setRegistered(Boolean registered) {
        isRegistered = registered;
    }

    public String getJenkinsStatus() {
        return jenkinsStatus;
    }

    @DataBoundSetter
    public void setJenkinsStatus(String jenkinsStatus) {
        this.jenkinsStatus = jenkinsStatus;
    }

    public String getJenkinsUserName() {
        return jenkinsUserName;
    }

    @DataBoundSetter
    public void setJenkinsUserName(String jenkinsUserName) {
        this.jenkinsUserName = jenkinsUserName;
    }

    public String getJenkinsBaseUrl() {
        return jenkinsBaseUrl;
    }

    @DataBoundSetter
    public void setJenkinsBaseUrl(String jenkinsBaseUrl) {
        this.jenkinsBaseUrl = jenkinsBaseUrl;
    }

    public Secret getJenkinsUserToken() {
        return jenkinsUserToken;
    }

    @DataBoundSetter
    public void setJenkinsUserToken(Secret jenkinsUserToken) {
        this.jenkinsUserToken = jenkinsUserToken;
    }

    public long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    @DataBoundSetter
    public void setHeartbeatDuration(long heartbeatDuration) {
        this.heartbeatDuration = heartbeatDuration;
    }

    public String getBullseyeXmlResultPaths() {
        return bullseyeXmlResultPaths;
    }

    @DataBoundSetter
    public void setBullseyeXmlResultPaths(String bullseyeXmlResultPaths) {
        this.bullseyeXmlResultPaths = bullseyeXmlResultPaths;
    }

    public long getConfigUpdatedAt() {
        return configUpdatedAt;
    }

    @DataBoundSetter
    public void setConfigUpdatedAt(long configUpdatedAt) {
        this.configUpdatedAt = configUpdatedAt;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    @DataBoundSetter
    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public String getDisplayName() {
        return "Propelo Job Reporter configuration";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        // reset optional certificate to default before data-binding
        req.bindJSON(this, json);
        save();
        return true;
    }
}
