package com.cintie.messenger.message;

import java.time.LocalDateTime;

public class Message {
    private String sender;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sender, String content){
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getSender() {return sender;}
    public String getContent() {return content;}
    public LocalDateTime getTimestamp() {return timestamp;}

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}
