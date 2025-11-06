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
                if (packet.lobbies.isEmpty())
                    System.out.println("null");
                connection.sendTCP(packet);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof CreateLobbyPacket)
                    processedLobbyPacket((CreateLobbyPacket) object, connection);
                if (object instanceof JoinToLobbyPacket)
                    processedJoinToLobbyPacket(connection, (JoinToLobbyPacket) object);
            }
        });

        server.start();
        server.bind(PORT);
    }

    public void updateData()
    {
        server.sendToAllTCP(new HandshakePacket());
    }

    private void processedJoinToLobbyPacket(Connection connection, JoinToLobbyPacket packet)
    {
        if (packet.lobby != null) {
            JoinToLobbyPacket answer = new JoinToLobbyPacket();

            if (packet.lobby.getPlayers() < packet.lobby.getMaxPlayers()) {
                ServerData.connectToLobby(packet.lobby, packet.player);
                answer.isAllowed = true;
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

    private void processedLobbyPacket(CreateLobbyPacket createLobbyPacket, Connection connection)
    {
        Lobby lobby = new Lobby(createLobbyPacket.lobbyName,
                                createLobbyPacket.maxPlayers,
                                createLobbyPacket.isPrivate,
                                createLobbyPacket.sizeWorld,
                                createLobbyPacket.isFallBlocks,
                                createLobbyPacket.hostPlayer);
        ServerData.createLobby(lobby);
        server.sendToAllTCP(new LobbyPacket(lobby));
    }
}