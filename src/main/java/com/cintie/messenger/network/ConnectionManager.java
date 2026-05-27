package com.cintie.messenger.network;


import com.cintie.messenger.protocol.Packet;

import java.util.concurrent.ConcurrentHashMap;

// Connection manager
public class ConnectionManager {

    private final ConcurrentHashMap<String, ClientConnection> connections = new ConcurrentHashMap<>();

    public void addConnection(String peerId, ClientConnection clientConnection){
        connections.put(peerId, clientConnection);
    }

    public void removeConnection(ClientConnection clientConnection){
        connections.values().remove(clientConnection);
    }

    public ClientConnection getConnection(String peerId){
        return connections.get(peerId);
    }

    public void sendTo(String toPeerId, Packet packet) {
        ClientConnection connection = connections.get(toPeerId);
        if (connection != null) {
            try {
                connection.sendPacket(packet);
            } catch (Exception e) {
                System.err.println("Failed to send packet to " + toPeerId + ": " + e.getMessage());
            }
        } else {
            System.err.println("No connection found for peer: " + toPeerId);
        }
    }
}
