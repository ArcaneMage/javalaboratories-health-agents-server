package org.javalaboratories.healthagents.probes;

public interface HealthProbe {
    String getName();
    boolean detect();
}
