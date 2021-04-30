package org.javalaboratories.healthagents.probes;

public final class OsxCommandRendererFactory implements CommandRendererFactory {

    @Override
    public CommandRenderer processStatusRenderer() {
        return new OsxProcessStatusCommandRenderer();
    }
}
