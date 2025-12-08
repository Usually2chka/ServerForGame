package project.example.Network.Packets;

import Entyties.Game;
import Entyties.Lobby;
import Entyties.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class GameStatePacket {
    public HashMap<Integer, int[]> playerIdToCoordinate;
    public Player playerTurned;
    public Player enemyPlayer;
    //public HashMap<Integer, Player> enemies;
    public int lobbyId;
    public boolean isAllowed;
    public int currentTurns;

    public GameStatePacket()
    {

    }

    public GameStatePacket(HashMap<Integer, int[]> idToCoord, int currentTurn)//, HashMap<Integer)//, Player> playersToDamage)
    {
        this.playerIdToCoordinate = idToCoord;
        this.currentTurns = currentTurn;
        //this.enemies = playersToDamage;
    }
}