FROM eclipse-temurin:21-jdk-jammy

WORKDIR /loadbalancer

ADD https://api.papermc.io/v2/projects/velocity/versions/3.3.0-SNAPSHOT/builds/436/downloads/velocity-3.3.0-SNAPSHOT-436.jar /loadbalancer/Proxy.jar
ADD OrchestratorHook.jar /loadbalancer/plugins/
ADD forwarding.secret /loadbalancer/
ADD velocity.toml /loadbalancer/

EXPOSE 25565

#CMD ["ls"]
CMD ["java", "-jar", "Proxy.jar"]
