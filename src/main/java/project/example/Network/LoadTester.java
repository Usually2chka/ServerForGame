package project.example.Network;

import Entyties.Lobby;
import Entyties.Player;
import LocalData.ServerData;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import project.example.Network.Network;
import project.example.Network.Packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Простая программа для нагрузочного тестирования GameServer на базе kryonet.
 *
 * Как работает:
 * - создаёт N клиентов в отдельных потоках
 * - каждый клиент подключается, ждёт HandshakePacket
 * - либо создаёт лобби (с вероятностью createRatio), либо пытается подключиться к существующему лобби
 * - после попадания в лобби клиент периодически отправляет GameStatePacket (симуляция ходов)
 * - в конце собираются и печатаются простые метрики
 *
 * ВАЖНО: этот код ожидает, что он компилируется и запускается в том же проекте, где
 * определены Network.RegisterClasses(...) и все Packet/Entity классы (как в вашем проекте).
 */
public class LoadTester {
    public static void main(String[] args) throws InterruptedException {
//        if (args.length < 4) {
//            System.out.println("Usage: java LoadTestClient <host> <port> <numClients> <durationSeconds> [createRatio] [messageIntervalMs] [rampUpMs]");
//            System.out.println("Example: java LoadTestClient 127.0.0.1 54555 200 60 0.2 200 5000");
//            return;
//        }
        //ServerData serverData = new ServerData();
        final String host = "127.0.0.1";
        final int port = 55555;
        final int numClients = 200;
        final int durationSeconds = 30;
        final double createRatio = 0.2;
        final int messageIntervalMs = 200;
        final int rampUpMs = 5000;

        System.out.printf("Starting load test: host=%s port=%d clients=%d duration=%ds createRatio=%.2f msgInterval=%d rampUp=%d\n",
                host, port, numClients, durationSeconds, createRatio, messageIntervalMs, rampUpMs);

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Math.min(64, numClients));
        final ExecutorService clientPool = Executors.newFixedThreadPool(Math.min(256, numClients));

        final AtomicInteger connectedCount = new AtomicInteger();
        final AtomicInteger failedConnect = new AtomicInteger();
        final AtomicInteger createdLobbies = new AtomicInteger();
        final AtomicInteger joinedLobbies = new AtomicInteger();
        final AtomicInteger gameMessagesSent = new AtomicInteger();

        final List<ClientWorker> workers = new CopyOnWriteArrayList<>();

        final CountDownLatch startLatch = new CountDownLatch(1);
        final Random rng = ThreadLocalRandom.current();

        final long startTime = System.currentTimeMillis();

        for (int i = 0; i < numClients; i++) {
            final int idx = i;
            clientPool.submit(() -> {
                try {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long target = (long) (idx * (rampUpMs / (double) Math.max(1, numClients)));
                    long toSleep = Math.max(0, target - elapsed);
                    if (toSleep > 0) Thread.sleep(toSleep);

                    ClientWorker w = new ClientWorker(host, port, idx, createRatio, messageIntervalMs,
                            connectedCount, failedConnect, createdLobbies, joinedLobbies, gameMessagesSent, scheduler);
                    workers.add(w);
                    w.start();
                } catch (InterruptedException ignored) {}
            });
        }


        startLatch.countDown();
        System.out.println("All client tasks submitted. Waiting for test duration...");
        Thread.sleep(durationSeconds * 1000L);


        System.out.println("Stopping workers...");
        for (ClientWorker w : workers) w.stop();

        clientPool.shutdownNow();
        scheduler.shutdownNow();

        System.out.println("--- Test summary ---");
        System.out.println("Requested clients: " + numClients);
        System.out.println("Connected clients: " + connectedCount.get());
        System.out.println("Failed connects: " + failedConnect.get());
        System.out.println("Lobbies created: " + createdLobbies.get());
        System.out.println("Lobbies joined: " + joinedLobbies.get());
        System.out.println("Game messages sent: " + gameMessagesSent.get());
        System.out.println("---------------------");
    }

    static class ClientWorker {
        private final String host;
        private final int port;
        private final int idx;
        private final double createRatio;
        private final int messageIntervalMs;
        private final AtomicInteger connectedCount;
        private final AtomicInteger failedConnect;
        private final AtomicInteger createdLobbies;
        private final AtomicInteger joinedLobbies;
        private final AtomicInteger gameMessagesSent;
        private final ScheduledExecutorService scheduler;

        private volatile boolean running = true;
        private Client client;
        private int playerId = -1;
        private Lobby assignedLobby = null;

        ClientWorker(String host, int port, int idx, double createRatio, int messageIntervalMs,
                     AtomicInteger connectedCount, AtomicInteger failedConnect, AtomicInteger createdLobbies,
                     AtomicInteger joinedLobbies, AtomicInteger gameMessagesSent, ScheduledExecutorService scheduler) {
            this.host = host;
            this.port = port;
            this.idx = idx;
            this.createRatio = createRatio;
            this.messageIntervalMs = messageIntervalMs;
            this.connectedCount = connectedCount;
            this.failedConnect = failedConnect;
            this.createdLobbies = createdLobbies;
            this.joinedLobbies = joinedLobbies;
            this.gameMessagesSent = gameMessagesSent;
            this.scheduler = scheduler;
        }

        void start() {
            client = new Client();
            Network.RegisterClasses(client);
            client.start();

            client.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    // nothing
                }

                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof HandshakePacket) {
                        HandshakePacket h = (HandshakePacket) object;
                        playerId = h.playerId;
                        connectedCount.incrementAndGet();

                        boolean create = ThreadLocalRandom.current().nextDouble() < createRatio || h.lobbies.isEmpty();
                        if (create) {
                            createLobby();
                        } else {
                            Lobby target = null;
                            for (Lobby l : h.lobbies) {
                                if (l != null && l.getPlayers() < l.getMaxPlayers()) {
                                    target = l; break;
                                }
                            }
                            if (target != null) joinLobby(target);
                            else createLobby();
                        }
                    } else if (object instanceof CreateLobbyPacket) {
                        CreateLobbyPacket p = (CreateLobbyPacket) object;
                        if (p.isAllowed) {
                            createdLobbies.incrementAndGet();
                        }
                    } else if (object instanceof JoinToLobbyPacket) {
                        JoinToLobbyPacket p = (JoinToLobbyPacket) object;
                        if (p.isAllowed) {
                            joinedLobbies.incrementAndGet();
                            scheduleGameMessages();
                        }
                    } else if (object instanceof AllLobbiesPacket) {
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                }
            });

            try {
                client.connect(3000, host, port);
            } catch (IOException e) {
                failedConnect.incrementAndGet();
                closeClient();
                return;
            }
        }

        private void createLobby() {
            Player player = new Player();
            player.id = ServerData.generateNextPlayerId();
            Lobby l = new Lobby("lt-" + idx, // name
                    8, // maxPlayers
                    false, // isPrivate
                    20, // sizeWorld
                    false, // isFallBlocks
                    player,//("LoadClient-" + idx),
                    ServerData.generateLobbyId());
            CreateLobbyPacket pack = new CreateLobbyPacket(true, l.getId());
            client.sendTCP(pack);
        }

        private void joinLobby(Lobby lobby) {
            JoinToLobbyPacket p = new JoinToLobbyPacket();
            p.lobby = lobby;
            p.player = new Player();
            client.sendTCP(p);
        }

        private void scheduleGameMessages() {
            Runnable task = () -> {
                if (!running) return;
                GameStatePacket gs = new GameStatePacket();
                gs.lobbyId = (assignedLobby != null) ? assignedLobby.getId() : -1; // may be -1
                gs.currentTurns = playerId;
                gs.playerIdToCoordinate = new java.util.HashMap<>();
                gs.playerIdToCoordinate.put(playerId, new int[]{ThreadLocalRandom.current().nextInt(0, 20), ThreadLocalRandom.current().nextInt(0, 20)});

                try {
                    client.sendTCP(gs);
                    gameMessagesSent.incrementAndGet();
                } catch (Exception ignored) {}
            };
            scheduler.scheduleAtFixedRate(task, 0, Math.max(10, messageIntervalMs), TimeUnit.MILLISECONDS);
        }

        void stop() {
            running = false;
            closeClient();
        }

        private void closeClient() {
            try {
                if (client != null) client.stop();
            } catch (Exception ignored) {}
        }
    }
}
