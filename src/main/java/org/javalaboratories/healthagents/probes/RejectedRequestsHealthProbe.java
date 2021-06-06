package org.javalaboratories.healthagents.probes;

import org.springframework.security.web.firewall.RequestRejectedException;

public final class RejectedRequestsHealthProbe extends LogHealthProbe {

    public RejectedRequestsHealthProbe(final int silenceHours) {
        super(RequestRejectedException.class, silenceHours);
    }

    @Override
    public String getName() {
        return "Rejected-Requests-Probe";
    }
}
