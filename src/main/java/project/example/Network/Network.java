package project.example.Network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import project.example.Network.Packets.HandshakePacket;

public class Network {
    public static final int PORT = 55555;

    public static void RegisterClasses(EndPoint endPoint)
    {
        Kryo kryo = endPoint.getKryo();
        kryo.register(HandshakePacket.class);
        kryo.register(String.class);
    }
}
