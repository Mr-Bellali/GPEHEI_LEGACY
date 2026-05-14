# Build stage
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# copy project
COPY . .

# compile
RUN javac \
    -cp "lib/*" \
    -d out \
    $(find src -name "*.java")


# Runtime stage
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/out ./out
COPY --from=builder /app/lib ./lib

CMD ["java", "-cp", "out:lib/*", "main.MainApp"]