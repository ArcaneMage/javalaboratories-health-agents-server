package org.javalaboratories.healthagents.probes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHealthProbe implements HealthProbe {

    protected static final Logger logger = LoggerFactory.getLogger(HealthProbe.class);

    private static final String DEFAULT_PROBE_NAME = "HealthProbe";

    protected AbstractHealthProbe() { }

    public String getName() {
        return DEFAULT_PROBE_NAME;
    }
}
