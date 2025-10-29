package project.example;


import Entyties.Lobby;
import Entyties.Player;
import LocalData.ServerData;
import project.example.Network.GameServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        ServerData serverData = new ServerData();
        ServerData.createLobby(new Lobby("", 2, false, new Player(), 16, false));
        GameServer server = new GameServer();
    }
}