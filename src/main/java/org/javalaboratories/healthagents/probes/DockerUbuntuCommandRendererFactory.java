package org.javalaboratories.healthagents.probes;

public final class DockerUbuntuCommandRendererFactory implements CommandRendererFactory {

    @Override
    public CommandRenderer processStatusRenderer() {
        return new DockerUbuntuProcessStatusCommandRenderer();
    }
}
