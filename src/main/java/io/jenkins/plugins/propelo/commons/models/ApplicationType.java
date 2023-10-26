package io.jenkins.plugins.propelo.commons.models;

import javax.annotation.Nullable;

public enum ApplicationType {

    SEI_LEGACY("SEI-LEGACY-US", "https://api.propelo.ai"),
    SEI_LEGACY_EU("SEI-LEGACY-EU", "https://eu1.api.propelo.ai"),
    SEI_LEGACY_ASIA("SEI-LEGACY-ASIA", "https://asia1.api.propelo.ai"),
    SEI_HARNESS("SEI-HARNESS", "https://app.harness.io/sei/api"),
    SEI_HARNESS_COMPLIANCE("SEI-HARNESS-COMPLIANCE", "https://app3.harness.io/sei/api");

    private String applicationType;
    private String targetUrl;

    ApplicationType(String applicationType, String targetUrl) {
        this.applicationType = applicationType;
        this.targetUrl = targetUrl;
    }

    public String getApplicationType(){
        return applicationType;
    }

    public String getTargetUrl(){
        return targetUrl;
    }

    public static ApplicationType fromString(@Nullable String value) {
        if(SEI_HARNESS.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS;
        }else if(SEI_HARNESS_COMPLIANCE.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS_COMPLIANCE;
        }else if(SEI_LEGACY_EU.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_LEGACY_EU;
        }else if(SEI_LEGACY_ASIA.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_LEGACY_ASIA;
        }else {
            return SEI_LEGACY;
        }
    }
}
