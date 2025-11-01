package LocalData;

import Entyties.Lobby;

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
        System.out.println(lobbies.get(lobbies.size()-1));
    }

    public static ArrayList<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
