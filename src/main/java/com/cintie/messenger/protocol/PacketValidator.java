package com.cintie.messenger.protocol;

// Validates packet integrity
public class PacketValidator {
    public static boolean validate(Packet packet) {
        if (packet == null) {
            return false;
        }

        if (packet.getVersion() != 1) {
            return false;
        }

        if (packet.getPacketId() == null || packet.getPacketId().isEmpty()) {
            return false;
        }

        if (packet.getSenderId() == null || packet.getSenderId().isEmpty()) {
            return false;
        }

        if (packet.getPacketType() == null) {
            return false;
        }

        if (packet.getTimestamp() <= 0) {
            return false;
        }

        // Validate based on packet type
        switch (packet.getPacketType()) {
            case MESSAGE:
                if (packet.getRecipientId() == null || packet.getRecipientId().isEmpty()) {
                    return false;
                }
                if (!(packet.getPayload() instanceof MessagePayload)) {
                    return false;
                }
                break;

            case KEY_EXCHANGE:
                if (packet.getRecipientId() == null || packet.getRecipientId().isEmpty()) {
                    return false;
                }
                if (!(packet.getPayload() instanceof KeyExchangePayload)) {
                    return false;
                }
                break;

            case ROUTE_BUILD:
                if (packet.getRecipientId() == null || packet.getRecipientId().isEmpty()) {
                    return false;
                }
                if (!(packet.getPayload() instanceof RoutePayload)) {
                    return false;
                }
                break;

            case HELLO:
                // HELLO should not have recipient
                if (packet.getRecipientId() != null) {
                    return false;
                }
                break;

            case PING:
                // PING should not have recipient
                if (packet.getRecipientId() != null) {
                    return false;
                }
                break;

            case ACK:
                if (packet.getRecipientId() == null || packet.getRecipientId().isEmpty()) {
                    return false;
                }
                break;
        }

        return true;
    }
}