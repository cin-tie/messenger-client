package com.cintie.messenger.protocol;

// Public key exchange payload
public class KeyExchangePayload implements Payload{

    private String publicKey;

    public KeyExchangePayload(){}

    public KeyExchangePayload(String publicKey){
        this.publicKey = publicKey;
    }

    public String getPublicKey(){
        return  this.publicKey;
    }

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }
}
