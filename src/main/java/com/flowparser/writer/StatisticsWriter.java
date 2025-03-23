package com.flowparser.writer;

import com.flowparser.model.Statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Writes statistics to an output file.
 */
public class StatisticsWriter {

    /**
     * Writes statistics to the specified output file.
     * 
     * @param statistics The statistics to write
     * @param outputFile Path to the output file
     * @throws IOException If there's an error writing to the file
     */
    public void writeStatistics(Statistics statistics, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write tag counts
            writer.write("Tag Counts:");
            writer.newLine();
            writer.write("Tag,Count");
            writer.newLine();
            
            // Sort tags by count (descending) and write them
            statistics.getTagCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    try {
                        writer.write(entry.getKey() + "," + entry.getValue());
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException("Error writing tag statistics", e);
                    }
                });
            
            writer.newLine();
            
            // Write port/protocol combination counts
            writer.write("Port/Protocol Combination Counts:");
            writer.newLine();
            writer.write("Port,Protocol,Count");
            writer.newLine();
            
            // Extract and sort port/protocol combinations
            statistics.getPortProtocolCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    try {
                        String[] parts = entry.getKey().split(",");
                        writer.write(parts[0] + "," + parts[1] + "," + entry.getValue());
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException("Error writing port/protocol statistics", e);
                    }
                });
        }
    }
}