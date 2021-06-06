package org.javalaboratories.healthagents.probes;

import org.javalaboratories.core.Try;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LogHealthProbe extends AbstractHealthProbe {

    private static final String LOG_DIRECTORY = System.getProperty("LOG_DIRECTORY");
    private static final String LOG_FILENAME = "health-agents-server.log";
    private static final int DEFAULT_SILENCE_HOURS = 1;

    private final Class<? extends Exception> clazz;
    private final int silenceHours;

    public LogHealthProbe(final Class<? extends Exception> clazz) {
        this(clazz,DEFAULT_SILENCE_HOURS);
    }

    public LogHealthProbe(final Class<? extends Exception> clazz, int silenceHours) {
        this.clazz = Objects.requireNonNull(clazz,"Expected class name");
        this.silenceHours = Math.max(silenceHours, DEFAULT_SILENCE_HOURS);
    }

    /**
     * @return {@code true} to indicate no errors/exceptions probed at this time,
     * otherwise {@code false}.
     */
    @Override
    public boolean detect() {
        return !probeErrorInLogs();
    }

    protected boolean probeErrorInLogs() {
        Path path = Paths.get(String.format("%s%s%s",LOG_DIRECTORY, File.separator, LOG_FILENAME));

        String s = Try.of (() -> new String(Files.readAllBytes(path)))
                .recover(e -> e instanceof IOException ? "" : e)
                .map(Object::toString) // if success, convert to string
                .map(this::findError)
                .orElseThrow(IllegalStateException::new);

        boolean result = false;
        if (s.length() > 0) {
            LocalTime timestamp = LocalTime.parse(s.substring(0,8));
            LocalTime now = LocalTime.now();

            Duration duration = Duration.between(timestamp,now);
            // Alert only if found in the last hour (false == probe is "down").
            result = duration.toHours() <= silenceHours;
        }

        return result;
    }

    private String findError(final String s) {
        String classname = clazz.getSimpleName();
        Pattern p = Pattern.compile(String.format("\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d.*ERROR.*\\n.*%s",classname));
        String result = "";
        Matcher matcher = p.matcher(s);
        if ( matcher.find() ) {
            result = matcher.group();
        }
        return result;
    }
}
