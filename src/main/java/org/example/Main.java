package org.example;


import java.io.IOException;

import org.example.Network.GameServer;

public class Main {
    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        System.out.println("Сервер работает... Нажмите Enter для выхода.");
        System.in.read();
    }
}