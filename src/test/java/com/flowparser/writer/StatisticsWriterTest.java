package com.flowparser.writer;

import com.flowparser.model.Statistics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsWriterTest {

    @Test
    void testWriteStatistics() throws IOException {
        // Create statistics
        Statistics statistics = new Statistics();
        statistics.addTag("sv_p1");
        statistics.addTag("sv_p1");
        statistics.addTag("sv_p2");
        statistics.addTag(null); // Untagged
        
        statistics.addPortProtocol(443, "tcp");
        statistics.addPortProtocol(23, "tcp");
        statistics.addPortProtocol(80, "tcp");
        statistics.addPortProtocol(443, "tcp"); // Duplicate
        
        // Create temporary output file
        Path tempFile = Files.createTempFile("stats", ".csv");
        
        // Write statistics
        StatisticsWriter writer = new StatisticsWriter();
        writer.writeStatistics(statistics, tempFile.toString());
        
        // Read the output file
        List<String> lines = Files.readAllLines(tempFile);
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify output format
        assertEquals("Tag Counts:", lines.get(0));
        assertEquals("Tag,Count", lines.get(1));
        
        // Verify tag counts (sorted by count in descending order)
        assertTrue(lines.contains("sv_p1,2"));
        assertTrue(lines.contains("sv_p2,1"));
        assertTrue(lines.contains("Untagged,1"));
        
        // Find the line for Port/Protocol Combination Counts header
        int portProtocolHeaderIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals("Port/Protocol Combination Counts:")) {
                portProtocolHeaderIndex = i;
                break;
            }
        }
        assertTrue(portProtocolHeaderIndex > 0);
        
        // Verify port/protocol counts
        assertEquals("Port,Protocol,Count", lines.get(portProtocolHeaderIndex + 1));
        assertTrue(lines.contains("443,tcp,2"));
        assertTrue(lines.contains("23,tcp,1"));
        assertTrue(lines.contains("80,tcp,1"));
    }
    
    @Test
    void testWriteEmptyStatistics() throws IOException {
        // Create empty statistics
        Statistics statistics = new Statistics();
        
        // Create temporary output file
        Path tempFile = Files.createTempFile("stats", ".csv");
        
        // Write statistics
        StatisticsWriter writer = new StatisticsWriter();
        writer.writeStatistics(statistics, tempFile.toString());
        
        // Read the output file
        List<String> lines = Files.readAllLines(tempFile);
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify output format
        assertEquals("Tag Counts:", lines.get(0));
        assertEquals("Tag,Count", lines.get(1));
        
        // Verify empty sections
        assertEquals("", lines.get(2)); // Empty line after tag counts
        assertEquals("Port/Protocol Combination Counts:", lines.get(3));
        assertEquals("Port,Protocol,Count", lines.get(4));
    }
}