package org.group7.server;

import java.io.IOException;

public class LaunchServer {
    private static Server server;

    public static void main(String[] args) throws IOException {
        server = new Server(3000);
        System.out.println("server is listening");
        server.listen();
    }
}
