package com.cintie.messenger.protocol;

public enum PacketType {
    HELLO,              // Initial handshake
    KEY_EXCHANGE,       // Public key exchange
    MESSAGE,            // Encrypted message
    ACK,                // Delivery confirmation
    ROUTE_BUILD,        // Create multi-hop route
    ROUTE_FORWARD,      // Forward through route
    PING,               // Keep connection alive
    ERROR               // Protocol error
}
