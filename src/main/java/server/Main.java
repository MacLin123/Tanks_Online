package server;

import server.model.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        System.out.println("server is running....");
    }
}
