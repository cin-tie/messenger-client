package com.cintie.messenger.ui;

import com.cintie.messenger.crypto.PeerIdGenerator;
import com.cintie.messenger.network.ClientConnection;
import com.cintie.messenger.network.ConnectionManager;
import com.cintie.messenger.service.MessageService;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleUI {

    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    public ConsoleUI(ConnectionManager connectionManager, MessageService messageService){
        this.connectionManager = connectionManager;
        this.messageService = messageService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        String peerId = PeerIdGenerator.generate();

        messageService.init(username, peerId);

        System.out.println("Your peerId: " + peerId);

        while (true) {
            String input = scanner.nextLine();

            if (input.startsWith("/connect")) {
                String[] parts = input.split(" ");
                connect(parts[1], Integer.parseInt(parts[2]), peerId);

            } else if (input.startsWith("/msg")) {
                String[] parts = input.split(" ", 3);
                messageService.sendMessage(parts[1], parts[2]);

            } else if (input.equals("/exit")) {
                System.exit(0);
            }
        }
    }

    private void connect(String host, int port, String peerId) {
        try {
            Socket socket = new Socket(host, port);

            ClientConnection connection =
                    new ClientConnection(socket, connectionManager, messageService, peerId);

            connectionManager.addConnection(connection);
            new Thread(connection).start();

            System.out.println("Connected to relay");

        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }
}
