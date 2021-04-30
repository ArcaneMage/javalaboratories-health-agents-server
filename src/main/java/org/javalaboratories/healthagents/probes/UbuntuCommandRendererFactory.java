package org.javalaboratories.healthagents.probes;

public final class UbuntuCommandRendererFactory implements CommandRendererFactory{
    @Override
    public CommandRenderer processStatusRenderer() {
        return new UbuntuProcessStatusCommandRenderer();
    }
}
