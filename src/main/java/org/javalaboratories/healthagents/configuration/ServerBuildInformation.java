package org.javalaboratories.healthagents.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Contains build information of the current project/server.
 */
@Component
@ConfigurationProperties("application-build-info")
@Data
public final class ServerBuildInformation {
    private String artifact;
    private String version;
    private String buildTimestamp;
}
