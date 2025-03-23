# Flow Log Parser

A Java application that parses AWS VPC flow log data and maps each record to a tag based on a lookup table.

## Overview

This application processes AWS VPC flow logs (version 2) and tags each log entry based on destination port and protocol combinations defined in a lookup table. It then generates statistics on tag counts and port/protocol combination counts.

## Features

- Parses AWS VPC flow logs (version 2 format only)
- Maps logs to tags based on a lookup table
- Case-insensitive matching
- Efficiently handles large files (up to 10MB) using streaming
- Supports lookup tables with up to 10,000 mappings
- Generates statistics on tag counts and port/protocol combination counts

## Requirements

- Java 14 or higher
- Maven 3.6 or higher

## Building the Application

To build the application, run:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory named `flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Testing

To run the tests, use:

```bash
mvn test
```

The test coverage includes:
- Unit tests for individual components
- Integration test for the end-to-end flow
- Edge cases (empty files, invalid entries, etc.)

## Running Locally

There are several ways to run the Flow Log Parser application locally after cloning the repository:

### Option 1: Using Maven and Java directly

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Run with the JAR file:
   ```bash
   java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar <flow_log_file> <lookup_table_file> <output_file>
   ```

   Example:
   ```bash
   java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar src/main/resources/sample-data/sample_flow_logs.txt src/main/resources/sample-data/sample_lookup_table.csv output_stats.csv
   ```

### Option 2: Using the provided run scripts

The project includes convenience scripts that handle building and running the application.

#### On Unix/Linux/MacOS:
```bash
chmod +x run.sh  # Make the script executable (first time only)
./run.sh <flow_log_file> <lookup_table_file> <output_file>
```

Example:
```bash
./run.sh src/main/resources/sample-data/sample_flow_logs.txt src/main/resources/sample-data/sample_lookup_table.csv output_stats.csv
```

#### On Windows:
```
run.bat <flow_log_file> <lookup_table_file> <output_file>
```

Example:
```
run.bat src\main\resources\sample-data\sample_flow_logs.txt src\main\resources\sample-data\sample_lookup_table.csv output_stats.csv
```

If no parameters are provided to the scripts, they will offer to generate sample data and run the application with those files.

### Option 3: Generating sample data

If you don't have your own flow logs and lookup table, you can generate sample data:

```bash
java -cp target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar com.flowparser.util.SampleDataGenerator
```

This will create:
- `sample_flow_logs.txt` in the current directory
- `sample_lookup_table.csv` in the current directory

Then you can run the application with these generated files:

```bash
java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar sample_flow_logs.txt sample_lookup_table.csv output_stats.csv
```

## Usage

```bash
java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar <flow_log_file> <lookup_table_file> <output_file>
```

### Parameters

- `flow_log_file`: Path to the flow log file to process
- `lookup_table_file`: Path to the lookup table CSV file
- `output_file`: Path to save the output statistics

### Example

```bash
java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar sample_flow_logs.txt sample_lookup_table.csv output_stats.csv
```

## Input File Formats

### Flow Log Format

The application expects AWS VPC flow logs in version 2 format. Each line should have the following fields:

```
<version> <account-id> <interface-id> <srcaddr> <dstaddr> <srcport> <dstport> <protocol> <packets> <bytes> <start> <end> <action> <log-status>
```

Example:
```
2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 49153 443 6 25 20000 1620140761 1620140821 ACCEPT OK
```

### Lookup Table Format

The lookup table should be a CSV file with the following header and columns:

```
dstport,protocol,tag
```

Example:
```
dstport,protocol,tag
25,tcp,sv_P1
68,udp,sv_P2
23,tcp,sv_P1
443,tcp,sv_P2
110,tcp,email
```

## Output Format

The application generates an output file with statistics in the following format:

```
Tag Counts:
Tag,Count
<tag1>,<count1>
<tag2>,<count2>
...
Untagged,<count>

Port/Protocol Combination Counts:
Port,Protocol,Count
<port1>,<protocol1>,<count1>
<port2>,<protocol2>,<count2>
...
```

## Assumptions and Limitations

- Only AWS VPC flow log version 2 is supported
- Only default flow log format is supported (not custom formats)
- The application uses case-insensitive matching for protocols and tags
- The flow log file is assumed to be in plain text (ASCII) format
- The lookup table is assumed to be a valid CSV file with the required header
- Flow log entries with invalid format are skipped with a warning
- Protocol numbers are converted to names (e.g., 6 -> tcp, 17 -> udp, 1 -> icmp)

## Sample Data Generation

The application includes a utility class to generate sample data for testing:

```bash
java -cp target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar com.flowparser.util.SampleDataGenerator
```

This will generate:
- `sample_flow_logs.txt`: A sample flow log file with 1000 entries
- `sample_lookup_table.csv`: A sample lookup table with common ports and protocols

## Dependencies

The application uses minimal external dependencies:
- JUnit and Mockito (for testing only)
- Java standard libraries

## License

[MIT License](LICENSE)