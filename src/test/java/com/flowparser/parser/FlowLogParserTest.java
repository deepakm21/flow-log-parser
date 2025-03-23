package com.flowparser.parser;

import com.flowparser.model.FlowLogEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowLogParserTest {

    @Test
    void testParseFlowLogs() throws IOException {
        // Create a temporary file with test data
        String testData = "2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 49153 443 6 25 20000 1620140761 1620140821 ACCEPT OK\n" +
                "2 123456789012 eni-4d3c2b1a 192.168.1.100 203.0.113.101 49154 23 6 15 12000 1620140761 1620140821 REJECT OK\n";
        Path tempFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(tempFile, testData);

        // Parse the flow logs
        FlowLogParser parser = new FlowLogParser();
        List<FlowLogEntry> entries = new ArrayList<>();
        parser.parseFlowLogs(tempFile.toString(), entries::add);

        // Clean up
        Files.delete(tempFile);

        // Verify results
        assertEquals(2, entries.size());

        // Check first entry
        FlowLogEntry entry1 = entries.get(0);
        assertEquals(2, entry1.getVersion());
        assertEquals("123456789012", entry1.getAccountId());
        assertEquals("eni-0a1b2c3d", entry1.getInterfaceId());
        assertEquals("10.0.1.201", entry1.getSrcAddr());
        assertEquals("198.51.100.2", entry1.getDstAddr());
        assertEquals(443, entry1.getDstPort());
        assertEquals(49153, entry1.getSrcPort());
        assertEquals(6, entry1.getProtocol());
        assertEquals("tcp", entry1.getProtocolName());
        assertEquals(25, entry1.getPackets());
        assertEquals(20000, entry1.getBytes());
        assertEquals(1620140761, entry1.getStartTime());
        assertEquals(1620140821, entry1.getEndTime());
        assertEquals("ACCEPT", entry1.getAction());
        assertEquals("OK", entry1.getLogStatus());

        // Check second entry
        FlowLogEntry entry2 = entries.get(1);
        assertEquals("REJECT", entry2.getAction());
        assertEquals(23, entry2.getDstPort());
    }

    @Test
    void testParseInvalidLine() throws IOException {
        // Create a temporary file with invalid data
        String testData = "Invalid line format\n" +
                "2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 443 49153 6 25 20000 1620140761 1620140821 ACCEPT OK\n";
        Path tempFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(tempFile, testData);

        // Parse the flow logs
        FlowLogParser parser = new FlowLogParser();
        List<FlowLogEntry> entries = new ArrayList<>();
        parser.parseFlowLogs(tempFile.toString(), entries::add);

        // Clean up
        Files.delete(tempFile);

        // Verify results - should only get one valid entry
        assertEquals(1, entries.size());
    }

    @Test
    void testParseUnsupportedVersion() throws IOException {
        // Create a temporary file with unsupported version
        String testData = "3 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 443 49153 6 25 20000 1620140761 1620140821 ACCEPT OK\n";
        Path tempFile = Files.createTempFile("flowlogs", ".test");
        Files.writeString(tempFile, testData);

        // Parse the flow logs
        FlowLogParser parser = new FlowLogParser();
        List<FlowLogEntry> entries = new ArrayList<>();
        parser.parseFlowLogs(tempFile.toString(), entries::add);

        // Clean up
        Files.delete(tempFile);

        // Verify results - should get no entries due to unsupported version
        assertEquals(0, entries.size());
    }
}