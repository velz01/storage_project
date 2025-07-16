# Stage 1: Сборка приложения с помощью Maven и JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Рабочая директория внутри контейнера
WORKDIR /app

# Кешируем скачивание зависимостей: сначала копируем только pom.xml
COPY pom.xml .

# Загружаем все зависимости офлайн
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем JAR (пропускаем тесты для скорости)
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Минимальный runtime-образ с JRE 21
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Копируем собранный JAR из builder-stage
COPY --from=builder /app/target/*.jar app.jar

# Опционально можно передавать JVM‑опции через переменную
ENV JAVA_OPTS=""

# Команда запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
