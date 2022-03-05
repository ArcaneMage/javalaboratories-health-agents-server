package org.javalaboratories.healthagents.controller;

import org.javalaboratories.healthagents.probes.AbstractHealthProbe;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class ServiceHealthProbe extends AbstractHealthProbe implements ApplicationContextAware {

    private ApplicationContext context;

    public ServiceHealthProbe() {
        super();
    }

    @Override
    public String getName() {
        return "Health-Agents-Service-Probe";
    }

    @Override
    public boolean detect() {
        Map<String,MainController> result = context.getBeansOfType(MainController.class);
        return result.size() == 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
