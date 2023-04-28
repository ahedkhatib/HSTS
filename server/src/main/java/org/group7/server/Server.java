package org.group7.server;

import org.group7.server.ocsf.AbstractServer;
import org.group7.server.ocsf.ConnectionToClient;

public class Server extends AbstractServer {

    public Server(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.print("Hello World!");
    }

    @Override
    protected void serverClosed() {
        GenerateData.clearTables();
        super.serverClosed();
    }
}
