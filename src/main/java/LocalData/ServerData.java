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
        for(Lobby lb : lobbyes)
            System.out.println(lb);
    }

    public static ArrayList<Lobby> getLobbies() {
        return new ArrayList<>(lobbyes);
    }
}
