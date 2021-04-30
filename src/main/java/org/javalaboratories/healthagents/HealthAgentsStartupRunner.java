package org.javalaboratories.healthagents;

import org.javalaboratories.healthagents.configuration.ServerBuildInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class is considered to be the bootstrap of the health-agents server.
 * <p>
 * It will report the current version and build of the server. The default log
 * directory is always the current directory unless otherwise specified by the
 * {@code LOG_DIRECTORY}.
 */
@Component
public class HealthAgentsStartupRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(HealthAgentsStartupRunner.class);

    @Autowired
    private ServerBuildInformation build;

    @Autowired
    private Environment environment;

    // LOG_DIRECTORY value set to current directory, value of ".", if unset
    @Value("${LOG_DIRECTORY:.}")
    private String logDirectory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String profiles = Arrays.stream(environment.getActiveProfiles())
                .collect(Collectors.joining(",","[","]"));
        logger.info("--");
        logger.info("Health-Agents Server v{}-({})",build.getVersion(),build.getBuildTimestamp());
        logger.info("Java Laboratories (c) 2021, Kevin Henry (AM)");
        logger.info("Server is now fully operational with the following enabled profile(s) '{}'; and all log output written " +
                "to the '{}' directory.",profiles,logDirectory);
        if (".".equals(logDirectory))
            logger.warn("Note: log directory set to current directory? Consider using the system property 'LOG_DIRECTORY' to" +
                    " override, if necessary.");
        logger.info("--");
    }
}
