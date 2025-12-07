package project.example.Network.Packets;

import Entyties.Lobby;

import java.util.ArrayList;
import java.util.HashMap;

public class LobbyPacket {
    public int lobbyId;
    public int sizeWorld;
    public int maxPlayers;
    public boolean isPrivate;

    public LobbyPacket() { }
}
