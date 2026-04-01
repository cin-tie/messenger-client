package com.cintie.messenger.crypto;

import java.util.UUID;

public class PeerIdGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
