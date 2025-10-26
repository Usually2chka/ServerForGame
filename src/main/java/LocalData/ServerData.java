package LocalData;

import Entyties.Lobby;

import java.util.ArrayList;


public class ServerData {

    private ArrayList<Lobby> lobbyes;

    public ServerData()
    {
        lobbyes = new ArrayList<>();
    }

    public void createLobby(Lobby lobby)
    {
        lobbyes.add(lobby);
    }
}
