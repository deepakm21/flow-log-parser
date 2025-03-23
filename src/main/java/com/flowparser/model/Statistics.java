package com.flowparser.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store statistics about flow log data.
 */
public class Statistics {
    // Count of tags
    private final Map<String, Integer> tagCounts;
    
    // Count of port/protocol combinations
    private final Map<String, Integer> portProtocolCounts;
    
    public Statistics() {
        this.tagCounts = new HashMap<>();
        this.portProtocolCounts = new HashMap<>();
    }
    
    /**
     * Adds a tag to the statistics.
     * 
     * @param tag The tag to add (or "Untagged" if null)
     */
    public void addTag(String tag) {
        String tagName = (tag == null) ? "Untagged" : tag;
        tagCounts.put(tagName, tagCounts.getOrDefault(tagName, 0) + 1);
    }
    
    /**
     * Adds a port/protocol combination to the statistics.
     * 
     * @param port The destination port
     * @param protocol The protocol (tcp, udp, icmp, etc.)
     */
    public void addPortProtocol(int port, String protocol) {
        String key = port + "," + protocol;
        portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);
    }
    
    /**
     * Gets the map of tag counts.
     * 
     * @return Map of tag names to count
     */
    public Map<String, Integer> getTagCounts() {
        return tagCounts;
    }
    
    /**
     * Gets the map of port/protocol combination counts.
     * 
     * @return Map of port,protocol to count
     */
    public Map<String, Integer> getPortProtocolCounts() {
        return portProtocolCounts;
    }
}