package Entyties;

import java.util.ArrayList;

public class Lobby {
    private String lobbyName;
    private int maxPlayers;
    private boolean isPrivate;
    private Player hostPlayer;
    private ArrayList<Player> players;

    public Lobby(String lobbyName, int maxPlayers, boolean isPrivate, Player hostPlayer)
    {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.hostPlayer = hostPlayer;
    }

    public void joinToLobby(Player player)
    {
        players.add(player);
    }
}
