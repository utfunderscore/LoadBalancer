FROM eclipse-temurin:21-jdk-jammy as deps

WORKDIR /orchestrator

ADD https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/40/downloads/paper-1.21.1-40.jar /orchestrator
ADD Spigot-1.0-SNAPSHOT-all.jar /orchestrator/plugins/
ADD config.yml /orchestrator/plugins/LoadBalancer/

EXPOSE 25565

CMD ["java", "-Dcom.mojang.eula.agree=true", "-jar", "paper-1.21.1-40.jar"]