package com.cintie.messenger.crypto;

import com.cintie.messenger.protocol.Packet;
import com.fasterxml.jackson.databind.ObjectMapper;

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


}
