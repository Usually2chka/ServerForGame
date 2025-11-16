package project.example.Network;

import Entyties.Lobby;
import LocalData.ServerData;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Packets.*;

import java.io.IOException;

import static project.example.Network.Network.PORT;

public class GameServer {
    private Server server;

    public GameServer() throws IOException {
        server = new Server();
        Network.RegisterClasses(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                HandshakePacket packet = new HandshakePacket();
                packet.lobbies = ServerData.getLobbies();
                packet.playerId = ServerData.generateNextPlayerId();
                connection.sendTCP(packet);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LobbyPacket)
                    processedLobbyPacket((LobbyPacket) object);
                if (object instanceof AllLobbiesPacket)
                    processedAllLobbiesPacket(connection, (AllLobbiesPacket) object);
                if (object instanceof CreateLobbyPacket)
                    processedCreateLobbyPacket((CreateLobbyPacket) object, connection);
                if (object instanceof JoinToLobbyPacket)
                    processedJoinToLobbyPacket(connection, (JoinToLobbyPacket) object);
                if (object instanceof  LeaveFromLobbyPacket)
                    processedLeaveFromLobbyPacket((LeaveFromLobbyPacket) object);
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
        ServerData.createLobby(lobby);//, createLobbyPacket.hostPlayer);
        leaderLobby.sendTCP(new CreateLobbyPacket(true, ServerData.getNextLobbyId()));
        server.sendToAllTCP(new AllLobbiesPacket());
    }

    private void processedAllLobbiesPacket(Connection connection, AllLobbiesPacket packet)
    {
        server.sendToAllTCP(new AllLobbiesPacket());
    }
    private void processedLobbyPacket(LobbyPacket packet)
    {
        Lobby lobby = ServerData.findLobbyByID(packet.lobby.getId());
        if (lobby.getPlayers() == 0)
            ServerData.removeLobby(lobby);

        server.sendToAllTCP(new AllLobbiesPacket());
    }
    private void processedLeaveFromLobbyPacket(LeaveFromLobbyPacket packet)
    {
        processedLobbyPacket(new LobbyPacket(ServerData.leaveFromLobby(packet.lobby, packet.player)));
    }
}