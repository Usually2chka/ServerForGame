package project.example.Network;

import Entyties.Lobby;
import LocalData.ServerData;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Packets.HandshakePacket;
import project.example.Network.Packets.LobbyPacket;
import project.example.Network.Packets.SuccessPacket;

import java.io.IOException;
import java.util.*;

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
                if (object instanceof HandshakePacket)
                    processedHandshakePacker();
                if (object instanceof LobbyPacket)
                    processedLobbyPacket((LobbyPacket) object, connection);
            }
        });

        server.start();
        server.bind(PORT);
    }

    public void updateData()
    {
        server.sendToAllTCP(new SuccessPacket());
    }

    private void processedHandshakePacker()
    {

    }

    private void processedLobbyPacket(LobbyPacket lobbyPacket, Connection connection)
    {
        ServerData.createLobby(new Lobby(lobbyPacket.lobbyName,
                                         lobbyPacket.maxPlayers,
                                         lobbyPacket.isPrivate,
                                         lobbyPacket.hostPlayer,
                                         lobbyPacket.sizeWorld,
                                         lobbyPacket.isFallBlocks));
        server.sendToTCP(connection.getID(), new LobbyPacket(true));
        updateData();
    }
}