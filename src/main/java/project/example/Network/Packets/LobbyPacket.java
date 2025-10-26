package project.example.Network.Packets;

import Entyties.Player;

public class LobbyPacket {
    private String lobbyName;
    private int maxPlayers;
    private boolean isPrivate;
    private Player hostPlayer;
}
