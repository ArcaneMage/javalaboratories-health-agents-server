#!/bin/bash
set echo off
HEALTH_AGENTS_HOME="/opt/health-agents-server"
VM_OPTIONS="-Dspring.profiles.active=cert-ubuntu \
            -DLOG_DIRECTORY=${HEALTH_AGENTS_HOME}/log"
JAVA_OPTS="-Xms192m -Xmx192m"
nohup java ${JAVA_OPTS} -jar ${VM_OPTIONS} ${HEALTH_AGENTS_HOME}/lib/@project.artifactId@-@project.version@.jar > ${HEALTH_AGENTS_HOME}/log/console.log 2>&1 &
echo $! > ${HEALTH_AGENTS_HOME}/health-agents-server.pid
echo "Started health-agents server, PID - $!"
