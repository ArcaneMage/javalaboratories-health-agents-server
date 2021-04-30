package org.javalaboratories.healthagents.repository;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.healthagents.model.User;
import org.springframework.stereotype.Repository;

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
    public static final String AUTHORISED_USER = "monitor";
    public static final String AUTHORISED_TEST_USER = "test";
    public static final String ROLE_MONITOR ="MONITOR";
    public static final String ROLE_NONE = "NONE";

    private static final String AUTHORISED_USER_PASSWORD = "6128-7314@javalaboratories.org";
    private static final String AUTHORISED_TEST_USER_PASSWORD = "FFFF-00FF@javalaboratories.org";

    public Maybe<User> findByName(final String name) {
        Objects.requireNonNull(name,"Require getName argument");

        Maybe<User> result;
        switch(name) {
            case AUTHORISED_USER:
                result = Maybe.of(new User(AUTHORISED_USER, AUTHORISED_USER_PASSWORD, Collections.singletonList(ROLE_MONITOR)));
                break;
            case AUTHORISED_TEST_USER:
                result = Maybe.of(new User(AUTHORISED_TEST_USER, AUTHORISED_TEST_USER_PASSWORD, Collections.singletonList(ROLE_NONE)));
                break;
            default:
                result = Maybe.empty();
        }
        return result;
    }
}
