package org.javalaboratories.healthagents.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic response object encapsulating current state of the probed service.
 */
@Value
public class Response {
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd hh:mm:ss (z)";
    private static final String DEFAULT_AGENT = "Unknown service";
    private static final String DEFAULT_MESSAGE = "No additional information";

    @JsonIgnore
    ZonedDateTime zonedTimestamp;

    String agent;
    int status;
    String meaning;
    String message;
    String timestamp;

    /**
     * Main constructor of this object.
     *
     * @param zonedTimestamp zoned timestamp - current timestamp of response.
     * @param status HTTP status code; 200 to indicate service is up and
     *               functional; 501 to indicate service is down or not
     *               implemented.
     * @param meaning actual meaning of the HTTP status code.
     * @param agent name of probe agent.
     * @param message additional message for clarity.
     */
    @Builder
    public Response(final ZonedDateTime zonedTimestamp, final int status, final String meaning, final String agent,
                    final String message) {
        this.zonedTimestamp = zonedTimestamp;
        this.status = status;
        this.message = message == null ? DEFAULT_MESSAGE : message;
        this.agent = agent == null ? DEFAULT_AGENT : agent;
        this.meaning = meaning;
        this.timestamp = getTimestampAsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Response"+Stream.of(timestamp,agent,status,meaning,message)
                .map(String::valueOf)
                .collect(Collectors.joining(", ","[","]"));
    }

    private String getTimestampAsString() {
        return zonedTimestamp.format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }
}
