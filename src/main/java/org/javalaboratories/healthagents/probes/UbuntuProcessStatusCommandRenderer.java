package org.javalaboratories.healthagents.probes;

import java.util.ArrayList;
import java.util.List;

public final class UbuntuProcessStatusCommandRenderer implements CommandRenderer {
    @Override
    public String[] render(String name) {
        // Retrieve list of processes with the given processName pattern, sort
        // PID in descending order and return first in the list.
        // /usr/bin/bash -c sudo ps aux | grep "%s" | sort -n -k2 -r | grep -v grep | head -n1

        List<String> result = new ArrayList<>();
        result.add("/usr/bin/bash");
        result.add("-c");
        result.add(String.format("sudo ps aux | grep \"%s\" | sort -n -k2 -r | grep -v grep | head -n1",name));
        return result.toArray(new String[0]);
    }
}
