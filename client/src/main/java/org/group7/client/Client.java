package org.group7.client;


import org.greenrobot.eventbus.EventBus;

import org.group7.client.ocsf.AbstractClient;

public class Client extends AbstractClient {

    private static Client client = null;

    private Client(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

}
