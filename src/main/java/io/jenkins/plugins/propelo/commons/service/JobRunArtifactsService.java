package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.Run;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.CiCdJobRunArtifact;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extracts SEI-shaped artifacts for JobRunComplete payloads.
 * <p>
 * Primary source for local verification: archived {@code artifact-manifest.json}
 * written by print-only CI/CD pipelines (under {@code run.getArtifactsDir()}).
 * Also accepts a top-level {@code artifacts[]} array or nested {@code artifact} object.
 */
public class JobRunArtifactsService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    public static final String ARTIFACT_MANIFEST_FILE = "artifact-manifest.json";
    public static final String SEI_ARTIFACTS_FILE = "sei-artifacts.json";

    private final ObjectMapper mapper;

    public JobRunArtifactsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<CiCdJobRunArtifact> parseArtifactsForRun(Run<?, ?> run) {
        List<CiCdJobRunArtifact> artifacts = new ArrayList<>();
        if (run == null) {
            return artifacts;
        }
        File artifactsDir = run.getArtifactsDir();
        if (artifactsDir != null && artifactsDir.isDirectory()) {
            collectFromFile(new File(artifactsDir, ARTIFACT_MANIFEST_FILE), artifacts);
            collectFromFile(new File(artifactsDir, SEI_ARTIFACTS_FILE), artifacts);
        }
        LOGGER.log(Level.FINE, "Parsed {0} SEI artifacts for run {1}", new Object[]{artifacts.size(), run});
        return artifacts;
    }

    public boolean isCi(List<CiCdJobRunArtifact> artifacts) {
        if (artifacts == null) {
            return false;
        }
        for (CiCdJobRunArtifact artifact : artifacts) {
            if (Boolean.TRUE.equals(artifact.getOutput())) {
                return true;
            }
        }
        return false;
    }

    public boolean isCd(List<CiCdJobRunArtifact> artifacts) {
        if (artifacts == null) {
            return false;
        }
        for (CiCdJobRunArtifact artifact : artifacts) {
            if (Boolean.TRUE.equals(artifact.getInput())) {
                return true;
            }
        }
        return false;
    }

    void collectFromFile(File file, List<CiCdJobRunArtifact> sink) {
        if (file == null || !file.isFile()) {
            return;
        }
        try {
            JsonNode root = mapper.readTree(file);
            if (root == null || root.isNull()) {
                return;
            }
            if (root.has("artifacts") && root.get("artifacts").isArray()) {
                for (JsonNode node : root.get("artifacts")) {
                    CiCdJobRunArtifact artifact = fromNode(node);
                    if (artifact != null) {
                        sink.add(artifact);
                    }
                }
                return;
            }
            if (root.has("artifact") && root.get("artifact").isObject()) {
                CiCdJobRunArtifact artifact = fromNode(root.get("artifact"));
                if (artifact != null) {
                    sink.add(artifact);
                }
                return;
            }
            // Flat object itself may be an artifact
            if (root.isObject() && (root.has("name") || root.has("location") || root.has("qualifier"))) {
                CiCdJobRunArtifact artifact = fromNode(root);
                if (artifact != null) {
                    sink.add(artifact);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse SEI artifacts file: " + file.getAbsolutePath(), e);
        }
    }

    private CiCdJobRunArtifact fromNode(JsonNode node) {
        if (node == null || !node.isObject()) {
            return null;
        }
        String name = text(node, "name");
        String location = text(node, "location");
        String qualifier = text(node, "qualifier");
        String type = text(node, "type");
        List<String> missingFields = new ArrayList<>();
        if (isBlank(type)) {
            missingFields.add("type");
        }
        if (isBlank(location)) {
            missingFields.add("location");
        }
        if (isBlank(name)) {
            missingFields.add("name");
        }
        if (isBlank(qualifier)) {
            missingFields.add("qualifier");
        }
        if (!missingFields.isEmpty()) {
            LOGGER.log(Level.WARNING, "Skipping SEI artifact with incomplete identity, missing required fields: {0}", missingFields);
            return null;
        }
        Boolean input = bool(node, "input");
        Boolean output = bool(node, "output");
        String hash = text(node, "hash");
        Map<String, String> metadata = null;
        if (node.has("metadata") && node.get("metadata").isObject()) {
            metadata = new LinkedHashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = node.get("metadata").fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                metadata.put(entry.getKey(), entry.getValue() == null || entry.getValue().isNull()
                        ? null
                        : entry.getValue().asText());
            }
        }
        return new CiCdJobRunArtifact(input, output, type, location, name, qualifier, hash, metadata);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return isBlank(text) ? null : text;
    }

    private static Boolean bool(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asBoolean();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
