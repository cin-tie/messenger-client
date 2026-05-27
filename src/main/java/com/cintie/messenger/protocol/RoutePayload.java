package com.cintie.messenger.protocol;

import java.util.List;

// Stores route hops for forwarding
public class RoutePayload implements Payload {

    private List<String> hops;

    public RoutePayload() {
    }

    public RoutePayload(List<String> hops) {
        this.hops = hops;
    }

    public List<String> getHops() {
        return hops;
    }

    public void setHops(List<String> hops) {
        this.hops = hops;
    }
}