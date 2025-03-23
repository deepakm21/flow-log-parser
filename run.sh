#!/bin/bash

# Script to build and run the Flow Log Parser application

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is required but not installed. Please install Maven."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is required but not installed. Please install Java."
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ -z "$java_version" ]]; then
    echo "Could not determine Java version. Proceeding anyway..."
elif [[ ! "$java_version" =~ ^1[4-9]\.|^[2-9][0-9] ]]; then
    echo "Java 14 or higher is required. Found: $java_version"
    exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Check if jar was created
if [ ! -f "target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar" ]; then
    echo "JAR file not found. Build may have failed."
    exit 1
fi

# Check command line arguments
if [ $# -lt 3 ]; then
    echo "Usage: $0 <flow_log_file> <lookup_table_file> <output_file>"

    # Ask user if they want to generate sample data
    read -p "Do you want to generate sample data for testing? (y/n): " generate_data
    if [[ "$generate_data" == "y" || "$generate_data" == "Y" ]]; then
        echo "Generating sample data..."
        java -cp target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar com.flowparser.util.SampleDataGenerator
        echo "Sample data generated: sample_flow_logs.txt and sample_lookup_table.csv"

        # Ask user if they want to run with sample data
        read -p "Do you want to run the application with the sample data? (y/n): " run_sample
        if [[ "$run_sample" == "y" || "$run_sample" == "Y" ]]; then
            FLOW_LOG_FILE="sample_flow_logs.txt"
            LOOKUP_TABLE_FILE="sample_lookup_table.csv"
            OUTPUT_FILE="output_stats.csv"
        else
            echo "Exiting. You can run the application later with your own data."
            exit 0
        fi
    else
        echo "Exiting. Please provide the required files."
        exit 0
    fi
else
    FLOW_LOG_FILE="$1"
    LOOKUP_TABLE_FILE="$2"
    OUTPUT_FILE="$3"

    # Check if input files exist
    if [ ! -f "$FLOW_LOG_FILE" ]; then
        echo "Flow log file not found: $FLOW_LOG_FILE"
        exit 1
    fi

    if [ ! -f "$LOOKUP_TABLE_FILE" ]; then
        echo "Lookup table file not found: $LOOKUP_TABLE_FILE"
        exit 1
    fi
fi

# Run the application
echo "Running Flow Log Parser with:"
echo "  Flow Log File: $FLOW_LOG_FILE"
echo "  Lookup Table: $LOOKUP_TABLE_FILE"
echo "  Output File: $OUTPUT_FILE"
echo ""

java -jar target/flow-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar "$FLOW_LOG_FILE" "$LOOKUP_TABLE_FILE" "$OUTPUT_FILE"

if [ $? -eq 0 ]; then
    echo "Processing completed successfully."
    echo "Output written to: $OUTPUT_FILE"

    # Show preview of output file
    echo "Preview of output file:"
    head -n 10 "$OUTPUT_FILE"
    echo "..."
else
    echo "Processing failed. Please check the errors above."
    exit 1
fi