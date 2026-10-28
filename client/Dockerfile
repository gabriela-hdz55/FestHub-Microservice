# ---- build the CLI jar ----
FROM maven:3.9-eclipse-temurin-21 AS cli-build
WORKDIR /app
COPY cli/pom.xml .
RUN mvn -q dependency:go-offline
COPY cli/src ./src
RUN mvn -q package -DskipTests

# ---- runtime: nginx + JRE + CLI ----
FROM nginx:alpine

# Java runtime for the CLI
RUN apk add --no-cache openjdk21-jre-headless

# CLI jar and a launcher so users can just type "platform ..."
COPY --from=cli-build /app/target/cli.jar /opt/cli/cli.jar
RUN printf '#!/bin/sh\nexec java -jar /opt/cli/cli.jar "$@"\n' > /usr/local/bin/platform \
    && chmod +x /usr/local/bin/platform

# Web client
COPY client/nginx.conf /etc/nginx/conf.d/default.conf
COPY client/src/ /usr/share/nginx/html/

# Default API target for the CLI (nginx in this same container proxies /api to the gateway)
ENV API_URL=http://localhost