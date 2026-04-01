package com.cintie.messenger.service;

import com.cintie.messenger.message.Message;
import com.cintie.messenger.network.ConnectionManager;

public class MessageService {

    private final ConnectionManager connectionManager;

    private String username;
    private String peerId;

    public MessageService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void init(String username, String peerId) {
        this.username = username;
        this.peerId = peerId;
    }

    public void sendMessage(String toPeerId, String content) {
        String packet = peerId + "|" + toPeerId + "|" + content;
        connectionManager.sendTo(toPeerId, packet);
    }

    public void handleIncoming(String raw) {
        String[] parts = raw.split("\\|", 2);

        String from = parts[0];
        String message = parts[1];

        System.out.println("[" + from + "]: " + message);
    }
}