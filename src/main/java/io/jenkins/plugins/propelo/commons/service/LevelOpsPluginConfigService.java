package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.jenkins.plugins.propelo.commons.models.ApplicationType;
import io.jenkins.plugins.propelo.commons.models.LevelOpsConfig;
import io.jenkins.plugins.propelo.commons.plugins.Common;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;
import io.jenkins.plugins.propelo.job_reporter.plugins.PropeloPluginImpl;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.propelo.commons.plugins.Common.UTF_8;

public class LevelOpsPluginConfigService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final File OVERRIDE_CONFIG_FILE = new File("/var/lib/jenkins/levelops_config.json");
    private static final ObjectMapper OBJECT_MAPPER = JsonUtils.buildObjectMapper();
    private static final LevelOpsConfig DEFAULT_LEVELOPS_CONFIG = new LevelOpsConfig(Common.API_URL_EFFECTIVE);
    private static final LevelOpsConfig EU_LEVELOPS_CONFIG = new LevelOpsConfig(Common.EU_API_URL_PROD);
    private static final LevelOpsConfig ASIA_LEVELOPS_CONFIG = new LevelOpsConfig(Common.ASIA_API_URL_PROD);
    private static final LevelOpsConfig DEFAULT_HARNESS_LEVELOPS_CONFIG = new LevelOpsConfig(Common.HARNESS_API_URL_PROD);
    private static final LevelOpsConfig DEFAULT_HARNESS_COMPLIANCE_LEVELOPS_CONFIG = new LevelOpsConfig(Common.HARNESS_COMPLIANCE_API_URL_PROD);
    private static final LevelOpsPluginConfigService INSTANCE = new LevelOpsPluginConfigService();

    private final LoadingCache<String, LevelOpsConfig> cache;

    public LevelOpsPluginConfigService() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, LevelOpsConfig>() {
                    @Override
                    public LevelOpsConfig load(String s) throws Exception {
                        try {
                            ApplicationType applicationType = PropeloPluginImpl.getInstance().getApplicationType();

                            if (!OVERRIDE_CONFIG_FILE.exists()){
                                switch (applicationType){
                                    case SEI_LEGACY:
                                        return DEFAULT_LEVELOPS_CONFIG;
                                    case SEI_LEGACY_EU:
                                        return EU_LEVELOPS_CONFIG;
                                    case SEI_LEGACY_ASIA:
                                        return ASIA_LEVELOPS_CONFIG;
                                    case SEI_HARNESS:
                                        return DEFAULT_HARNESS_LEVELOPS_CONFIG;
                                    case SEI_HARNESS_COMPLIANCE:
                                        return DEFAULT_HARNESS_COMPLIANCE_LEVELOPS_CONFIG;
                                    default:
                                        return DEFAULT_LEVELOPS_CONFIG;
                                }
                            }

                            String configDataString = new String(Files.readAllBytes(OVERRIDE_CONFIG_FILE.toPath()), UTF_8);
                            LevelOpsConfig levelOpsConfig = OBJECT_MAPPER.readValue(configDataString, LevelOpsConfig.class);
                            return levelOpsConfig;
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error reading override config file!", e);
                            return DEFAULT_LEVELOPS_CONFIG;
                        }
                    }
                });
    }

    public LevelOpsConfig getLevelopsConfig() {
        try {
            return cache.get("DEFAULT");
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error reading levelOpsConfig from cache!", e);
            return DEFAULT_LEVELOPS_CONFIG;
        }
    }

    public static LevelOpsPluginConfigService getInstance() {
        return INSTANCE;
    }

}
