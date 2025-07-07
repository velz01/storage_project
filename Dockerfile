#
#FROM maven:3.9.10-eclipse-temurin-21 AS builder
#WORKDIR /app
#
#
#COPY pom.xml ./
#RUN mvn dependency:go-offline -B
#
#
#COPY src/ src/
#RUN mvn clean package
#
#
#FROM amazoncorretto:21
#WORKDIR /app
#
#
#COPY --from=builder /app/target/*.jar ./app.jar
#
#
#EXPOSE 8090
#
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
# Stage 1: Сборка приложения
#FROM maven:3.9.10-eclipse-temurin-21 AS builder
#WORKDIR /app
#COPY . .
#RUN mvn clean package
#
## Stage 2: Запуск приложения
#FROM amazoncorretto:21
#WORKDIR /app
#COPY --from=builder /app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]
# —————————————————————————————
# Stage 1: сборка с помощью Maven
# —————————————————————————————
# stage 1: build
#FROM maven:3.9.10-eclipse-temurin-21 AS builder
#WORKDIR /app
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
#COPY src/ src/
#RUN mvn clean package -DskipTests
#
## stage 2: runtime
#FROM amazoncorretto:21
#WORKDIR /app
#
#COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar
#
#EXPOSE 8090
#ENTRYPOINT ["java","-jar","app.jar"]
