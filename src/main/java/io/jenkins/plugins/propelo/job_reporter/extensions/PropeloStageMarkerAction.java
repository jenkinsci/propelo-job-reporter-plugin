package io.jenkins.plugins.propelo.job_reporter.extensions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.InvisibleAction;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.PhaseEvent;
import io.jenkins.plugins.propelo.commons.utils.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Run-scoped store for {@code seiReportPhase} markers.
 * Events are persisted as a JSON string so Jenkins XStream does not deep-serialize
 * {@link java.util.ArrayList} internals (breaks under Java 17 without {@code --add-opens}).
 * Instance methods are synchronized for concurrent stages; callers must also
 * synchronize on the {@link hudson.model.Run} when get-or-creating this action
 * so parallel stages share a single instance.
 */
public class PropeloStageMarkerAction extends InvisibleAction implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final Logger LOGGER = Logger.getLogger(PropeloStageMarkerAction.class.getName());
    private static final ObjectMapper MAPPER = JsonUtils.buildObjectMapper();
    private static final TypeReference<List<PhaseEvent>> EVENTS_TYPE = new TypeReference<List<PhaseEvent>>() {};

    /** Only field XStream persists — plain String is Java 17 safe. */
    private String eventsJson = "[]";

    private transient List<PhaseEvent> events;

    public synchronized void append(PhaseEvent event) {
        if (event == null) {
            return;
        }
        ensureEvents();
        events.add(event);
        persist();
    }

    public synchronized List<PhaseEvent> snapshot() {
        ensureEvents();
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public synchronized boolean isEmpty() {
        ensureEvents();
        return events.isEmpty();
    }

    private void ensureEvents() {
        if (events != null) {
            return;
        }
        events = deserialize(eventsJson);
    }

    private void persist() {
        try {
            eventsJson = MAPPER.writeValueAsString(events);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to serialize phase events for Run persistence", e);
            eventsJson = "[]";
        }
    }

    private static List<PhaseEvent> deserialize(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<PhaseEvent> parsed = MAPPER.readValue(json, EVENTS_TYPE);
            return parsed == null ? new ArrayList<>() : new ArrayList<>(parsed);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to deserialize persisted phase events; starting empty", e);
            return new ArrayList<>();
        }
    }

    private Object readResolve() {
        events = null;
        if (eventsJson == null) {
            eventsJson = "[]";
        }
        return this;
    }
}
