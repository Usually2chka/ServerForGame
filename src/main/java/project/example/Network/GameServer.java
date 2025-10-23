package project.example.Network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Packets.HandshakePacket;

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
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof HandshakePacket) {
                }
            }
        });

        server.start();
        server.bind(PORT);
    }
}