package io.levelops.plugins.levelops_job_reporter.plugins;

import hudson.XmlFile;
import io.jenkins.plugins.propelo.commons.utils.DateUtils;
import io.jenkins.plugins.propelo.commons.utils.EnvironmentVariableNotDefinedException;
import io.jenkins.plugins.propelo.commons.utils.Utils;
import jenkins.model.Jenkins;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.jenkins.plugins.propelo.commons.plugins.Common.REPORTS_DIR_NAME;

public class LevelOpsPluginImpl {
// { public void save() {}; public void load() {};} 
// extends Plugin {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String DATA_DIR_NAME = "run-complete-data";
    public static final String PLUGIN_SHORT_NAME = "propelo-job-reporter";
    private String levelOpsApiKey = "";
    private String levelOpsPluginPath = "${JENKINS_HOME}/levelops-jenkin";
    private boolean trustAllCertificates = false;
    private String productIds = "";
    private String jenkinsInstanceName = "Jenkins Instance";
    public Boolean isRegistered = false;
    private String jenkinsStatus = "";
    private String jenkinsUserName = "";
    private String jenkinsBaseUrl = "";
    private String jenkinsUserToken = "";
    private long heartbeatDuration = 60;
    private String bullseyeXmlResultPaths = "";
    private long configUpdatedAt = System.currentTimeMillis();
    private static LevelOpsPluginImpl instance = null;
    private static final Pattern OLDER_DIRECTORIES_PATTERN = Pattern.compile("^(run-complete-data-)");
    private Boolean migrated = false;

    //ToDo: This is deprecated! Fix soon.
    private LevelOpsPluginImpl() {
    }

    private XmlFile getConfig() {
        File configFile = new File(Jenkins.get().getRootDir(),"levelops-job-reporter.xml");
        LOGGER.info("LevelOpsPluginImpl config path: " + configFile.getAbsolutePath());
        return new XmlFile(Jenkins.XSTREAM, configFile);
    }
    
    public void load() throws IOException{
        XmlFile xml = getConfig();
        if (xml.exists()){
            xml.unmarshal(this);
        }
    }

    public void save() throws IOException {
        XmlFile xml = getConfig();
        xml.write(this);
    }

    public static LevelOpsPluginImpl getInstance() {
        if (LevelOpsPluginImpl.instance == null) {
            LevelOpsPluginImpl.instance = new LevelOpsPluginImpl();
        }
        return LevelOpsPluginImpl.instance;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public boolean isMigrated(){
        return this.migrated != null ? this.migrated : false;
    }

    public File getHudsonHome() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        return (jenkins == null) ? null : jenkins.getRootDir();
    }

    public void setJenkinsBaseUrl(final String jenkinsBaseUrl){
        this.jenkinsBaseUrl = jenkinsBaseUrl;
    }

    public String getJenkinsBaseUrl(){
        return this.jenkinsBaseUrl;
    }

    public String getLevelOpsApiKey() {
        return levelOpsApiKey;
    }

    public void setLevelOpsApiKey(String levelOpsApiKey) {
        this.levelOpsApiKey = levelOpsApiKey;
    }

    /**
     * Get the Propelo plugin path as entered by the user. May contain environment variables.
     *
     * If you need a path that can be used as is (env. vars expanded), please use @link{getExpandedLevelOpsPluginPath}.
     *
     * @return the path as entered by the user.
     */
    public String getLevelOpsPluginPath() {
        return levelOpsPluginPath;
    }

    public void setLevelOpsPluginPath(String levelOpsPluginPath) {
        this.levelOpsPluginPath = levelOpsPluginPath;
    }

    /**
     * @return the levelOpsPluginPath path with possibly contained environment variables expanded.
     */
    public String getExpandedLevelOpsPluginPath() {
        if (StringUtils.isBlank(levelOpsPluginPath)) {
            return levelOpsPluginPath;
        }
        String expandedPath = "";
        try {
            expandedPath = Utils.expandEnvironmentVariables(levelOpsPluginPath);
        } catch (final EnvironmentVariableNotDefinedException evnde) {
            LOGGER.log(Level.SEVERE, evnde.getMessage() + " Using unexpanded path.");
            expandedPath = levelOpsPluginPath;
        }

        return expandedPath;
    }

    public String getJenkinsStatus() {
        return jenkinsStatus;
    }

    public void setJenkinsStatus(String jenkinsStatus) {
        this.jenkinsStatus = jenkinsStatus;
    }

    public long getConfigUpdatedAt() {
        return configUpdatedAt;
    }

    public void setConfigUpdatedAt(long configUpdatedAt) {
        this.configUpdatedAt = configUpdatedAt;
    }

    public long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    public void setHeartbeatDuration(long heartbeatDuration) {
        this.heartbeatDuration = heartbeatDuration;
    }

    public File getExpandedLevelOpsPluginDir() {
        return new File(this.getExpandedLevelOpsPluginPath());
    }


    public Boolean isRegistered() {
        return isRegistered;
    }

    public boolean isExpandedLevelOpsPluginPathNullOrEmpty(){
        return StringUtils.isEmpty(getExpandedLevelOpsPluginPath());
    }

    private File buildReportsDirectory(String levelOpsPluginPath){
        return new File(levelOpsPluginPath,REPORTS_DIR_NAME);
    }
    public File getReportsDirectory() {
        return buildReportsDirectory(this.getExpandedLevelOpsPluginPath());
    }

    private void deleteOlderDirectories() {
        File currentDataDirectoryWithVersion = getDataDirectoryWithVersion();
        File expandedLevelOpsPluginDir = getExpandedLevelOpsPluginDir();
        if (expandedLevelOpsPluginDir != null && expandedLevelOpsPluginDir.exists()
                && currentDataDirectoryWithVersion != null) {
            for (File file : Objects.requireNonNull(expandedLevelOpsPluginDir.listFiles())) {
                Matcher matcher = OLDER_DIRECTORIES_PATTERN.matcher(file.getName());
                if (matcher.find() && !file.getName().equalsIgnoreCase(currentDataDirectoryWithVersion.getName())) {
                    FileUtils.deleteQuietly(file);
                }
            }
        }
    }

    public String getPluginVersionString() {
        LOGGER.log(Level.FINEST, "getPluginVersionString starting");
        String pluginVersionString = Jenkins.get().getPluginManager().getPlugin(LevelOpsPluginImpl.PLUGIN_SHORT_NAME).getVersion();
        LOGGER.log(Level.FINEST, "getPluginVersionString completed pluginVersionString = {0}", pluginVersionString);
        return pluginVersionString;
    }

    private File buildDataDirectory(String levelOpsPluginPath) {
        LOGGER.log(Level.FINEST, "buildDataDirectory starting");
        File dataDirectory = new File(levelOpsPluginPath, DATA_DIR_NAME);
        LOGGER.log(Level.FINEST, "buildDataDirectory completed = {0}", dataDirectory);
        return dataDirectory;
    }
    public File getDataDirectory() {
        return buildDataDirectory(this.getExpandedLevelOpsPluginPath());
    }

    private File buildDataDirectoryWithVersion(String levelOpsPluginPath) {
        LOGGER.log(Level.FINEST, "buildDataDirectoryWithVersion starting");
        String dataDirWithVersionName = DATA_DIR_NAME + "-" + getPluginVersionString();
        LOGGER.log(Level.FINEST, "dataDirWithVersionName = {0}", dataDirWithVersionName);
        File dataDirectoryWithVersion = new File(levelOpsPluginPath, dataDirWithVersionName);
        LOGGER.log(Level.FINEST, "buildDataDirectoryWithVersion completed = {0}", dataDirectoryWithVersion);
        return dataDirectoryWithVersion;
    }
    public File getDataDirectoryWithVersion() {
        return buildDataDirectoryWithVersion(this.getExpandedLevelOpsPluginPath());
    }

    public File getDataDirectoryWithRotation() {
        File dataDirWithVersion = buildDataDirectoryWithVersion(this.getExpandedLevelOpsPluginPath());
        LOGGER.log(Level.FINEST, "dataDirWithVersion = {0}", dataDirWithVersion);
        File dataDirWithRotation = new File(dataDirWithVersion, DateUtils.getDateFormattedDirName());
        LOGGER.log(Level.FINEST, "dataDirWithRotation = {0}", dataDirWithRotation);
        return dataDirWithRotation;
    }

    public String getJenkinsUserName() {
        return jenkinsUserName;
    }

    public void setJenkinsUserName(String jenkinsUserName) {
        this.jenkinsUserName = jenkinsUserName;
    }

    public String getJenkinsUserToken() {
        return jenkinsUserToken;
    }

    public void setJenkinsUserToken(String jenkinsUserToken) {
        this.jenkinsUserToken = jenkinsUserToken;
    }

    public String getBullseyeXmlResultPaths() {
        return bullseyeXmlResultPaths;
    }

    public void setBullseyeXmlResultPath(String bullseyeXmlResultPaths) {
        this.bullseyeXmlResultPaths = bullseyeXmlResultPaths;
    }

    public boolean isTrustAllCertificates() {
        return trustAllCertificates;
    }

    public void setTrustAllCertificates(boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    private List<String> parseProductIdsList(String productIds){
        if((productIds == null) || (productIds.length() == 0)){
            return Collections.emptyList();
        }
        String[] productIdsSplit = productIds.split(",");
        if((productIdsSplit == null) || (productIdsSplit.length ==0)){
            return Collections.emptyList();
        }
        return Arrays.asList(productIdsSplit);
    }
    public List<String> getProductIdsList(){
        return parseProductIdsList(this.productIds);
    }

    public String getJenkinsInstanceName() {
        return jenkinsInstanceName;
    }

    public void setJenkinsInstanceName(String jenkinsInstanceName) {
        this.jenkinsInstanceName = jenkinsInstanceName;
    }

}