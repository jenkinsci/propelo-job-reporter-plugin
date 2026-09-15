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

    private static final String BRANCHES_PATH_SEGMENT = "/branches/";

    /**
     * Replaces the filesystem-mangled branch segment in {@code job_full_name} with the Jenkins URL-safe
     * encoded SCM branch (e.g. {@code feature%2Fmaac-mute} instead of {@code feature-maac-mute.l94old}).
     */
    public static String replaceEncodedBranchInJobFullName(String jobFullName,
                                                           String encodedBranchName,
                                                           String resolvedBranchName) {
        if (StringUtils.isBlank(jobFullName)
                || StringUtils.isBlank(encodedBranchName)
                || StringUtils.isBlank(resolvedBranchName)) {
            return jobFullName;
        }
        if (encodedBranchName.equals(resolvedBranchName)) {
            return jobFullName;
        }
        String branchesSuffix = BRANCHES_PATH_SEGMENT + encodedBranchName;
        if (!jobFullName.endsWith(branchesSuffix)) {
            return jobFullName;
        }
        String resolvedBranchSegment = encodeBranchPathSegment(resolvedBranchName);
        return jobFullName.substring(0, jobFullName.length() - branchesSuffix.length())
                + BRANCHES_PATH_SEGMENT
                + resolvedBranchSegment;
    }

    /**
     * URL path-segment encoding aligned with Jenkins {@code NameEncoder} for multibranch job names.
     */
    static String encodeBranchPathSegment(String branchName) {
        if (StringUtils.isBlank(branchName)) {
            return branchName;
        }
        if ("".equals(branchName)) {
            return "%00";
        }
        if (".".equals(branchName)) {
            return "%2E";
        }
        if ("..".equals(branchName)) {
            return "%2E.";
        }
        StringBuilder encoded = new StringBuilder(branchName.length() + 16);
        for (char character : branchName.toCharArray()) {
            switch (character) {
                case '#':
                    encoded.append("%23");
                    break;
                case '%':
                    encoded.append("%25");
                    break;
                case '/':
                    encoded.append("%2F");
                    break;
                case '?':
                    encoded.append("%3F");
                    break;
                case '[':
                    encoded.append("%5B");
                    break;
                case ']':
                    encoded.append("%5D");
                    break;
                case '\\':
                    encoded.append("%5C");
                    break;
                default:
                    encoded.append(character);
                    break;
            }
        }
        return encoded.toString();
    }
}
