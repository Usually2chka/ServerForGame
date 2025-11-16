package project.example.Network.Packets;

import Entyties.Player;

public class CreateLobbyPacket {
    public String lobbyName;
    public int maxPlayers;
    public boolean isPrivate;
    public int sizeWorld;
    public boolean isFallBlocks;
    public Player hostPlayer;
    public boolean isAllowed;
    public int id;

    public CreateLobbyPacket() { }
    public CreateLobbyPacket(boolean isAllowed, int id)
    {
        this.isAllowed = isAllowed;
        this.id = id;
    }
}
