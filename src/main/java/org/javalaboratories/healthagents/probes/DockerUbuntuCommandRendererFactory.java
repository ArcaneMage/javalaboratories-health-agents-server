package org.javalaboratories.healthagents.probes;

public class DockerUbuntuCommandRendererFactory implements CommandRendererFactory {

    @Override
    public CommandRenderer processStatusRenderer() {
        return new DockerUbuntuProcessStatusCommandRenderer();
    }
}
