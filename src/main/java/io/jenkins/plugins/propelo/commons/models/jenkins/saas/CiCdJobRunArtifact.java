package io.jenkins.plugins.propelo.commons.models.jenkins.saas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SEI/Propelo artifact identity for CI/CD correlation.
 * Matches etl-spark JobRunCompleteRequest artifacts shape.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CiCdJobRunArtifact implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("input")
    private Boolean input;

    @JsonProperty("output")
    private Boolean output;

    @JsonProperty("type")
    private String type;

    @JsonProperty("location")
    private String location;

    @JsonProperty("name")
    private String name;

    @JsonProperty("qualifier")
    private String qualifier;

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("metadata")
    private Map<String, String> metadata;

    /** Required for Jenkins XStream persistence. */
    public CiCdJobRunArtifact() {
    }

    public CiCdJobRunArtifact(Boolean input, Boolean output, String type, String location, String name,
                              String qualifier, String hash, Map<String, String> metadata) {
        this.input = input;
        this.output = output;
        this.type = type;
        this.location = location;
        this.name = name;
        this.qualifier = qualifier;
        this.hash = hash;
        this.metadata = metadata == null ? null : new HashMap<>(metadata);
    }

    public Boolean getInput() {
        return input;
    }

    public Boolean getOutput() {
        return output;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getHash() {
        return hash;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CiCdJobRunArtifact that = (CiCdJobRunArtifact) o;
        return Objects.equals(input, that.input)
                && Objects.equals(output, that.output)
                && Objects.equals(type, that.type)
                && Objects.equals(location, that.location)
                && Objects.equals(name, that.name)
                && Objects.equals(qualifier, that.qualifier)
                && Objects.equals(hash, that.hash)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, type, location, name, qualifier, hash, metadata);
    }

    @Override
    public String toString() {
        return "CiCdJobRunArtifact{"
                + "input=" + input
                + ", output=" + output
                + ", type='" + type + '\''
                + ", location='" + location + '\''
                + ", name='" + name + '\''
                + ", qualifier='" + qualifier + '\''
                + ", hash='" + hash + '\''
                + ", metadata=" + metadata
                + '}';
    }
}
