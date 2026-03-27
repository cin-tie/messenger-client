package com.cintie.messenger.network;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private final List<ClientConnection> connections = new ArrayList<>();

    public void addConnection(ClientConnection connection){
        connections.add(connection);
        System.out.println("New connection: " + connection.getRemoteAddress());
    }

    public void removeConnection(ClientConnection connection) {
        connections.remove(connection);
        System.out.println("Connection closed: " + connection.getRemoteAddress());
    }

    public void broadcast(String message) {
        for (ClientConnection connection : connections) {
            connection.send(message);
        }
    }

    public List<ClientConnection> getConnections() {
        return connections;
    }
}
