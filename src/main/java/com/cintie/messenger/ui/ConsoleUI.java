package com.cintie.messenger.ui;

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
        messageService.setUsername(scanner.nextLine());

        while (true){
            String input = scanner.nextLine();

            if(input.startsWith("/connect")){
                String[] parts = input.split(" ");
                connect(parts[1], Integer.parseInt(parts[2]));
            } else if(input.equals("/exit")){
                System.exit(0);
            }
            else{
                messageService.sendMessage(input);
            }
        }
    }

    private void connect(String host, int port){
        try {
            Socket socket = new Socket(host, port);

            ClientConnection connection = new ClientConnection(socket, connectionManager, messageService);
            connectionManager.addConnection(connection);

            new Thread(connection).start();

            System.out.println("Connected to " + host + ":" + port);
        } catch (IOException e){
            System.out.println("Connection failed");
        }
    }
}
