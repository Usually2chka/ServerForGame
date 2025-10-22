package project.example.Network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Packets.ChatMessage;

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
                System.out.println("Клиент подключился: " + connection.getRemoteAddressTCP());
                connection.sendTCP(new ChatMessage("Сервер: Добро пожаловать в чат!"));
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ChatMessage) {
                    ChatMessage message = (ChatMessage) object;
                    System.out.println(": " + message.text);

                    // Рассылаем сообщение всем подключенным клиентам
                    server.sendToAllTCP(message);
                }
            }
        });

        server.start();
        server.bind(PORT);
        System.out.println("Сервер запущен на порту " + PORT);
    }
}