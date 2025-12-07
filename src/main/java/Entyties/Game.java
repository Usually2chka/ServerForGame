package Entyties;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    public Lobby lobby;
    public HashMap<Integer, int[]> playerIdToCoordinate;
    public ArrayList<Connection> connections;
    public int[] queueTurns;
    public int currentTurn = 0;
    public ArrayList<Player> players;


    public Game(Lobby lobby, HashMap<Integer, int[]> playerIdToCoordinate, ArrayList<Connection> connections, int[] queueTurns) {
        this.lobby = lobby;
        this.playerIdToCoordinate = playerIdToCoordinate;
        this.connections = connections;
        this.queueTurns = queueTurns;
    }
    public Connection getPlayerByCurrentTurn(int currentTurn)
    {
        for (Connection con : connections)
            if (currentTurn == con.getID())
                return con;
        return null;
    }
    public int nextTurn() {
        return currentTurn = currentTurn < lobby.getMaxPlayers()-1 ? ++currentTurn : 0;
    }
}
