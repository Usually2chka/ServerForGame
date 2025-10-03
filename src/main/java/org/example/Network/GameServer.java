package org.example.Network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.example.Network.Packets.PacketMessage;

import java.io.IOException;

public class GameServer {
    public GameServer() throws IOException {
        Server server = new Server();
        server.getKryo().register(PacketMessage.class);
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection)
            {
                System.out.println("Клиент подключился: " + connection.getRemoteAddressTCP().getHostString());

                PacketMessage response = new PacketMessage();
                response.message = "Привет от сервера!";
                connection.sendTCP(response); // Отправка по TCP
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Клиент отключился.");
            }
        });
        server.start();
        server.bind(55555);

    }
}
