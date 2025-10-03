package org.example;



import org.example.Network.GameServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        System.out.println(server);
    }
}