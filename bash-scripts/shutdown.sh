#!/bin/bash
set echo off
HEALTH_AGENTS_HOME=/opt/health-agents-server
kill $(cat ${HEALTH_AGENTS_HOME}/health-agents-server.pid)
echo "Signalled health-agents server to terminate"