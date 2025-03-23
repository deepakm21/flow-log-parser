package com.flowparser;

import com.flowparser.model.Statistics;
import com.flowparser.parser.LookupTableParser;
import com.flowparser.processor.FlowLogProcessor;
import com.flowparser.writer.StatisticsWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class IntegrationTest {

    @Test
    void testEndToEndFlow() throws IOException {
        // Create lookup table file
        String lookupTableData = "dstport,protocol,tag\n" +
                                "443,tcp,sv_P2\n" +
                                "23,tcp,sv_P1\n" +
                                "25,tcp,sv_P1\n" +
                                "110,tcp,email\n" +
                                "993,tcp,email\n" +
                                "143,tcp,email\n";
        Path lookupTableFile = Files.createTempFile("lookup", ".csv");
        Files.writeString(lookupTableFile, lookupTableData);
        
        // Create flow log file
        String flowLogData = "2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 49153 443 6 25 20000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-4d3c2b1a 192.168.1.100 203.0.113.101 49154 23 6 15 12000 1620140761 1620140821 REJECT OK\n" +
                "2 123456789012 eni-9h8g7f6e 172.16.0.100 203.0.113.102 49156 110 6 12 9000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-7i8j9k0l 172.16.0.101 192.0.2.203 49157 993 6 8 5000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-6m7n8o9p 10.0.2.200 198.51.100.4 49158 143 6 18 14000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-5e6f7g8h 192.168.1.101 198.51.100.3 49155 80 6 10 8000 1620140761 1620140821 ACCEPT OK\n";

        Path flowLogFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(flowLogFile, flowLogData);
        
        // Create output file
        Path outputFile = Files.createTempFile("output", ".csv");
        
        // Process the flow logs
        Map<String, String> lookupTable = new LookupTableParser().parseLookupTable(lookupTableFile.toString());
        FlowLogProcessor processor = new FlowLogProcessor(lookupTable);
        Statistics statistics = processor.processFlowLogs(flowLogFile.toString());
        StatisticsWriter writer = new StatisticsWriter();
        writer.writeStatistics(statistics, outputFile.toString());
        
        // Read the output file
        List<String> lines = Files.readAllLines(outputFile);
        
        // Clean up
        Files.delete(lookupTableFile);
        Files.delete(flowLogFile);
        Files.delete(outputFile);
        
        // Verify tag counts
        assertTrue(lines.contains("email,3"));
        assertTrue(lines.contains("sv_p1,1")); // lowercase 'p'
        assertTrue(lines.contains("sv_p2,1")); // lowercase 'p'
        assertTrue(lines.contains("Untagged,1"));
        
        // Verify port/protocol counts
        assertTrue(lines.contains("443,tcp,1"));
        assertTrue(lines.contains("23,tcp,1"));
        assertTrue(lines.contains("110,tcp,1"));
        assertTrue(lines.contains("993,tcp,1"));
        assertTrue(lines.contains("143,tcp,1"));
        assertTrue(lines.contains("80,tcp,1"));
    }

    @Test
    void testWithSampleDataFiles() throws IOException, URISyntaxException {
        // Use ClassLoader to find resources (works in any environment)
        ClassLoader classLoader = getClass().getClassLoader();
        URL flowLogUrl = classLoader.getResource("sample-data/sample_flow_logs.txt");
        URL lookupTableUrl = classLoader.getResource("sample-data/sample_lookup_table.csv");

        // Skip test if resources aren't available (instead of failing)
        assumeTrue(flowLogUrl != null, "Sample flow log file not found in resources");
        assumeTrue(lookupTableUrl != null, "Sample lookup table file not found in resources");

        // Convert URLs to paths in a platform-independent way
        Path flowLogFile = Paths.get(flowLogUrl.toURI());
        Path lookupTableFile = Paths.get(lookupTableUrl.toURI());

        // Create output file
        Path outputFile = Files.createTempFile("output", ".csv");

        // Process with the sample files
        Map<String, String> lookupTable = new LookupTableParser().parseLookupTable(lookupTableFile.toString());
        FlowLogProcessor processor = new FlowLogProcessor(lookupTable);
        Statistics statistics = processor.processFlowLogs(flowLogFile.toString());
        StatisticsWriter writer = new StatisticsWriter();
        writer.writeStatistics(statistics, outputFile.toString());

        // Read the output file
        List<String> lines = Files.readAllLines(outputFile);

        // Clean up
        Files.delete(outputFile);

        // Verify expected tag counts
        assertTrue(lines.contains("Untagged,8"));
        assertTrue(lines.contains("email,3"));
        assertTrue(lines.contains("sv_p1,2"));
        assertTrue(lines.contains("sv_p2,1"));

        // Verify some port/protocol combinations
        assertTrue(lines.contains("443,tcp,1"));
        assertTrue(lines.contains("23,tcp,1"));
        assertTrue(lines.contains("110,tcp,1"));
    }

}