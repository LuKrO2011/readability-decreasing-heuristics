# Readability Decreasing Heuristics

The Readability Decreasing Heuristics (RDH) project is a Java code processing tool that employs a collection of
heuristics to intentionally reduce the readability of the code. This project achieves its goal by transforming the
Abstract Syntax Tree (AST) of the code using [Spoon](https://github.com/LuKrO2011/spoon.git), a powerful library for
Java code analysis and transformation. The primary objective of this project is to generate training data for a source
code readability classifier.

## Usage

To execute the code within this project, ensure that you have [Maven](https://maven.apache.org/) installed. Follow these
steps to run the project:

1. Clone the repository:
   ```bash
   git clone https://github.com/LuKrO2011/readability-decreasing-heuristics.git

2. Navigate to the project directory:
   ```bash
   cd readability-decreasing-heuristics
   ```

3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

4. Run the project using the generated jar file:
   ```bash
   java -jar target/readability-decreasing-heuristics-1.0-SNAPSHOT-jar-with-dependencies.jar <input-path> -o <output-path>
    ```

## Parameters

The following command-line options are available for running the `readability-decreasing-heuristics` tool:

- `-h`, `--help`: Display the help message and exit.
- `-c=<configPath>`, `--config=<configPath>`: Provide the path to a configuration file for the tool. If not specified,
  the default configuration will be used.
- `-o=<outputPath>`, `--output=<outputPath>`: Specify the path to the output directory. If not provided, the output will
  be written to the input directory (or file).
- `-p=<probabilitiesPath>`, `--probs`, `--probabilities=<probabilitiesPath>`: Supply the path to a configuration file
  containing probability distributions for the refactorings. If not specified, the default configuration will be used.
- `--seed=<seed>`: Initialize the random instance with a given seed to ensure reproducible runs.
- `-V`, `--version`: Print version information and exit.

## Project Details

- Java SDK: 17
- Language Level: 20
- Build Tool: Maven

For more information about the `Spoon` library and its capabilities, please refer to the
official [Spoon Website](https://spoon.gforge.inria.fr).

Feel free to explore, experiment, and contribute to this project to further its objectives in generating training data
for source code readability classification.

## Developers

The project requires certain changes on the Spoon library.

### Change to Local Spoon-Fork

This [Fork](https://github.com/LuKrO2011/spoon.git) is used to apply the required changes. The following steps are
required (besides [Usage](#usage)) to use the fork in this project:

1. Clone the fork and build it:
    ```bash
    cd ..
    git clone https://github.com/LuKrO2011/spoon.git
    cd spoon
    mvn clean package -DskipTests
    cd readability-decreasing-heuristics
    ```

2. Change the pom.xml file to use the fork:
    - Change the spoon repository:
    ```xml
   <repository>
        <id>fr.inria.gforge.spoon</id>
        <url>file://${project.basedir}/../spoon</url>
    </repository>
    ```

    - Change the spoon dependency:
    ```xml
    <dependency>
        <groupId>fr.inria.gforge.spoon</groupId>
        <artifactId>spoon-core</artifactId>
        <version>10.4.1-FORK-jar-with-dependencies</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/../spoon/target/spoon-core-10.4.1-FORK-jar-with-dependencies.jar</systemPath>
    </dependency>
    ```

3. Build the project as usual:
    ```bash
    mvn clean install
    ```

### Change to Git Spoon-Fork

To change the project to use a fork of a repository, change the following lines in the pom.xml file:

- Change the spoon repository:
    ```xml
    <repository>
    <id>fr.inria.gforge.spoon</id>
    <name>Spoon</name>
    <url>https://github.com/LuKrO2011/spoon.git</url>
    </repository>
    ```
  
- Change the spoon dependency:
  ```xml
  <dependency>
      <groupId>fr.inria.gforge.spoon</groupId>
      <artifactId>spoon-core</artifactId>
      <version>10.4.1-FORK-jar-with-dependencies</version>
      <scope>compile</scope>
  </dependency>
  ```

### For debugging
```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>10.4.1-FORK</version>
</dependency>
```