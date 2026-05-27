package com.cintie.messenger.protocol;

// Universal protocol packet
public class Packet {

    private int version;                // Protocol version
    private PacketType packetType;      // Packet category

    private String packetId;            // Unique packet id

    private String senderId;            // Sender peer id
    private String recipientId;         // Target peer id

    private long timestamp;             // Creation time
    private long nonce;                 // Anti-replay nonce

    private Payload payload;            // Actual packet data
    private byte[] signature;           // Digital signature

    public Packet(){}

    public int getVersion(){
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}