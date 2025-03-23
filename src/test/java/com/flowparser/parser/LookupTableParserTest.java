package com.flowparser.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LookupTableParserTest {
    
    @Test
    void testParseLookupTable() throws IOException {
        // Create a temporary file with test data
        String testData = "dstport,protocol,tag\n" +
                          "25,tcp,sv_P1\n" +
                          "68,udp,sv_P2\n" +
                          "443,tcp,sv_P2\n";
        Path tempFile = Files.createTempFile("lookup", ".csv");
        Files.writeString(tempFile, testData);
        
        // Parse the lookup table
        LookupTableParser parser = new LookupTableParser();
        Map<String, String> lookupTable = parser.parseLookupTable(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify results
        assertEquals(3, lookupTable.size());
        assertEquals("sv_p1", lookupTable.get("25,tcp"));
        assertEquals("sv_p2", lookupTable.get("68,udp"));
        assertEquals("sv_p2", lookupTable.get("443,tcp"));
    }

    @Test
    void testParseLookupTableWithMissingHeader() throws IOException {
        // Create a temporary file with missing header
        String testData = "25,tcp,sv_P1\n" +
                          "68,udp,sv_P2\n";
        Path tempFile = Files.createTempFile("lookup", ".csv");
        Files.writeString(tempFile, testData);
        
        // Parse the lookup table
        LookupTableParser parser = new LookupTableParser();
        Map<String, String> lookupTable = parser.parseLookupTable(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify results - First line is treated as header and skipped
        assertEquals(1, lookupTable.size());
        assertEquals("sv_p2", lookupTable.get("68,udp"));
    }
    
    @Test
    void testParseLookupTableWithInvalidEntries() throws IOException {
        // Create a temporary file with some invalid entries
        String testData = "dstport,protocol,tag\n" +
                          "25,tcp,sv_P1\n" +
                          "invalid line\n" +
                          "443,tcp,sv_P2\n";
        Path tempFile = Files.createTempFile("lookup", ".csv");
        Files.writeString(tempFile, testData);
        
        // Parse the lookup table
        LookupTableParser parser = new LookupTableParser();
        Map<String, String> lookupTable = parser.parseLookupTable(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify results - Invalid line should be ignored
        assertEquals(2, lookupTable.size());
        assertEquals("sv_p1", lookupTable.get("25,tcp"));
        assertEquals("sv_p2", lookupTable.get("443,tcp"));
    }
    
    @Test
    void testCaseInsensitiveMatching() throws IOException {
        // Create a temporary file with mixed case
        String testData = "dstport,protocol,tag\n" +
                          "25,TCP,sv_P1\n" +
                          "443,tcp,SV_P2\n";
        Path tempFile = Files.createTempFile("lookup", ".csv");
        Files.writeString(tempFile, testData);
        
        // Parse the lookup table
        LookupTableParser parser = new LookupTableParser();
        Map<String, String> lookupTable = parser.parseLookupTable(tempFile.toString());
        
        // Clean up
        Files.delete(tempFile);
        
        // Verify results - Case insensitive
        assertEquals(2, lookupTable.size());
        assertEquals("sv_p1", lookupTable.get("25,tcp"));
        assertEquals("sv_p2", lookupTable.get("443,tcp"));
    }
}