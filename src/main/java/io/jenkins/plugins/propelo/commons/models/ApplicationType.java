package io.jenkins.plugins.propelo.commons.models;

import javax.annotation.Nullable;

public enum ApplicationType {
    SEI_LEGACY("SEI-LEGACY"),
    SEI_HARNESS("SEI-HARNESS"),
    SEI_HARNESS_COMPLIANCE("SEI-HARNESS-COMPLIANCE");

    private String applicationType;
    ApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationType(){
        return applicationType;
    }

    public static ApplicationType fromString(@Nullable String value) {
        if(SEI_HARNESS.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS;
        }else if(SEI_HARNESS_COMPLIANCE.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_HARNESS_COMPLIANCE;
        }else {
            return SEI_LEGACY;
        }
    }
}
