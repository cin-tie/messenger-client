package com.cintie.messenger.network;

import com.cintie.messenger.protocol.*;
import com.cintie.messenger.services.MessageService;

import java.io.*;
import java.net.Socket;

// Client thread
public class ClientConnection implements Runnable{

    private final Socket socket;
    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    private BufferedReader reader;
    private BufferedWriter writer;

    private final String myPeerId;
    private boolean isRegistered = false;

    // Constructor
    public ClientConnection(Socket socket, ConnectionManager connectionManager, MessageService messageService, String myPeerId) {
        this.socket = socket;
        this.connectionManager = connectionManager;
        this.messageService = messageService;
        this.myPeerId = myPeerId;

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendHello();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send hello to register
    private void sendHello(){
        try {
            Packet helloPacket = PacketBuilder.hello(myPeerId);
            sendPacket(helloPacket);
            System.out.println("Sent HELLO packet to relay server");
        } catch (Exception e) {
            System.err.println("Failed to send HELLO packet: " + e.getMessage());
        }
    }

    // Send message
    public void sendMessage(String toPeerId, String content){
        try {
            Packet messagePacket = PacketBuilder.message(myPeerId, toPeerId, content);
            sendPacket(messagePacket);
            System.out.println("Sent MESSAGE to " + toPeerId);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    // Send all packets
    public void sendPacket(Packet packet) throws Exception{
        String json = PacketSerializer.serialize(packet);
        writer.write(json);
        writer.newLine();
        writer.flush();
    }

    // Run thread
    @Override
    public void run() {
        try{
            String line;
            while ((line = reader.readLine()) != null){
                try {
                    // Packet from server
                    Packet packet = PacketSerializer.deserialize(line);

                    if(PacketValidator.validate(packet)){
                        handlePacket(packet);
                    }
                    else {
                        System.err.println("Received invalid packet from server");
                    }
                } catch (Exception e){
                    System.err.println("Error deserializing packet: " + e.getMessage());
                }
            }
        } catch (IOException e){
            System.err.println("Connection lost: " + e.getMessage());
        } finally {
            connectionManager.removeConnection(this);
            try {
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    // Handle packet based on type
    private void handlePacket(Packet packet){
        switch (packet.getPacketType()){
            case HELLO:
                if(!isRegistered && myPeerId.equals(packet.getRecipientId())){
                    isRegistered = true;
                    System.out.println("Successfully registered with relay server");
                }
                break;

            case MESSAGE:
                if(packet.getPayload() instanceof MessagePayload){
                    MessagePayload messagePayload = (MessagePayload)  packet.getPayload();
                    messageService.handleIncomingMessage(
                            packet.getSenderId(),
                            messagePayload.getContent(),
                            packet.getPacketId()
                    );
                }
                break;

            case ACK:
                System.out.println("Message delivered to " + packet.getSenderId() + " (Packet ID: " + packet.getPacketId() + ")");
                messageService.handleAck(packet.getPacketId());
                break;

            case KEY_EXCHANGE:
                if(packet.getPayload() instanceof KeyExchangePayload){
                    KeyExchangePayload keyExchangePayload = (KeyExchangePayload)  packet.getPayload();
                    messageService.handleKeyExchange(
                            packet.getSenderId(),
                            keyExchangePayload.getPublicKey()
                    );
                }
                break;

            case PING:
                try {
                    sendPacket(PacketBuilder.ack(myPeerId, packet.getSenderId()));
                } catch (Exception e) {
                    System.err.println("Failed to send PONG: " + e.getMessage());
                }
                break;

            case ERROR:
                if (packet.getPayload() instanceof MessagePayload) {
                    MessagePayload payload = (MessagePayload) packet.getPayload();
                    System.err.println("Server error: " + payload.getContent());
                }
                break;

            case ROUTE_BUILD:
            case ROUTE_FORWARD:
                System.out.println("Received route packet type: " + packet.getPacketType());
                break;

            default:
                System.out.println("Unhandled packet type: " + packet.getPacketType());
        }
    }
}
