package org.javalaboratories.healthagents.model;

import lombok.Value;

import java.util.List;

/**
 * Represents {@code user} value object.
 */
@Value
public class User {
    String username;
    String password;
    List<String> roles;
}
