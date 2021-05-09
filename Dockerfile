FROM openjdk:11
ADD health-agents-server-1.0.4-SNAPSHOT.zip /opt/health-agents-server-1.0.4-SNAPSHOT.zip
WORKDIR /opt
RUN unzip health-agents-server-1.0.4-SNAPSHOT.zip
WORKDIR /opt/health-agents-server/bin
RUN chmod 750 *
EXPOSE 443
