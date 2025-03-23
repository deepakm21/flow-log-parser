package com.flowparser.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for the lookup table CSV file.
 * The lookup table format is: dstport,protocol,tag
 */
public class LookupTableParser {

    /**
     * Parses the lookup table file and returns a map of key to tag.
     * The key is in the format "dstport,protocol" (lowercase).
     * 
     * @param filePath Path to the lookup table CSV file
     * @return Map of lookup keys to tags
     * @throws IOException If there's an error reading the file
     */
    public Map<String, String> parseLookupTable(String filePath) throws IOException {
        Map<String, String> lookupTable = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                // Skip header line
                if (isHeader) {
                    isHeader = false;
                    
                    // Validate header
                    String[] headerParts = line.toLowerCase().split(",");
                    if (headerParts.length < 3 || 
                        !headerParts[0].equals("dstport") || 
                        !headerParts[1].equals("protocol") || 
                        !headerParts[2].equals("tag")) {
                        System.err.println("Warning: Unexpected header format in lookup table, expected: dstport,protocol,tag");
                    }
                    
                    continue;
                }
                
                try {
                    String[] parts = line.toLowerCase().split(",");
                    if (parts.length < 3) {
                        System.err.println("Warning: Invalid lookup table entry on line " + lineNumber + ": insufficient fields");
                        continue;
                    }
                    
                    String dstPort = parts[0].trim();
                    String protocol = parts[1].trim();
                    String tag = parts[2].trim();
                    
                    // Create lookup key in the format "dstport,protocol"
                    String lookupKey = dstPort + "," + protocol;
                    
                    // Add to lookup table
                    lookupTable.put(lookupKey, tag);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to parse lookup table entry on line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        
        return lookupTable;
    }
}