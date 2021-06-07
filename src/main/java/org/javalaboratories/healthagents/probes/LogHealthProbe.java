package org.javalaboratories.healthagents.probes;

import org.javalaboratories.core.Try;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LogHealthProbe extends AbstractHealthProbe {

    public static final int DEFAULT_ALERT_TTL_MINUTES = 15;

    private static final String EXCEPTION_PATTERN = "\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d.*ERROR.*\\n.*%s";
    private static final String LOG_DIRECTORY = System.getProperty("LOG_DIRECTORY",".");
    private static final String LOG_FILENAME = "health-agents-server.log";

    private final Class<? extends Exception> clazz;
    private final int alertTtlMinutes;

    public LogHealthProbe(final Class<? extends Exception> clazz, final int alertTtlMinutes) {
        this.clazz = Objects.requireNonNull(clazz,"Expected class name");
        this.alertTtlMinutes = Math.max(alertTtlMinutes, DEFAULT_ALERT_TTL_MINUTES);
    }

    /**
     * @return The length of time (time-to-live) for which an exception is reported.
     */
    public int getAlertTtlMinutes() {
        return alertTtlMinutes;
    }

    /**
     * @return {@code true} to indicate no errors/exceptions probed at this time,
     * otherwise {@code false}.
     */
    @Override
    public boolean detect() {
        return !probeErrors();
    }

    protected boolean probeErrors() {
        Path path = Paths.get(String.format("%s%s%s",LOG_DIRECTORY, File.separator, LOG_FILENAME));

        return Try.of(() -> new String(Files.readAllBytes(path)))
                .map(this::probeErrors)
                .orElseThrow(() -> new IllegalStateException("Log directory/file not found"));
    }

    private boolean probeErrors(final String s) {
        String result = "";
        String classname = clazz.getSimpleName();
        Pattern p = Pattern.compile(String.format(EXCEPTION_PATTERN,classname));

        Matcher matcher = p.matcher(s);
        boolean alert = false;
        while (matcher.find() && !alert) {
            result = matcher.group();
            if (result.length() > 0) {
                LocalTime timestamp = LocalTime.parse(result.substring(0,8));
                LocalTime now = LocalTime.now();

                Duration duration = Duration.between(timestamp,now).abs();
                // Alert this exception only if with alert TTL (time-to-live).
                alert = duration.toMinutes() < alertTtlMinutes;
            }
        }
        return alert;
    }
}
