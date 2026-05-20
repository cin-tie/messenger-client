package com.cintie.messenger.protocol;

// Validates packet integrity
public class PacketValidator {
    public static boolean validate(Packet packet) {
        if (packet == null) {
            return false;
        }

        if (packet.getVersion() <= 0) {
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

        return true;
    }
}
