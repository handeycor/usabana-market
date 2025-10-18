FROM gradle:8.14-jdk21 AS build

WORKDIR /app

# copiar wrapper y proyecto dentro de /app (rutas relativas al WORKDIR)
COPY gradlew ./
COPY gradle ./gradle
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle settings.gradle ./
COPY src ./src

# usar el wrapper para garantizar la versión y generar el artefacto ejecutable
RUN gradle clean bootJar --no-daemon -x test

FROM eclipse-temurin:21-jdk
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=market
ENV DB_USER=usabana
ENV DB_PASSWORD=usabana123

WORKDIR /app

# copiar el .jar con extensión desde la etapa build
COPY --from=build /app/build/libs/market-1.0.0-SNAPSHOT.jar ./app.jar

EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]
