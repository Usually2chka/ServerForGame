package project.example.Network.Packets;


import Entyties.Lobby;
import LocalData.ServerData;

import java.util.ArrayList;

public class HandshakePacket {
    public ArrayList<Lobby> lobbies;

    public HandshakePacket()
    {
        lobbies = ServerData.getLobbies();
    }
}