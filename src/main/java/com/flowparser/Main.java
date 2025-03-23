package com.flowparser;

import com.flowparser.model.Statistics;
import com.flowparser.parser.LookupTableParser;
import com.flowparser.processor.FlowLogProcessor;
import com.flowparser.writer.StatisticsWriter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Main class for the Flow Log Parser application.
 * This application parses flow logs and tags them based on a lookup table.
 */
public class Main {
    private static final String USAGE = "Usage: java -jar flow-log-parser.jar <flow_log_file> <lookup_table_file> <output_file>";

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(USAGE);
            return;
        }

        String flowLogFile = args[0];
        String lookupTableFile = args[1];
        String outputFile = args[2];

        try {
            // Check if files exist
            validateFileExists(flowLogFile);
            validateFileExists(lookupTableFile);

            // Load the lookup table
            Map<String, String> lookupTable = new LookupTableParser().parseLookupTable(lookupTableFile);

            // Process the flow logs
            FlowLogProcessor processor = new FlowLogProcessor(lookupTable);
            Statistics statistics = processor.processFlowLogs(flowLogFile);

            // Write statistics to output file
            StatisticsWriter writer = new StatisticsWriter();
            writer.writeStatistics(statistics, outputFile);

            System.out.println("Processing completed successfully.");
            System.out.println("Output written to: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void validateFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File not found: " + filePath);
        }
    }
}