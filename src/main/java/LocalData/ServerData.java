package LocalData;

import Entyties.Lobby;
import Entyties.Player;

import java.util.ArrayList;
import java.util.HashMap;


public class ServerData {

    private static HashMap<Integer, Lobby> lobbies;
    private static int nextLobbyId = -1;
    private static int nextPlayerId = -1;

    public ServerData()
    {
        lobbies = new HashMap<>();
    }

    public static void createLobby(Lobby lobby)//, Player leaderRoom)
    {
        lobbies.put(lobby.getId(), lobby);
        //lobby.joinToLobby(leaderRoom);
        System.out.println(lobbies.toString());
    }
    public static void connectToLobby(Lobby lobby, Player player)
    {
        lobby.joinToLobby(player);
        System.out.println(lobby.toString());
    }

    public static void removeLobby(Lobby lobby)
    {
        lobbies.remove(lobby.getId());
    }

    public static Lobby leaveFromLobby(Lobby lobby, Player player)
    {
        Lobby lb = findLobbyByID(lobby.getId());
        lb.kickPlayer(player);
        return lb;
    }

    public static Lobby findLobbyByID(int id)
    {
        return lobbies.get(id);
    }

    public static ArrayList<Lobby> getLobbies() {
        ArrayList<Lobby> list = new ArrayList<>();

        for (int i = 0; i < lobbies.size(); i++)
            if (lobbies.get(i) != null)
                list.add(lobbies.get(i));

        return list;
    }

    public static int generateLobbyId() { return ++nextLobbyId; }
    public static int generateNextPlayerId() { return ++nextPlayerId; }
    public static int getNextLobbyId() { return nextLobbyId; }
    public static int getNextPlayerId() { return nextPlayerId; }
}
