package io.jenkins.plugins.propelo.commons.plugins;

import java.nio.charset.Charset;

public class Common {
    public static final String API_URL_PROD = "https://api.propelo.ai";
    public static final String API_URL_DEV = "https://testapi1.propelo.ai";
    public static final String API_URL_LOCAL = "http://localhost:8080";
    public static final String API_URL_EFFECTIVE = API_URL_PROD;

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static final String LEVELOPS_JENKINS_HTML_REPORT_FILE_NAME = "LevelOps_Jenkins_Security_Report.hml";
    public static final String LEVELOPS_JENKINS_HTML_REPORT_FILE_NAME_TEMP = "LevelOps_Jenkins_Security_Report.hml.tmp";
    public static final String REPORTS_DIR_NAME = "reports";
    //public static final String DATA_DIR_NAME = "data";
    public static final String JOBS_DATA_DIR_NAME = "jobs";
    public static final String CONF_HISTORY_FILE = "config-history.txt";
    public static final String CONF_SCM_FILE = "config-scm.txt";
    public static final String RUN_TRIGGERS_FILE = "run-triggers.txt";
    public static final String RUN_HISTORY_FILE = "run-history.txt";
    public static final String RUN_HISTORY_COMPLETE_DATA_DIRECTORY = "complete-data-%d";
    public static final String RUN_HISTORY_COMPLETE_DATA_ZIP_FILE = "complete-data-temp-%d.zip";
    public static final String RUN_PARAMS_HISTORY_FILE = "run-params.txt";
    public static final String RUN_GIT_CHANGES_HISTORY_FILE = "run-git.txt";
    public static final String JOB_FULL_NAME_FILE = "job-full-name.txt";
    public static final String JENKINS_INSTANCE_GUID_FILE = "jenkins-instance-guid.txt";
    public static final String JENKINS_HEARTBEAT_INFO_FILE = "jenkins-status-info.json";
    public static final String JENKINS_INSTANCE_NAME_FILE = "jenkins-instance-name.txt";
    public static final String JENKINS_INSTANCE_URL_FILE = "jenkins-instance-url.txt";
    public static final String PLUGIN_VERSION_FILE = "plugin-version.txt";
    public static final String LEVELOPS_DATA_ZIP_FILE = "levelops-data.zip";
    public static final String LEVELOPS_DATA_ZIP_FILE_FORMAT = "levelops-data-%s.zip";

    public static final String CONFIG_HISTORY_CREATED= "Created";
    public static final String CONFIG_HISTORY_RENAMED= "Renamed";
    public static final String CONFIG_HISTORY_CHANGED= "Changed";
    public static final String CONFIG_HISTORY_DELETED= "Deleted";
}
