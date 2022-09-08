package org.javalaboratories.healthagents.probes;

import java.util.ArrayList;
import java.util.List;

/**
 * Docker containers are isolated from the host, therefore accessing the state
 * of the host's processes is not directly possible.
 * <p>
 * To overcome this problem, the host provides a state of the processes in
 * a file (updated in "realtime") in the temporary directory. It is this data
 * that is queried and interrogated by the
 * {@link DockerUbuntuProcessStatusCommandRenderer} class to gain information
 * on any process that resides outside the docker container.
 * <p>
 * The expected structure of the file/data is that the processes must be sorted
 * by user, then by PID in reverse order. The following linux process status
 * command illustrates the expected output: {@code sudo ps aux | sort -n -k2 -r}.
 */
public final class DockerUbuntuProcessStatusCommandRenderer implements CommandRenderer {
    private static final String HOST_PROCESS_STATUS_FILE="process-status-file.txt";

    @Override
    public String[] render(String name) {

        List<String> result = new ArrayList<>();
        result.add("/usr/bin/bash");
        result.add("-c");
        result.add(String.format("cat /tmp/%s | grep \"%s\" | head -n1", HOST_PROCESS_STATUS_FILE,name));
        return result.toArray(new String[0]);
    }
}
