package org.javalaboratories.healthagents.probes;

public interface CommandRendererFactory {

    String MACOS  = "MACOS";
    String UBUNTU = "UBUNTU";

    static CommandRendererFactory getInstance(final String factory) {
        String f = factory;
        if (f == null || f.isEmpty()) {
            f = MACOS;
        } else {
            f = f.toUpperCase();
        }
        switch (f) {
            case MACOS: return new OsxCommandRendererFactory();
            case UBUNTU: return new UbuntuCommandRendererFactory();
            default:
                throw new IllegalArgumentException(String.format("CommandRendererFactory implementation not available " +
                        "for '%s'",factory));
        }
    }

    CommandRenderer processStatusRenderer();

}
