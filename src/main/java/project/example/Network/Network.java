package project.example.Network;

import Entyties.Lobby;
import Entyties.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import project.example.Network.Packets.*;

import java.util.ArrayList;

public class Network {
    public static final int PORT = 55555;

    public static void RegisterClasses(EndPoint endPoint)
    {
        Kryo kryo = endPoint.getKryo();
        kryo.register(HandshakePacket.class);
        kryo.register(CreateLobbyPacket.class);
        kryo.register(Player.class);
        kryo.register(LobbyPacket.class);
        kryo.register(ArrayList.class);
        kryo.register(Lobby.class);
        kryo.register(JoinToLobbyPacket.class);
    }
}
