# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG APP_VERSION=2.1.0
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" \
    SHOP_PROFILE=production
RUN addgroup -S shop && adduser -S backoffice -G shop
COPY --from=build /src/target/orders-backoffice.jar /opt/shop/orders-backoffice.jar
EXPOSE 8080
USER backoffice
HEALTHCHECK --interval=30s CMD wget -qO- http://localhost:8080/health/live || exit 1
# ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y", "-jar", "/opt/shop/orders-backoffice.jar"]
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/shop/orders-backoffice.jar"]
