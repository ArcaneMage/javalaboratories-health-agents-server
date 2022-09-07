# Dockerizes heal-agents-server
#
# Build command:
# docker build --build-arg JAR_FILE=health-agents-server-1.0.8-SNAPSHOT.jar -t health-agents-server:1.0.8 .
#
# Container:
# docker run --detach -p 443:433 -v /tmp:/tmp <image-id>
#
FROM eclipse-temurin:11
MAINTAINER javalaboratories.org

ARG JAR_FILE

ENV HEALTH_AGENTS_HOME=/opt/health-agents-server
ENV VM_OPTIONS="-Dspring.profiles.active=cert-docker -DLOG_DIRECTORY=${HEALTH_AGENTS_HOME}/log" \
    JAVA_OPTS="-Xms192m -Xmx192m" \
    JAVA_MAIN=${JAR_FILE}

WORKDIR ${HEALTH_AGENTS_HOME}

COPY ./target/${JAR_FILE} ./lib/health-agents-server.jar

RUN mkdir ./log

VOLUME /tmp

EXPOSE 443

ENTRYPOINT java -jar $JAVA_OPTS $VM_OPTIONS ./lib/health-agents-server.jar
