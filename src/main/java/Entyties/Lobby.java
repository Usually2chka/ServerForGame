package Entyties;

import java.util.ArrayList;

public class Lobby {
    private int id;

    private String lobbyName;
    private int maxPlayers;
    private boolean isPrivate;
    private Player hostPlayer;
    private int sizeWorld;
    private boolean isFallBlocks;
    private ArrayList<Player> players;

    public Lobby()
    {

    }

    public Lobby(String lobbyName, int maxPlayers, boolean isPrivate, int sizeWorld, boolean isFallBlocks, Player hostPlayer, int id)
    {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.hostPlayer = hostPlayer;
        this.sizeWorld = sizeWorld;
        this.isFallBlocks = isFallBlocks;
        this.players = new ArrayList<>();
        players.add(hostPlayer);
        this.id = id;
    }

    public void joinToLobby(Player player)
    {
        players.add(player);
    }

    public void kickPlayer(Player player) {
        players.remove(findById(player.getId()));
    }

    @Override
    public String toString()
    {
        return lobbyName + " " + isPrivate + " " + hostPlayer + " " + sizeWorld + " " + isFallBlocks + " " + players.size() + "/" + maxPlayers;
    }

    public String getLobbyName()
    {
        return lobbyName;
    }

    public int getHostID() {
        return players.get(0).getId();
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

    public int getPlayers()
    {
        return players.size();
    }

    public boolean getIsFallBlocks()
    {
        return isFallBlocks;
    }

    public int getId() { return id; }

    public Player findById(int id) {
        Player player = null;
        for (Player pl : players)
            if (pl.getId() == id)
                player = pl;
        return player;
    }
}
