package com.flowparser.parser;

import com.flowparser.model.FlowLogEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Parser for AWS VPC flow logs (version 2).
 */
public class FlowLogParser {
    
    /**
     * Parses a flow log file and calls the consumer for each entry.
     * This uses streaming to efficiently handle large files.
     * 
     * @param filePath Path to the flow log file
     * @param consumer Consumer function to be called for each flow log entry
     * @throws IOException If there's an error reading the file
     */
    public void parseFlowLogs(String filePath, Consumer<FlowLogEntry> consumer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    FlowLogEntry entry = parseLine(line);
                    consumer.accept(entry);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to parse line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Parses a single line of the flow log file.
     * 
     * @param line The line to parse
     * @return FlowLogEntry object representing the parsed data
     * @throws IllegalArgumentException If the line format is invalid
     */
    private FlowLogEntry parseLine(String line) {
        String[] parts = line.trim().split("\\s+");
        
        if (parts.length < 14) {
            throw new IllegalArgumentException("Invalid flow log format: insufficient fields");
        }
        
        try {
            int version = Integer.parseInt(parts[0]);
            if (version != 2) {
                throw new IllegalArgumentException("Unsupported flow log version: " + version);
            }
            
            String accountId = parts[1];
            String interfaceId = parts[2];
            String srcAddr = parts[3];
            String dstAddr = parts[4];
            int srcPort = Integer.parseInt(parts[5]);
            int dstPort = Integer.parseInt(parts[6]);
            int protocol = Integer.parseInt(parts[7]);
            int packets = Integer.parseInt(parts[8]);
            int bytes = Integer.parseInt(parts[9]);
            long startTime = Long.parseLong(parts[10]);
            long endTime = Long.parseLong(parts[11]);
            String action = parts[12];
            String logStatus = parts[13];
            
            return new FlowLogEntry(
                version, accountId, interfaceId, srcAddr, dstAddr,
                srcPort, dstPort, protocol, packets, bytes,
                startTime, endTime, action, logStatus
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value in flow log", e);
        }
    }
}