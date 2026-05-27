package com.cintie.messenger.services;

import com.cintie.messenger.network.ClientConnection;
import com.cintie.messenger.network.ConnectionManager;
import com.cintie.messenger.protocol.PacketBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageService {

    private final ConnectionManager connectionManager;
    private final Map<String, Long> pendingMessages = new ConcurrentHashMap<>();

    private String username;
    private String peerId;
    private ClientConnection currentConnection;

    public MessageService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void init(String username, String peerId, ClientConnection connection) {
        this.username = username;
        this.peerId = peerId;
        this.currentConnection = connection;
        connectionManager.addConnection(peerId, connection);
    }

    public void sendMessage(String toPeerId, String content) {
        if (currentConnection == null) {
            System.err.println("Not connected to relay server");
            return;
        }

        String messageKey = peerId + ":" + toPeerId + ":" + System.currentTimeMillis();
        pendingMessages.put(messageKey, System.currentTimeMillis());

        currentConnection.sendMessage(toPeerId, content);
        displayMessage(username, content);
    }

    public void handleIncomingMessage(String fromPeerId, String content, String packetId) {
        displayMessage(fromPeerId, content);

        if (currentConnection != null) {
            try {
                currentConnection.sendPacket(PacketBuilder.ack(peerId, fromPeerId));
            } catch (Exception e) {
                System.err.println("Failed to send ACK: " + e.getMessage());
            }
        }
    }

    public void handleAck(String packetId) {
        String keyToRemove = null;
        for (String key : pendingMessages.keySet()) {
            if (key.contains(packetId)) {
                keyToRemove = key;
                break;
            }
        }

        if (keyToRemove != null) {
            pendingMessages.remove(keyToRemove);
            System.out.println("Message acknowledged by server");
        }
    }

    public void handleKeyExchange(String fromPeerId, String publicKey) {
        System.out.println("Key exchange initiated with " + fromPeerId);
    }

    private void displayMessage(String sender, String content) {
        System.out.println("\n[" + sender + "]: " + content);
        System.out.print("> ");
    }

    public void checkPendingMessages() {
        long now = System.currentTimeMillis();
        pendingMessages.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > 30000) {
                return true;
            }
            return false;
        });
    }
}