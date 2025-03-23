package com.flowparser.model;

/**
 * Represents a single entry in the flow log.
 * Based on AWS VPC flow log format version 2.
 */
public class FlowLogEntry {
    private final int version;
    private final String accountId;
    private final String interfaceId;
    private final String srcAddr;
    private final String dstAddr;
    private final int srcPort;
    private final int dstPort;
    private final int protocol;
    private final int packets;
    private final int bytes;
    private final long startTime;
    private final long endTime;
    private final String action;
    private final String logStatus;
    
    // Tag assigned to this flow log entry (if matched)
    private String tag;

    public FlowLogEntry(int version, String accountId, String interfaceId, String srcAddr, String dstAddr, 
                       int srcPort, int dstPort, int protocol, int packets, int bytes, 
                       long startTime, long endTime, String action, String logStatus) {
        this.version = version;
        this.accountId = accountId;
        this.interfaceId = interfaceId;
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
        this.packets = packets;
        this.bytes = bytes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.action = action;
        this.logStatus = logStatus;
        this.tag = null; // Initially untagged
    }

    public int getVersion() {
        return version;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public String getSrcAddr() {
        return srcAddr;
    }

    public String getDstAddr() {
        return dstAddr;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public int getProtocol() {
        return protocol;
    }
    
    /**
     * Returns the protocol name based on the protocol number.
     * Only common protocols used in the assignment.
     */
    public String getProtocolName() {
        switch (protocol) {
            case 6: return "tcp";
            case 17: return "udp";
            case 1: return "icmp";
            default: return String.valueOf(protocol);
        }
    }

    public int getPackets() {
        return packets;
    }

    public int getBytes() {
        return bytes;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getAction() {
        return action;
    }

    public String getLogStatus() {
        return logStatus;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    /**
     * Creates a key for lookup table matching in the format "dstport,protocol".
     */
    public String getLookupKey() {
        return dstPort + "," + getProtocolName();
    }

    @Override
    public String toString() {
        return "FlowLogEntry{" +
                "version=" + version +
                ", accountId='" + accountId + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", srcAddr='" + srcAddr + '\'' +
                ", dstAddr='" + dstAddr + '\'' +
                ", srcPort=" + srcPort +
                ", dstPort=" + dstPort +
                ", protocol=" + protocol +
                ", tag='" + tag + '\'' +
                '}';
    }
}