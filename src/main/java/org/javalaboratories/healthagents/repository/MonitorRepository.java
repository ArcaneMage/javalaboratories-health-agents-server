package org.javalaboratories.healthagents.repository;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.healthagents.cryptography.AesCryptography;
import org.javalaboratories.healthagents.model.yaml.IdentityManagement;
import org.javalaboratories.healthagents.model.User;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

/**
 * Repository of the privileged users that has access to health-probes.
 * <p>
 * The underlying repository is an encrypted yaml file called security-passwd.yml.
 * The structure of which is as follows:
 * <pre>
 * ---
 * # Identity Management of Health-Agent server
 * users:
 *   - name: monitor
 *     password: <password>
 *     roles:
 *       - MONITOR
 *   - name: test
 *     password: <password>
 *     roles:
 *       - NONE
 * </pre>
 * To create a new encrypted file or modify the existing one, consider referring
 * to the AesCryptographyTest class for details.
 */
@Repository
public final class MonitorRepository {

    public static final String ROLE_MONITOR ="MONITOR";
    public static final String SECURITY_PASSWD_FILE="security-passwd.yml.enc";

    private final IdentityManagement identities;

    /**
     * Default constructor reads and processes the encrypted
     * "security-oasswd,yml.enc" file.
     */
    public MonitorRepository() {
        // Read encrypted security file, then decrypt and construct
        // IdentityManagement object
        InputStream stream;
        stream = getResource(SECURITY_PASSWD_FILE);
        Yaml yaml = new Yaml(new Constructor(IdentityManagement.class));
        identities = yaml.loadAs(AesCryptography.decrypt(stream),IdentityManagement.class);
    }

    /**
     * Looks up user details via {code name}
     *
     * @param name name of user to with which to search
     * @return Maybe return user details.
     */
    public Maybe<User> findByName(final String name) {
        Objects.requireNonNull(name,"Require getName argument");
        return identities.getUsers().stream()
            .filter(u -> u.getName().equals(name))
            .map(u -> Maybe.of(new User(u.getName(),u.getPassword(),Collections.unmodifiableList(u.getRoles()))))
            .reduce(Maybe.empty(),(a,b) -> b);
    }

    private InputStream getResource(final String filename) {
        return this.getClass().getClassLoader().getResourceAsStream(filename);
    }
}
