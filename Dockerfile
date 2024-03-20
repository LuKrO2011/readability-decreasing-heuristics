# Use the official Corretto 17 base image
FROM amazoncorretto:17

# Create a working directory
WORKDIR /app

# Copy jar
COPY /target/readability-decreasing-heuristics-1.0-SNAPSHOT-jar-with-dependencies.jar /app/rdh.jar