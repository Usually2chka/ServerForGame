package project.example.Network.Packets;

import Entyties.Lobby;
import LocalData.ServerData;

import java.util.ArrayList;

public class SuccessPacket {
    public boolean isSuccess;
    public ArrayList<Lobby> lobbies;

    public SuccessPacket()
    {
        this.lobbies = ServerData.getLobbies();
    }
}
