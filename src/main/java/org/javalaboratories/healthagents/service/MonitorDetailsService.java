package org.javalaboratories.healthagents.service;

import org.javalaboratories.healthagents.repository.MonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MonitorDetailsService implements UserDetailsService {

    @Autowired
    private MonitorRepository repository;

    @Override
    public UserDetails loadUserByUsername(final String s) throws UsernameNotFoundException {
        return repository.findByName(s)
                .map (u -> User.builder()
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRoles().toArray(new String[0]))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found",s)));
    }
}
