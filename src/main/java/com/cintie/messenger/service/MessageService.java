package com.cintie.messenger.service;

import com.cintie.messenger.message.Message;
import com.cintie.messenger.network.ConnectionManager;

public class MessageService {
    private final ConnectionManager connectionManager;
    private String username = "user";

    public MessageService(ConnectionManager connectionManager){
        this.connectionManager = connectionManager;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public  void sendMessage(String content){
        Message message = new Message(username, content);
        connectionManager.broadcast(serialize(message));
    }

    public void handleIncoming(String raw) {
        Message message = deserialize(raw);
        System.out.println(message);
    }

    private String serialize(Message message) {
        return message.getSender() + "|" +
                message.getTimestamp() + "|" +
                message.getContent();
    }

    private Message deserialize(String raw) {
        String[] parts = raw.split("\\|", 3);
        return new Message(parts[0], parts[2]);
    }
}
