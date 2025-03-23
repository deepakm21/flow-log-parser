package com.flowparser.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Utility class to generate sample data for testing.
 * Note: This is not part of the main application, just a helper for testing.
 */
public class SampleDataGenerator {
    private static final Random random = new Random();
    
    /**
     * Generates a sample flow log file with the specified number of entries.
     * 
     * @param filePath Path to save the flow log file
     * @param numEntries Number of entries to generate
     * @throws IOException If there's an error writing to the file
     */
    public static void generateFlowLogFile(String filePath, int numEntries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < numEntries; i++) {
                writer.write(generateFlowLogEntry());
                writer.newLine();
            }
        }
    }
    
    /**
     * Generates a sample lookup table file.
     * 
     * @param filePath Path to save the lookup table file
     * @throws IOException If there's an error writing to the file
     */
    public static void generateLookupTableFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("dstport,protocol,tag");
            writer.newLine();
            
            // Common ports and their tags
            writer.write("25,tcp,sv_P1");
            writer.newLine();
            writer.write("68,udp,sv_P2");
            writer.newLine();
            writer.write("23,tcp,sv_P1");
            writer.newLine();
            writer.write("31,udp,SV_P3");
            writer.newLine();
            writer.write("443,tcp,sv_P2");
            writer.newLine();
            writer.write("22,tcp,sv_P4");
            writer.newLine();
            writer.write("3389,tcp,sv_P5");
            writer.newLine();
            writer.write("0,icmp,sv_P5");
            writer.newLine();
            writer.write("110,tcp,email");
            writer.newLine();
            writer.write("993,tcp,email");
            writer.newLine();
            writer.write("143,tcp,email");
            writer.newLine();
            
            // Add some more random ports for testing
            for (int i = 0; i < 50; i++) {
                int port = 1024 + random.nextInt(64000);
                String protocol = (random.nextBoolean()) ? "tcp" : "udp";
                String tag = "sv_R" + (1 + random.nextInt(10));
                
                writer.write(port + "," + protocol + "," + tag);
                writer.newLine();
            }
        }
    }
    
    /**
     * Generates a random flow log entry.
     * 
     * @return A string representing a flow log entry
     */
    private static String generateFlowLogEntry() {
        int version = 2;
        String accountId = "123456789012";
        String interfaceId = "eni-" + generateRandomHex(8);
        String srcAddr = generateRandomIpAddr();
        String dstAddr = generateRandomIpAddr();
        int srcPort = 1024 + random.nextInt(64000);
        int dstPort = getRandomCommonPort();
        int protocol = getRandomProtocol();
        int packets = 1 + random.nextInt(100);
        int bytes = 1000 + random.nextInt(50000);
        long startTime = 1620140661 + random.nextInt(1000);
        long endTime = startTime + 60 + random.nextInt(3600);
        String action = (random.nextBoolean()) ? "ACCEPT" : "REJECT";
        String logStatus = "OK";
        
        return version + " " + accountId + " " + interfaceId + " " + 
               srcAddr + " " + dstAddr + " " + 
               srcPort + " " + dstPort + " " + 
               protocol + " " + packets + " " + bytes + " " + 
               startTime + " " + endTime + " " + 
               action + " " + logStatus;
    }
    
    /**
     * Generates a random hexadecimal string of the specified length.
     * 
     * @param length Length of the hexadecimal string
     * @return Random hexadecimal string
     */
    private static String generateRandomHex(int length) {
        StringBuilder sb = new StringBuilder();
        String chars = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generates a random IP address.
     * 
     * @return Random IP address as a string
     */
    private static String generateRandomIpAddr() {
        if (random.nextBoolean()) {
            // Private IP
            switch (random.nextInt(3)) {
                case 0: return "10." + random.nextInt(256) + "." + random.nextInt(256) + "." + (1 + random.nextInt(254));
                case 1: return "172." + (16 + random.nextInt(16)) + "." + random.nextInt(256) + "." + (1 + random.nextInt(254));
                default: return "192.168." + random.nextInt(256) + "." + (1 + random.nextInt(254));
            }
        } else {
            // Public IP (simplified)
            return random.nextInt(223) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + (1 + random.nextInt(254));
        }
    }
    
    /**
     * Returns a random commonly used port.
     * 
     * @return Random common port number
     */
    private static int getRandomCommonPort() {
        int[] commonPorts = {22, 23, 25, 53, 80, 110, 143, 443, 993, 3389};
        
        // 70% chance of common port, 30% chance of random port
        if (random.nextInt(10) < 7) {
            return commonPorts[random.nextInt(commonPorts.length)];
        } else {
            return 1024 + random.nextInt(64000);
        }
    }
    
    /**
     * Returns a random protocol number.
     * 
     * @return Random protocol number
     */
    private static int getRandomProtocol() {
        int[] protocols = {6, 17, 1}; // TCP, UDP, ICMP
        int[] weights = {80, 15, 5}; // 80% TCP, 15% UDP, 5% ICMP
        
        int randomValue = random.nextInt(100);
        int cumulativeWeight = 0;
        
        for (int i = 0; i < weights.length; i++) {
            cumulativeWeight += weights[i];
            if (randomValue < cumulativeWeight) {
                return protocols[i];
            }
        }
        
        return 6; // Default to TCP
    }
    
    /**
     * Main method to generate sample files.
     */
    public static void main(String[] args) {
        try {
            generateFlowLogFile("sample_flow_logs.txt", 1000);
            generateLookupTableFile("sample_lookup_table.csv");
            System.out.println("Sample files generated successfully.");
        } catch (IOException e) {
            System.err.println("Error generating sample files: " + e.getMessage());
        }
    }
}