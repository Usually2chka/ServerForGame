package project.example.Network.Packets;

import Entyties.Player;

public class CreateLobbyPacket {
    public String lobbyName;
    public int maxPlayers;
    public boolean isPrivate;
    public int sizeWorld;
    public boolean isFallBlocks;
    public Player hostPlayer;

    public boolean isSuccess;

    public CreateLobbyPacket() { }

    public CreateLobbyPacket(boolean isSuccess)
    {
        this.isSuccess = isSuccess;
    }
}
