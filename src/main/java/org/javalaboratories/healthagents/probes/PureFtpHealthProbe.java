package org.javalaboratories.healthagents.probes;

public final class PureFtpHealthProbe extends ProcessHealthProbe {

    private static final String FTP_PROCESS_NAME="pure-ftpd";

    public PureFtpHealthProbe(final CommandRendererFactory factory) {
        super(factory);
    }

    @Override
    public String getName() {
        return "Pure-FTP-Probe";
    }

    @Override
    public boolean detect() {
        Channel channel = probeProcessStatus(FTP_PROCESS_NAME);
        boolean result = false;
        if (channel.status() == 0) {
            result = channel.output()
                    .filter(s -> s.length() > 0)
                    .map(s -> s.contains(FTP_PROCESS_NAME))
                    .fold(false,v -> v);
        }
        return result;
    }
}
