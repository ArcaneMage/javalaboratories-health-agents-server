package org.javalaboratories.healthagents.model.yaml;

import lombok.Data;

import java.util.List;

@Data
public final class IdentityManagement {
    private List<User> users;
}