package project.example.Network.Packets;

import Entyties.Player;

public class LobbyPacket {
    public String lobbyName;
    public int maxPlayers;
    public boolean isPrivate;
    public short sizeWorld;
    public boolean isFallBlocks;
    public Player hostPlayer;

    public boolean isSuccess;

    public LobbyPacket() { }

    public LobbyPacket(boolean isSuccess)
    {
        this.isSuccess = isSuccess;
    }
}
