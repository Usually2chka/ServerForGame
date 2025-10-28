package LocalData;

import Entyties.Lobby;

import java.util.ArrayList;


public class ServerData {

    private static ArrayList<Lobby> lobbyes;

    public ServerData()
    {
        lobbyes = new ArrayList<>();
    }

    public static void createLobby(Lobby lobby)
    {
        lobbyes.add(lobby);
    }

    public static ArrayList<Lobby> getLobbies() {
        return (ArrayList<Lobby>) lobbyes.clone();
    }
}
