package io.jenkins.plugins.propelo.commons.service;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobFullNameConverter {
    public static String convertJobFullNameToJobNormalizedFullName(String jobFullName){
        if(StringUtils.isBlank(jobFullName)){
            return jobFullName;
        }
        String [] parts = jobFullName.split("/");
        if((parts == null) || (parts.length < 2)){
            return jobFullName;
        }

        List<String> resultParts = new ArrayList<>();
        for(String part : parts) {
            if("jobs".equalsIgnoreCase(part)){
                continue;
            }
            if("branches".equalsIgnoreCase(part)){
                continue;
            }
            if("modules".equalsIgnoreCase(part)){
                continue;
            }
            resultParts.add(part);
        }
        String blueOceanJobPath = StringUtils.join(resultParts, "/");
        return blueOceanJobPath;
    }

    public static String replaceEncodedBranchInNormalizedName(String normalizedFullName,
                                                                String encodedBranchName,
                                                                String resolvedBranchName) {
        if (StringUtils.isBlank(normalizedFullName)
                || StringUtils.isBlank(encodedBranchName)
                || StringUtils.isBlank(resolvedBranchName)) {
            return normalizedFullName;
        }
        if (encodedBranchName.equals(resolvedBranchName)) {
            return normalizedFullName;
        }
        String encodedSuffix = "/" + encodedBranchName;
        if (normalizedFullName.endsWith(encodedSuffix)) {
            return normalizedFullName.substring(0, normalizedFullName.length() - encodedSuffix.length())
                    + "/" + resolvedBranchName;
        }
        if (normalizedFullName.equals(encodedBranchName)) {
            return resolvedBranchName;
        }
        return normalizedFullName;
    }
}
