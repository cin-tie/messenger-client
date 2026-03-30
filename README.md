# Messenger Client

A simple Java-based messaging client with server functionality.

## Features
- Peer-to-peer messaging via TCP sockets
- Multi-client support with connection management
- Console-based user interface
- Message broadcasting to all connected clients

## Requirements
- Java 21
- Maven

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/messenger-client-1.0.0.jar
```

## Usage
1. Enter your username when prompted
2. Connect to another instance: `/connect <host> <port>`
3. Type any message to broadcast
4. Exit: `/exit`

## Architecture
- `ConnectionManager` - Manages active client connections
- `ClientConnection` - Handles individual client communication
- `ServerListener` - Accepts incoming connections on port 5000
- `MessageService` - Serializes/deserializes messages
- `ConsoleUI` - Command-line interface