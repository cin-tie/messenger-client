package com.cintie.messenger.protocol;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

// Creates protocol packets safely.
public class PacketBuilder {

    private static final SecureRandom secureRandom = new SecureRandom();

    // Private method for creating packets
    private static Packet base(
            PacketType packetType,
            String senderId,
            String recipientId,
            Payload payload
    ) {
        Packet packet = new Packet();

        packet.setVersion(1);
        packet.setPacketType(packetType);

        packet.setPacketId(UUID.randomUUID().toString());

        packet.setSenderId(senderId);
        packet.setRecipientId(recipientId);

        packet.setTimestamp(System.currentTimeMillis());
        packet.setNonce(secureRandom.nextLong());

        packet.setPayload(payload);

        return packet;
    }

    // Create hello message
    public static Packet hello(String senderId) {
        return base(
                PacketType.HELLO,
                senderId,
                null,
                null
        );
    }

    // Create message
    public static Packet message(String senderId, String recipientId, String content){
        return base(
                PacketType.MESSAGE,
                senderId,
                recipientId,
                new MessagePayload(content)
        );
    }

    // Create key exchange
    public static Packet keyExchange(String senderId, String recipientId, String publicKey){
        return base(
                PacketType.KEY_EXCHANGE,
                senderId,
                recipientId,
                new KeyExchangePayload(publicKey)
        );
    }

    // Create build road
    public static Packet routeBuild(
            String senderId,
            String recipientId,
            List<String> hops
    ) {
        return base(
                PacketType.ROUTE_BUILD,
                senderId,
                recipientId,
                new RoutePayload(hops)
        );
    }

    // Create ack
    public static Packet ack(
            String senderId,
            String recipientId
    ) {
        return base(
                PacketType.ACK,
                senderId,
                recipientId,
                null
        );
    }

    // Create ping
    public static Packet ping(String senderId){
        return base(
                PacketType.PING,
                senderId,
                null,
                null
        );
    }
}
