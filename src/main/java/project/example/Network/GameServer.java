package project.example.Network;

import Entyties.Game;
import Entyties.Lobby;
import Entyties.Player;
import LocalData.ServerData;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static project.example.Network.Network.PORT;

public class GameServer {
    private Server server;
    private final HashMap<Connection, Integer> connectionKey = new HashMap<>();
    private final HashMap<Integer, ArrayList<Connection>> idLobbyToArrayPlayers = new HashMap<>();
    private final HashMap<Integer, Game> lobbyInGame = new HashMap<>();

    public GameServer() throws IOException {
        server = new Server();
        Network.RegisterClasses(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                HandshakePacket packet = new HandshakePacket();
                packet.lobbies = ServerData.getLobbies();
                packet.playerId = ServerData.generateNextPlayerId();
                connectionKey.put(connection, packet.playerId);
                connection.sendTCP(packet);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GameStatePacket)
                    processedGameStatePacket((GameStatePacket) object);
                if (object instanceof LobbyPacket)
                    processedLobbyPacket((LobbyPacket) object);
                if (object instanceof AllLobbiesPacket)
                    processedAllLobbiesPacket(connection, (AllLobbiesPacket) object);
                if (object instanceof CreateLobbyPacket)
                    processedCreateLobbyPacket((CreateLobbyPacket) object, connection);
                if (object instanceof JoinToLobbyPacket)
                    processedJoinToLobbyPacket(connection, (JoinToLobbyPacket) object);
                if (object instanceof  LeaveFromLobbyPacket)
                    processedLeaveFromLobbyPacket(((LeaveFromLobbyPacket) object).lobby.getId(), ((LeaveFromLobbyPacket) object).player.getId(), connection);
            }

            @Override
            public void disconnected(Connection connection) {
                Player player = ServerData.findPlayerByID(connectionKey.get(connection));
                Lobby lobby = ServerData.findLobbyByID(player.lobbyId);
                System.out.println(player.lobbyId);
                if (player.lobbyId != -1 && lobby != null)
                    processedLeaveFromLobbyPacket(lobby.getId(), player.getId(), connection);

                if (idLobbyToArrayPlayers.get(player.lobbyId) != null)
                    if (!idLobbyToArrayPlayers.get(player.lobbyId).isEmpty()) { //под вопросом
                        idLobbyToArrayPlayers.get(player.lobbyId).remove(connection);
                        if (idLobbyToArrayPlayers.get(player.lobbyId).isEmpty())
                            idLobbyToArrayPlayers.remove(player.lobbyId);
                    }


            }
        });

        server.start();
        server.bind(PORT);
    }

    private void processedJoinToLobbyPacket(Connection connection, JoinToLobbyPacket packet)
    {
        if (packet.lobby != null) {
            JoinToLobbyPacket answer = new JoinToLobbyPacket();

            if (packet.lobby.getPlayers() < packet.lobby.getMaxPlayers()) {
                ServerData.connectToLobby(packet.lobby, packet.player);
                answer.isAllowed = true;
                idLobbyToArrayPlayers.get(packet.lobby.getId()).add(connection);
                server.sendToAllTCP(new AllLobbiesPacket());
            } else {
                answer.isAllowed = false;
                answer.reason = "Lobby is full";
            }
            server.sendToTCP(connection.getID(), answer);
        }
        else {
            System.out.println("lobby to connect is null");
        }
    }

    private void processedCreateLobbyPacket(CreateLobbyPacket createLobbyPacket, Connection leaderLobby)
    {
        Lobby lobby = new Lobby(createLobbyPacket.lobbyName,
                                createLobbyPacket.maxPlayers,
                                createLobbyPacket.isPrivate,
                                createLobbyPacket.sizeWorld,
                                createLobbyPacket.isFallBlocks,
                                createLobbyPacket.hostPlayer,
                                ServerData.generateLobbyId());
        ServerData.createLobby(lobby, createLobbyPacket.hostPlayer);
        leaderLobby.sendTCP(new CreateLobbyPacket(true, ServerData.getNextLobbyId()));
        idLobbyToArrayPlayers.put(ServerData.getNextLobbyId(), new ArrayList<>());
        idLobbyToArrayPlayers.get(ServerData.getNextLobbyId()).add(leaderLobby);
        server.sendToAllTCP(new AllLobbiesPacket());
    }

    private void processedAllLobbiesPacket(Connection connection, AllLobbiesPacket packet)
    {
        server.sendToAllTCP(new AllLobbiesPacket());
    }

    private void processedLeaveFromLobbyPacket(int lobbyId, int playerId, Connection connection)
    {
        Lobby lobby = ServerData.findLobbyByID(lobbyId);
        Player player = lobby.findById(playerId);
        ServerData.leaveFromLobby(lobby, player);
        idLobbyToArrayPlayers.get(lobbyId).remove(connection);
        if (lobby.getPlayers() == 0) {
            idLobbyToArrayPlayers.remove(lobbyId);
            ServerData.removeLobby(lobby);
        }

        server.sendToAllTCP(new AllLobbiesPacket());
    }

    private void processedLobbyPacket(LobbyPacket packet)
    {
        Lobby lobby = ServerData.findLobbyByID(packet.lobbyId);

        for (Connection con : idLobbyToArrayPlayers.get(lobby.getId()))
            con.sendTCP(new LobbyPacket());

        inialisationGame(packet);
        ServerData.removeLobby(ServerData.findLobbyByID(packet.lobbyId));
        server.sendToAllTCP(new AllLobbiesPacket()); //удаление в рантайм
        //server.sendToAllTCP(new LobbyPacket());
    }

    private void processedGameStatePacket(GameStatePacket packet) {
        Game game = lobbyInGame.get(packet.lobbyId);
        HashMap<Integer, Player> enemiesGotDamage = new HashMap<>();
        Player playerToDamage;
        int countDiedPlayer = 0;
        if (!isGameEnd()) {
            for (Player p : game.lobby.getEntityPlayers()) {
                if (p.hitPoint <= 0) {
                    countDiedPlayer++;
                    p.isDied = true;
                }
                //if (coord == correct)
                game.playerIdToCoordinate.replace(p.getId(), game.playerIdToCoordinate.get(p.getId()),
                                                             packet.playerIdToCoordinate.get(p.getId()));
            }

            game.currentTurn = game.nextTurn();

            for (Connection con : game.connections)
                con.sendTCP(new GameStatePacket(game.playerIdToCoordinate, game.queueTurns[game.currentTurn]));//, enemiesGotDamage));
        }
    }


    private void inialisationGame(LobbyPacket packet) {

        Lobby lobby = ServerData.findLobbyByID(packet.lobbyId);
        int sizeWorld = packet.sizeWorld;
        Random random = new Random();
        ArrayList<Connection> connections = idLobbyToArrayPlayers.get(lobby.getId());
        HashMap<Integer, int[]> playerIdToCoordinate = new HashMap<>();
        int[] queueTurns = new int[lobby.getPlayers()];
        System.out.println(queueTurns.length);

        for (int i = 0; i < queueTurns.length; i++)
            queueTurns[i] = lobby.getEntityPlayers().get(i).getId();

        for (Connection con : connections) {
            int playerId = connectionKey.get(con);
            int positionX = random.nextInt(sizeWorld);
            int positionY = random.nextInt(sizeWorld);

            playerIdToCoordinate.put(playerId, new int[]{positionX, positionY});
        }

        for (Connection con : connections)
            con.sendTCP(new GameStatePacket(playerIdToCoordinate, queueTurns[0]));//, null));

        lobbyInGame.put(packet.lobbyId, new Game(ServerData.findLobbyByID(packet.lobbyId), playerIdToCoordinate, connections, queueTurns));
    }
    private boolean isGameEnd() {
        return false;
    }
}