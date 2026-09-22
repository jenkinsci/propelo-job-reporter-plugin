package io.jenkins.plugins.propelo.job_reporter.extensions;

import hudson.model.InvisibleAction;
import io.jenkins.plugins.propelo.commons.models.jenkins.saas.PhaseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Run-scoped store for {@code seiReportPhase} markers.
 * Instance methods are synchronized for concurrent stages; callers must also
 * synchronize on the {@link hudson.model.Run} when get-or-creating this action
 * so parallel stages share a single instance.
 */
public class PropeloStageMarkerAction extends InvisibleAction {
    private final List<PhaseEvent> events = new ArrayList<>();

    public synchronized void append(PhaseEvent event) {
        if (event != null) {
            events.add(event);
        }
    }

    public synchronized List<PhaseEvent> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public synchronized boolean isEmpty() {
        return events.isEmpty();
    }
}
