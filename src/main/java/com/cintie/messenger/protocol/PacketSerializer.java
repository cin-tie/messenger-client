package com.cintie.messenger.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * Serializes and deserializes packets.
 */
public class PacketSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
    }

    public static String serialize(Packet packet) throws Exception {
        return objectMapper.writeValueAsString(packet);
    }

    public static Packet deserialize(String raw) throws Exception {
        return objectMapper.readValue(raw, Packet.class);
    }
}
