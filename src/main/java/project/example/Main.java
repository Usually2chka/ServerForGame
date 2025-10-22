package project.example;


import project.example.Network.GameServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        System.out.println("Сервер работает... Нажмите Enter для выхода.");
        System.in.read();
    }
}