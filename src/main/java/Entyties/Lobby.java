package Entyties;

import java.util.ArrayList;

public class Lobby {
    private String lobbyName;
    private int maxPlayers;
    private boolean isPrivate;
    private Player hostPlayer;
    private int sizeWorld;
    private boolean isFallBlocks;
    private ArrayList<Player> players;

    private Lobby()
    {

    }

    public Lobby(String lobbyName, int maxPlayers, boolean isPrivate, int sizeWorld, boolean isFallBlocks, Player hostPlayer)
    {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.hostPlayer = hostPlayer;
        this.sizeWorld = sizeWorld;
        this.isFallBlocks = isFallBlocks;
        this.players = new ArrayList<>();
    }

    public void joinToLobby(Player player)
    {
        players.add(player);
    }

    public String getLobbyName()
    {
        return lobbyName;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public boolean getIsPrivate()
    {
        return isPrivate;
    }

    public int getSizeWorld()
    {
        return sizeWorld;
    }

    public int getPlayers() { return players.size(); }

    public boolean getIsFallBlocks()
    {
        return isFallBlocks;
    }

    @Override
    public String toString()
    {
        return lobbyName + " " + maxPlayers + " " + isPrivate + " " + hostPlayer + " " + sizeWorld + " " + isFallBlocks + " " + players.size() + "/4";
    }

}
