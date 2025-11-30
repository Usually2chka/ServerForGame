package LocalData;

import Entyties.Lobby;
import Entyties.Player;

import java.util.ArrayList;
import java.util.HashMap;


public class ServerData {

    private static HashMap<Integer, Lobby> lobbies;
    private static HashMap<Integer, Player> listPlayers;
    private static int nextLobbyId = -1;
    private static int nextPlayerId = -1;

    public ServerData()
    {
        lobbies = new HashMap<>();
        listPlayers = new HashMap<>();
    }

    public static void createLobby(Lobby lobby, Player leaderRoom)
    {
        lobbies.put(lobby.getId(), lobby);
        leaderRoom.lobbyId = lobby.getId();
        listPlayers.put(leaderRoom.getId(), leaderRoom);
    }
    public static void connectToLobby(Lobby lobby, Player player)
    {
        listPlayers.put(player.getId(), player);
        player.lobbyId = lobby.getId();
        lobby.joinToLobby(player);
        lobbies.replace(lobby.getId(), lobbies.get(lobby.getId()), lobby);
    }

    public static void removeLobby(Lobby lobby)
    {
        lobbies.remove(lobby.getId());
    }

    public static Lobby leaveFromLobby(Lobby lobby, Player player)
    {
        Lobby lb = null;
        if (lobby != null) {
            lb = findLobbyByID(lobby.getId());
            lb.kickPlayer(player);

            if (player.getId() == lb.getHostID() && lb.getPlayers() > 0)
                lb.updateHost();

            lobbies.replace(lb.getId(), lobbies.get(lb.getId()), lb);
        }

        return lb;
    }

    public static Lobby findLobbyByID(int id)
    {
        return lobbies.get(id);
    }

    public static Player findPlayerByID(int id)
    {
        return listPlayers.get(id);
    }

    public static ArrayList<Lobby> getLobbies() {
        ArrayList<Lobby> list = new ArrayList<>();

        for (Lobby lobby : lobbies.values())
            if (lobby != null)
                list.add(lobby);

        return list;
    }

    public static int generateNextPlayerId() {
        int newId = ++nextPlayerId;
        Player newPlayer = new Player();
        listPlayers.put(newId, newPlayer);

        return newId;
    }
    public static int generateLobbyId() { return ++nextLobbyId; }
    public static int getNextLobbyId() { return nextLobbyId; }
    public static int getNextPlayerId() { return nextPlayerId; }
}
