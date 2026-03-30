package com.cintie.messenger.network;

import com.cintie.messenger.service.MessageService;
import com.sun.jndi.ldap.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener implements Runnable{

    private final int port;
    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    public ServerListener(int port, ConnectionManager connectionManager, MessageService messageService){
        this.port = port;
        this.connectionManager = connectionManager;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true){
                Socket socket = serverSocket.accept();

                ClientConnection connection = new ClientConnection(socket, connectionManager, messageService);
                connectionManager.addConnection(connection);

                new Thread(connection).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
