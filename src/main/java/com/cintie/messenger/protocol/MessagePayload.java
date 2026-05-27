package com.cintie.messenger.protocol;

// User message payload
public class MessagePayload implements Payload{

    private String content;

    public MessagePayload(){}

    public MessagePayload(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }
}