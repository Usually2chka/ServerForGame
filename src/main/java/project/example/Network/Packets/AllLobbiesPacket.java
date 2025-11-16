package project.example.Network.Packets;


import Entyties.Lobby;
import LocalData.ServerData;

import java.util.ArrayList;

public class AllLobbiesPacket {
    public ArrayList<Lobby> lobbies;

    public AllLobbiesPacket()
    {
        lobbies = ServerData.getLobbies();
    }
}