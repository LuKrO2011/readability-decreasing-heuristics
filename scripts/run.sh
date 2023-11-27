#!/bin/bash

# Set the paths
programPath="target/readability-decreasing-heuristics-1.0-SNAPSHOT-jar-with-dependencies.jar"
inputPath="input"
outputPath="output"
configPath="res/config-keep-dir.yaml"
probabilitiesPath="res/probabilities.yaml"

# Run the program
java -jar "$programPath" "$inputPath" -o "$outputPath" -c "$configPath" -p "$probabilitiesPath"

exit 0