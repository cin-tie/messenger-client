package com.cintie.messenger.crypto;

import com.cintie.messenger.protocol.Packet;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class SignatureService {

    private final IdentityManager identityManager;
    private final ObjectMapper objectMapper;

    public SignatureService(IdentityManager identityManager){
        this.identityManager = identityManager;
        this.objectMapper = new ObjectMapper();
    }

    // Signing packet
    public byte[] signPacket(Packet packet) throws Exception{
        // Copy without signature
        Packet packetToSign = copyWithoutSignature(packet);

        // Serialize to bytes
        byte[] dataToSign = objectMapper.writeValueAsBytes(packetToSign);

        // Sign
        return identityManager.sign(dataToSign);
    }

    // Verify signature
    public boolean verifyPacketSignature(Packet packet, PublicKey senderPublicKey) throws Exception{
        byte[] signature = packet.getSignature();
        if(signature == null || signature.length == 0){
            return false;
        }

        // Copy without signature
        Packet packetToVerify = copyWithoutSignature(packet);

        // Serialize
        byte[] dataToVerify = objectMapper.writeValueAsBytes(packetToVerify);

        // Verify
        return identityManager.verify(dataToVerify, signature, senderPublicKey);
    }

    // Check peerId and publicKey
    public boolean verifyPeerId(String peerId, PublicKey publicKey) throws Exception{
        byte[] rawPublicKey = publicKey.getEncoded();
        byte[] hash = computeSha256(rawPublicKey);
        String computedPeerId = bytesToHex(hash);
        return computedPeerId.equals(peerId);
    }

    // Get SHA-256 hash
    private byte[] computeSha256(byte[] data){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Convert bytes to hex
    private String bytesToHex(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for(byte b: bytes){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    private Packet copyWithoutSignature(Packet original){
        Packet copy = new Packet();
        copy.setVersion(original.getVersion());
        copy.setPacketType(original.getPacketType());
        copy.setPacketId(original.getPacketId());
        copy.setSenderId(original.getSenderId());
        copy.setRecipientId(original.getRecipientId());
        copy.setTimestamp(original.getTimestamp());
        copy.setNonce(original.getNonce());
        copy.setPayload(original.getPayload());
        // signature null
        return copy;
    }
}
