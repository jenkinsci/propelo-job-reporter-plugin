package io.jenkins.plugins.propelo.commons.models;

import javax.annotation.Nullable;

public enum ApplicationType {

    SEI_LEGACY("SEI-LEGACY-US", "https://api.propelo.ai"),
    SEI_LEGACY_EU("SEI-LEGACY-EU", "https://eu1.api.propelo.ai"),
    SEI_HARNESS_PROD1("SEI-HARNESS-PROD1", "https://app.harness.io/prod1/sei/api"),
    SEI_HARNESS_PROD2("SEI-HARNESS-PROD2", "https://app.harness.io/gratis/sei/api"),
    SEI_HARNESS_PROD3("SEI-HARNESS-PROD3", "https://app3.harness.io/sei/api");

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
        if(SEI_HARNESS_PROD1.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS_PROD1;
        }else if(SEI_HARNESS_PROD3.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS_PROD3;
        }else if(SEI_LEGACY_EU.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_LEGACY_EU;
        }else if(SEI_HARNESS_PROD2.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS_PROD2;
        }else {
            return SEI_LEGACY;
        }
    }
}
