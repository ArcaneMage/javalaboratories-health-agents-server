package org.javalaboratories.healthagents.probes;

import org.javalaboratories.core.Maybe;

public interface Channel {
    int status();
    Maybe<String> output();
}
