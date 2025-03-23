package com.flowparser.processor;

import com.flowparser.model.FlowLogEntry;
import com.flowparser.model.Statistics;
import com.flowparser.parser.FlowLogParser;

import java.io.IOException;
import java.util.Map;

/**
 * Processor that combines parsing flow logs with matching them against the lookup table.
 */
public class FlowLogProcessor {
    private final Map<String, String> lookupTable;
    private final FlowLogParser parser;
    
    /**
     * Creates a new FlowLogProcessor with the given lookup table.
     * 
     * @param lookupTable Map of lookup keys to tags
     */
    public FlowLogProcessor(Map<String, String> lookupTable) {
        this.lookupTable = lookupTable;
        this.parser = new FlowLogParser();
    }
    
    /**
     * Processes the flow log file and returns statistics.
     * 
     * @param flowLogFile Path to the flow log file
     * @return Statistics object with counts
     * @throws IOException If there's an error reading the file
     */
    public Statistics processFlowLogs(String flowLogFile) throws IOException {
        Statistics statistics = new Statistics();
        
        parser.parseFlowLogs(flowLogFile, entry -> {
            // Match entry against lookup table
            String tag = matchEntryToTag(entry);
            entry.setTag(tag);
            
            // Add to statistics
            statistics.addTag(tag);
            statistics.addPortProtocol(entry.getDstPort(), entry.getProtocolName());
        });
        
        return statistics;
    }
    
    /**
     * Attempts to match a flow log entry to a tag from the lookup table.
     * 
     * @param entry The flow log entry to match
     * @return The matching tag, or null if no match is found
     */
    private String matchEntryToTag(FlowLogEntry entry) {
        String lookupKey = entry.getLookupKey();
        return lookupTable.get(lookupKey);
    }
}