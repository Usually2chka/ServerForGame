package project.example.Network.Packets;

import Entyties.Lobby;
import LocalData.ServerData;

import java.util.ArrayList;

public class SuccessPacket {
    public Lobby lobby;

    public SuccessPacket() {}

    public SuccessPacket(Lobby lobby)
    {
        this.lobby = lobby;
    }
}
