FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q
COPY src src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S foodchain && adduser -S foodchain -G foodchain
COPY --from=build /workspace/target/*.jar app.jar
RUN chown foodchain:foodchain app.jar
USER foodchain
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
