package LocalData;

import Entyties.Lobby;
import Entyties.Player;

import java.util.ArrayList;


public class ServerData {

    private static ArrayList<Lobby> lobbies;

    public ServerData()
    {
        lobbies = new ArrayList<>();
    }

    public static void createLobby(Lobby lobby)
    {
        lobbies.add(lobby);
        System.out.println(lobbies.toString());
    }
    public static void connectToLobby(Lobby lobby, Player player)
    {
        lobby.joinToLobby(player);
        System.out.println(lobby.toString());
    }

    public static ArrayList<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
