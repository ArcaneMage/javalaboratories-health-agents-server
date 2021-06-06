package org.javalaboratories.healthagents.probes;

public final class OpenVpnHealthProbe extends ProcessHealthProbe {

    private static final String VPN_PROCESS_NAME="openvpn";
    private static final String VPN_CONFIG_DIR  ="/etc/openvpn";

    public OpenVpnHealthProbe(final CommandRendererFactory factory) {
        super(factory);
    }

    @Override
    public String getName() {
        return "VPN-Probe";
    }

    @Override
    public boolean detect() {
        Channel channel = probeProcessStatus(VPN_PROCESS_NAME);
        boolean result = false;
        if (channel.status() == 0) {
            result = channel.output()
                    .filter(s -> s.length() > 0)
                    .map(s -> s.contains(VPN_CONFIG_DIR))
                    .fold(false,v -> v);
        }
        return result;
    }
}
