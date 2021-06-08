package org.javalaboratories.healthagents.probes;

import org.springframework.security.web.firewall.RequestRejectedException;

public final class RequestsHealthProbe extends LogHealthProbe {

    public RequestsHealthProbe(final int alertTTL) {
        super(RequestRejectedException.class, alertTTL);
    }

    @Override
    public String getName() {
        return "Requests-Probe";
    }
}
