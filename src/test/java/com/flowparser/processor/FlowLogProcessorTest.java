package com.flowparser.processor;

import com.flowparser.model.Statistics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlowLogProcessorTest {

    @Test
    void testProcessFlowLogs() throws IOException {
        // Create lookup table
        Map<String, String> lookupTable = new HashMap<>();
        lookupTable.put("443,tcp", "sv_p2");
        lookupTable.put("23,tcp", "sv_p1");
        lookupTable.put("25,tcp", "sv_p1");
        
        // Create a temporary flow log file
        String flowLogData = "2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 49153 443 6 25 20000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-4d3c2b1a 192.168.1.100 203.0.113.101 49154 23 6 15 12000 1620140761 1620140821 REJECT OK\n" +
                "2 123456789012 eni-5e6f7g8h 192.168.1.101 198.51.100.3 49155 80 6 10 8000 1620140761 1620140821 ACCEPT OK\n";
        Path tempFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(tempFile, flowLogData);
        
        // Process the flow logs
        FlowLogProcessor processor = new FlowLogProcessor(lookupTable);
        Statistics statistics = processor.processFlowLogs(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify tag statistics
        Map<String, Integer> tagCounts = statistics.getTagCounts();
        assertEquals(3, tagCounts.size());
        assertEquals(1, tagCounts.get("sv_p2"));
        assertEquals(1, tagCounts.get("sv_p1"));
        assertEquals(1, tagCounts.get("Untagged"));
        
        // Verify port/protocol statistics
        Map<String, Integer> portProtocolCounts = statistics.getPortProtocolCounts();
        assertEquals(3, portProtocolCounts.size());
        assertEquals(1, portProtocolCounts.get("443,tcp"));
        assertEquals(1, portProtocolCounts.get("23,tcp"));
        assertEquals(1, portProtocolCounts.get("80,tcp"));
    }
    
    @Test
    void testProcessEmptyFlowLogs() throws IOException {
        // Create lookup table
        Map<String, String> lookupTable = new HashMap<>();
        lookupTable.put("443,tcp", "sv_p2");
        
        // Create an empty flow log file
        Path tempFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(tempFile, "");
        
        // Process the flow logs
        FlowLogProcessor processor = new FlowLogProcessor(lookupTable);
        Statistics statistics = processor.processFlowLogs(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify empty statistics
        assertTrue(statistics.getTagCounts().isEmpty());
        assertTrue(statistics.getPortProtocolCounts().isEmpty());
    }
}