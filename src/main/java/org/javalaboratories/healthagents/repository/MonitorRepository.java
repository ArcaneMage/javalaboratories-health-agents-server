package org.javalaboratories.healthagents.repository;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.healthagents.model.yaml.IdentityManagement;
import org.javalaboratories.healthagents.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

/**
 * Repository of the privileged users that has access to health-probes.
 * <p>
 * Currently this is hardcoded at the time of writing and am considering
 * persistence storage in the near future.
 */
@Repository
public final class MonitorRepository {

    public static final String ROLE_MONITOR ="MONITOR";

    private static final Logger logger = LoggerFactory.getLogger(MonitorRepository.class);

    public static final String SECURITY_PASSWD_FILE="security-passwd.yml";

    private IdentityManagement identities;

    public MonitorRepository() {
        Yaml yaml = new Yaml(new Constructor(IdentityManagement.class));
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(SECURITY_PASSWD_FILE)) {
            identities = yaml.loadAs(stream,IdentityManagement.class);
        } catch (IOException e) {
            logger.error("Failed to close '{}' stream",SECURITY_PASSWD_FILE);
        }
    }

    public Maybe<User> findByName(final String name) {
        Objects.requireNonNull(name,"Require getName argument");
        return identities.getUsers().stream()
            .filter(u -> u.getName().equals(name))
            .map(u -> Maybe.of(new User(u.getName(),u.getPassword(),Collections.unmodifiableList(u.getRoles()))))
            .reduce(Maybe.empty(),(a,b) -> b);
    }
}
