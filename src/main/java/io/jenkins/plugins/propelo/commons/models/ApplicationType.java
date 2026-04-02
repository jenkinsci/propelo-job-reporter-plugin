package io.jenkins.plugins.propelo.commons.models;

import javax.annotation.Nullable;

public enum ApplicationType {

    SEI_HARNESS_PROD1("SEI-HARNESS-PROD1", "https://app.harness.io/prod1/sei/api"),
    SEI_HARNESS_PROD2("SEI-HARNESS-PROD2", "https://app.harness.io/gratis/sei/api"),
    SEI_HARNESS_PROD3("SEI-HARNESS-PROD3", "https://app3.harness.io/sei/api"),
    SEI_HARNESS_PROD4("SEI-HARNESS-PROD4", "https://prod4.harness.io/sei/api"),
    SEI_HARNESS_EU1("SEI-HARNESS-EU1", "https://accounts.eu.harness.io/sei/api");

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
        for (ApplicationType type : values()) {
            if (type.getApplicationType().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ApplicationType: " + value);
    }
}
