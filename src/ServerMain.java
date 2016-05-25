import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerMain {
    private static final int SERVER_TCP_THREAD_FOR_CLIENT_PORT = 8081;
    private static final int SERVER_TCP_ONE_THREAD_PORT = 8082;
    private static final int SERVER_TCP_NON_BLOCKING_PORT = 8083;
    private static final int SERVER_TCP_THREAD_POOL_PORT = 8084;

    private static final int SERVER_UDP_THREAD_FOR_QUERY_PORT = 8085;
    private static final int SERVER_UDP_THREAD_POOL_PORT = 8086;

    private final int port;
    private ServerSocket serverSocket;
    private Thread catchThread;

    private ServerTCPThreadForClient serverTCPThreadForClient;
    private ServerTCPOneThread serverTCPOneThread;
    private ServerTCPNonBlocking serverTCPNonBlocking;
    private ServerTCPThreadPool serverTCPThreadPool;

    private ServerUDPThreadForQuery serverUDPThreadForQuery;
    private ServerUDPThreadPool serverUDPThreadPool;

    private ServerMain(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain(8080);

        serverMain.serverTCPThreadForClient = new ServerTCPThreadForClient(SERVER_TCP_THREAD_FOR_CLIENT_PORT);
        serverMain.serverTCPOneThread = new ServerTCPOneThread(SERVER_TCP_ONE_THREAD_PORT);
        serverMain.serverTCPNonBlocking = new ServerTCPNonBlocking(SERVER_TCP_NON_BLOCKING_PORT);
        serverMain.serverTCPThreadPool = new ServerTCPThreadPool(SERVER_TCP_THREAD_POOL_PORT);

        serverMain.serverUDPThreadForQuery = new ServerUDPThreadForQuery(SERVER_UDP_THREAD_FOR_QUERY_PORT);
        serverMain.serverUDPThreadPool = new ServerUDPThreadPool(SERVER_UDP_THREAD_POOL_PORT);

        try {
            serverMain.serverSocket = new ServerSocket(serverMain.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverMain.catchThread = new Thread(() -> {
            try {
                serverMain.catchSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverMain.catchThread.start();
    }

    private void catchSocket() throws IOException {
        while (true) {
            Socket socket = accept();
            if (socket != null) {
                handlingQueries(socket);
            } else {
                return;
            }
        }
    }

    private Socket accept() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketException e) {
            return null;
        }
    }

    private void handlingQueries(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                String task = dis.readUTF();
                int id = dis.readInt();

                Server currentServer = null;
                if (id == SERVER_TCP_NON_BLOCKING_PORT) {
                    currentServer = serverTCPNonBlocking;
                } else if (id == SERVER_TCP_ONE_THREAD_PORT) {
                    currentServer = serverTCPOneThread;
                } else if (id == SERVER_TCP_THREAD_FOR_CLIENT_PORT) {
                    currentServer = serverTCPThreadForClient;
                } else if (id == SERVER_TCP_THREAD_POOL_PORT) {
                    currentServer = serverTCPThreadPool;
                } else if (id == SERVER_UDP_THREAD_FOR_QUERY_PORT) {
                    currentServer = serverUDPThreadForQuery;
                } else if (id == SERVER_UDP_THREAD_POOL_PORT) {
                    currentServer = serverUDPThreadPool;
                }

                assert (currentServer != null);

                switch (task) {
                    case "START":
                        if (id == SERVER_TCP_NON_BLOCKING_PORT) {
                            serverTCPNonBlocking = new ServerTCPNonBlocking(id);
                            serverTCPNonBlocking.start();
                        } else if (id == SERVER_TCP_ONE_THREAD_PORT) {
                            serverTCPOneThread = new ServerTCPOneThread(id);
                            serverTCPOneThread.start();
                        } else if (id == SERVER_TCP_THREAD_FOR_CLIENT_PORT) {
                            serverTCPThreadForClient = new ServerTCPThreadForClient(id);
                            serverTCPThreadForClient.start();
                            System.err.println("Start");
                        } else if (id == SERVER_TCP_THREAD_POOL_PORT) {
                            serverTCPThreadPool = new ServerTCPThreadPool(id);
                            serverTCPThreadPool.start();
                        } else if (id == SERVER_UDP_THREAD_FOR_QUERY_PORT) {
                            serverUDPThreadForQuery = new ServerUDPThreadForQuery(id);
                            serverUDPThreadForQuery.start();
                        } else {
                            serverUDPThreadPool = new ServerUDPThreadPool(id);
                            serverUDPThreadPool.start();
                        }
                        dos.writeUTF("OK");
                        break;
                    case "STOP":
                        currentServer.stop();
                        System.err.println("stop");
                        dos.writeUTF("OK");
                        break;
                    case "GET_CLIENT":
                        dos.writeLong(currentServer.getTimeForClient());
                        System.err.println("print get client");
                        break;
                    case "GET_TASK":
                        dos.writeLong(currentServer.getTimeForTask());
                        System.err.println("print get server");
                        break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
