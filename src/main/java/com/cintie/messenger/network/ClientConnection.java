package com.cintie.messenger.network;

import com.cintie.messenger.service.MessageService;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable{

    private final Socket socket;
    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientConnection(Socket socket, ConnectionManager connectionManager, MessageService messageService){
        this.socket = socket;
        this.connectionManager = connectionManager;
        this.messageService = messageService;

        try{
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void send(String message){
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getRemoteAddress(){
        return socket.getRemoteSocketAddress().toString();
    }

    @Override
    public void run() {
        try{
            String line;

            while ((line = reader.readLine()) != null){
                messageService.handleIncoming(line);
            }
        } catch (IOException e){
        } finally {
            connectionManager.removeConnection(this);
            try {
                socket.close();
            } catch (IOException e){

            }
        }
    }
}
