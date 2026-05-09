package com.cintie.messenger.network;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private final List<ClientConnection> connections = new ArrayList<>();

    public void addConnection(ClientConnection connection){
        connections.add(connection);
    }

    public void removeConnection(ClientConnection connection) {
        connections.remove(connection);
    }

    public void sendTo(String toPeerId, String message) {
        for (ClientConnection connection : connections) {
            connection.send(message);
        }
    }
}
