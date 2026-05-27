package com.cintie.messenger.ui;

import com.cintie.messenger.crypto.PeerIdGenerator;
import com.cintie.messenger.network.ClientConnection;
import com.cintie.messenger.network.ConnectionManager;
import com.cintie.messenger.services.MessageService;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

// Console  ui class
public class ConsoleUI {

    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    private ClientConnection currentConnection;
    private String currentPeerId;

    public ConsoleUI(ConnectionManager connectionManager, MessageService messageService){
        this.connectionManager = connectionManager;
        this.messageService = messageService;
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        currentPeerId = PeerIdGenerator.generate();

        System.out.println("Your peerId: " + currentPeerId);
        System.out.println("Commands:");
        System.out.println("  /connect <host> <port> - Connect to relay server");
        System.out.println("  /message <peerId> <message> - Send message to peer");
        System.out.println("  /exit - Exit application");
        System.out.println();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                messageService.checkPendingMessages();
            }
        }, 5000, 5000);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.startsWith("/connect")) {
                String[] parts = input.split(" ");
                if (parts.length >= 3) {
                    connect(parts[1], Integer.parseInt(parts[2]), username);
                } else {
                    System.out.println("Usage: /connect <host> <port>");
                }

            } else if (input.startsWith("/message")) {
                String[] parts = input.split(" ", 3);
                if (parts.length >= 3) {
                    if (currentConnection == null) {
                        System.out.println("Not connected to relay server. Use /connect first");
                    } else {
                        messageService.sendMessage(parts[1], parts[2]);
                    }
                } else {
                    System.out.println("Usage: /message <peerId> <message>");
                }

            } else if (input.equals("/exit")) {
                System.out.println("Exiting...");
                System.exit(0);

            } else if (!input.isEmpty()) {
                System.out.println("Unknown command. Available: /connect, /message, /exit");
            }
        }
    }

    private void connect(String host, int port, String username){
        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected to relay server at " + host + ":" + port);

            currentConnection = new ClientConnection(socket, connectionManager, messageService, currentPeerId);
            messageService.init(username, currentPeerId, currentConnection);

            new Thread(currentConnection).start();

            System.out.println("Waiting for registration confirmation...");

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}