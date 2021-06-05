package org.javalaboratories.healthagents.probes;

import org.javalaboratories.core.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public abstract class AbstractHealthProbe implements HealthProbe {

    protected static final Logger logger = LoggerFactory.getLogger(HealthProbe.class);

    private static final String DEFAULT_PROBE_NAME = "HealthProbe";
    private static final InputStream EMPTY_INPUTSTREAM = new ByteArrayInputStream(new byte[]{});
    private static final int MAX_BUFFER_SZ = 16;

    private final CommandRendererFactory rendererFactory;

    public AbstractHealthProbe(final CommandRendererFactory rendererFactory) {
        Objects.requireNonNull(rendererFactory,"Requires CommandRendererFactory object");
        this.rendererFactory = rendererFactory;
    }

    protected AbstractHealthProbe() {
        rendererFactory = null;
    }

    protected Channel probeProcessStatus(final String name) {
        Objects.requireNonNull(name,"Requires process getName");

        int exitValue = -1;
        InputStream stream = EMPTY_INPUTSTREAM;
        Process process = null;
        Channel result;
        try {
            CommandRenderer renderer = rendererFactory.processStatusRenderer();
            ProcessBuilder builder = new ProcessBuilder();
            process = builder
                    .command(renderer.render(name))
                    .redirectErrorStream(true)
                    .start();
            exitValue = process.waitFor();
            stream = process.getInputStream();

            if (exitValue != 0)
                logger.warn("Health probe subprocess returned non-zero exit code '{}'",exitValue);
            else
                logger.info("Health probe subprocess exited successfully");
        } catch (InterruptedException e) {
            logger.error("Current thread interrupted whilst waiting for subprocess to complete");
        } catch (IOException e) {
            logger.error("Failed to communicate with subprocess -- I/O failure: {}",e.getMessage());
        } finally {
            result = toChannel(exitValue,toString(stream));
            logger.trace("Standard output (stdout) from subprocess: {}",
                    result.output()
                            .filterNot(String::isEmpty)
                            .fold("No standard output (stdout) available",s -> s));
            if (process != null)
                process.destroy();
        }

        return result;
    }

    public String getName() {
        return DEFAULT_PROBE_NAME;
    }

    private static Channel toChannel(final int code, final Maybe<String> output) {
        return new Channel() {
            @Override
            public int status() {
                return code;
            }
            @Override
            public Maybe<String> output() {
                return output;
            }
        };
    }

    private Maybe<String> toString(final InputStream stream) {
        Maybe<String> result = Maybe.empty();
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            char[] buffer = new char[MAX_BUFFER_SZ];
            int read;
            StringBuilder b = new StringBuilder();
            while ((read = reader.read(buffer,0,MAX_BUFFER_SZ)) > -1) {
                b.append(buffer,0,read);
            }
            result = Maybe.of(b.toString());
        } catch (IOException e) {
            // I/O Exception Handled
        }
        return result;
    }
}
