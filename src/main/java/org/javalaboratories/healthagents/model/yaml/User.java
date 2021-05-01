package org.javalaboratories.healthagents.model.yaml;

import lombok.Data;

import java.util.List;

/**
 * Represents {@code user} persisted object.
 */
@Data
public class User {
    private String name;
    private String password;
    private List<String> roles;
}
