package io.jenkins.plugins.propelo.commons.models;

import javax.annotation.Nullable;

public enum ApplicationType {
    SEI_LEGACY("SEI-LEGACY-US"),
    SEI_LEGACY_EU("SEI-LEGACY-EU"),
    SEI_LEGACY_ASIA("SEI-LEGACY-ASIA"),
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
        }else if(SEI_LEGACY_EU.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_LEGACY_EU;
        }else if(SEI_LEGACY_ASIA.getApplicationType().equalsIgnoreCase(value)) {
            return SEI_LEGACY_ASIA;
        }else {
            return SEI_LEGACY;
        }
    }
}
