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
     * Handles nested jobs under a branch (e.g. {@code .../branches/encoded/jobs/sub-job}).
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
        String resolvedBranchSegment = encodeBranchPathSegment(resolvedBranchName);
        String branchesSuffix = BRANCHES_PATH_SEGMENT + encodedBranchName;
        if (jobFullName.endsWith(branchesSuffix)) {
            return jobFullName.substring(0, jobFullName.length() - branchesSuffix.length())
                    + BRANCHES_PATH_SEGMENT
                    + resolvedBranchSegment;
        }
        String branchesSegment = BRANCHES_PATH_SEGMENT + encodedBranchName + "/";
        int branchIndex = jobFullName.indexOf(branchesSegment);
        if (branchIndex >= 0) {
            return jobFullName.substring(0, branchIndex)
                    + BRANCHES_PATH_SEGMENT
                    + resolvedBranchSegment
                    + jobFullName.substring(branchIndex + branchesSegment.length() - 1);
        }
        return jobFullName;
    }

    private static final String ENCODED_HASH = "%23";
    private static final String ENCODED_PERCENT = "%25";
    private static final String ENCODED_SLASH = "%2F";
    private static final String ENCODED_QUESTION = "%3F";
    private static final String ENCODED_OPEN_BRACKET = "%5B";
    private static final String ENCODED_CLOSE_BRACKET = "%5D";
    private static final String ENCODED_BACKSLASH = "%5C";

    /**
     * URL path-segment encoding aligned with Jenkins {@code NameEncoder} for multibranch job names.
     * Empty / "." / ".." branch names are not valid for this use case and are returned unchanged.
     */
    static String encodeBranchPathSegment(String branchName) {
        if (StringUtils.isBlank(branchName)) {
            return branchName;
        }
        StringBuilder encoded = new StringBuilder(branchName.length() + 16);
        for (char character : branchName.toCharArray()) {
            switch (character) {
                case '#':
                    encoded.append(ENCODED_HASH);
                    break;
                case '%':
                    encoded.append(ENCODED_PERCENT);
                    break;
                case '/':
                    encoded.append(ENCODED_SLASH);
                    break;
                case '?':
                    encoded.append(ENCODED_QUESTION);
                    break;
                case '[':
                    encoded.append(ENCODED_OPEN_BRACKET);
                    break;
                case ']':
                    encoded.append(ENCODED_CLOSE_BRACKET);
                    break;
                case '\\':
                    encoded.append(ENCODED_BACKSLASH);
                    break;
                default:
                    encoded.append(character);
                    break;
            }
        }
        return encoded.toString();
    }
}
