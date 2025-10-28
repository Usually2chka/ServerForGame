package project.example.Network;

import Entyties.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import project.example.Network.Packets.HandshakePacket;
import project.example.Network.Packets.LobbyPacket;
import project.example.Network.Packets.SuccessPacket;

public class Network {
    public static final int PORT = 55555;

    public static void RegisterClasses(EndPoint endPoint)
    {
        Kryo kryo = endPoint.getKryo();
        kryo.register(HandshakePacket.class);
        kryo.register(LobbyPacket.class);
        kryo.register(Player.class);
        kryo.register(SuccessPacket.class);
    }
}
