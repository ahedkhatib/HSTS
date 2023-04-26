package org.group7.client.server;

import org.group7.client.server.ocsf.AbstractServer;
import org.group7.client.server.ocsf.ConnectionToClient;

public class Server extends AbstractServer {

    public Server(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.print("Hello World!");
    }
}
