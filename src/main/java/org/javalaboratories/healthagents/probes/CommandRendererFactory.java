package org.javalaboratories.healthagents.probes;

import java.util.Objects;

public interface CommandRendererFactory {

    String DOCKER_UBUNTU = "DOCKER-UBUNTU";
    String MACOS  = "MACOS";
    String UBUNTU = "UBUNTU";

    static CommandRendererFactory getInstance(final String factory) {
        String f = Objects.requireNonNull(factory);
        if (f.isEmpty()) {
            f = MACOS;
        } else {
            f = f.toUpperCase();
        }
        switch (f) {
            case DOCKER_UBUNTU: return new DockerUbuntuCommandRendererFactory();
            case MACOS: return new OsxCommandRendererFactory();
            case UBUNTU: return new UbuntuCommandRendererFactory();
            default:
                throw new IllegalArgumentException(String.format("CommandRendererFactory implementation not available " +
                        "for '%s'",factory));
        }
    }

    CommandRenderer processStatusRenderer();

}
