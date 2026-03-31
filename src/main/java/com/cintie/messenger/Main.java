package com.cintie.messenger;

import com.cintie.messenger.network.ConnectionManager;
import com.cintie.messenger.network.ServerListener;
import com.cintie.messenger.service.MessageService;
import com.cintie.messenger.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        int port = 5000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 5000");
            }
        }

        ConnectionManager connectionManager = new ConnectionManager();
        MessageService messageService = new MessageService(connectionManager);

        new Thread(new ServerListener(port, connectionManager, messageService)).start();

        ConsoleUI ui = new ConsoleUI(connectionManager, messageService);
        ui.start();
    }
}
