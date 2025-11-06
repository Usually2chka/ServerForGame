package project.example.Network.Packets;

import Entyties.Lobby;
import Entyties.Player;

public class JoinToLobbyPacket {
    public Lobby lobby;
    public Player player;
    public boolean isAllowed;
    public String reason;

    public JoinToLobbyPacket() { }
}
