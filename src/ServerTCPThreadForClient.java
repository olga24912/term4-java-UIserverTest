import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerTCPThreadForClient {
    private ServerSocket serverSocket;
    private final int port;

    private long timeForTask;
    private long timeForClient;

    public ServerTCPThreadForClient(int port) {
        this.port = port;
    }

    public Thread start() throws IOException {
        serverSocket = new ServerSocket(port);
        Thread thread = new Thread(() -> {
            try {
                catchSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }

    public synchronized void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket accept() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketException e) {
            return null;
        }

    }

    private void catchSocket() throws IOException {
        while (true) {
            Socket socket = accept();
            long beginClientTime = System.currentTimeMillis();
            if (socket != null) {
                (new Thread(()->{
                    handlingQueries(socket, beginClientTime);})).start();
            } else {
                return;
            }
        }
    }

    private void handlingQueries(Socket socket, long beginClientTime) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                int arraySize = dis.readInt();
                int[] array = new int[arraySize];

                for (int i = 0; i < arraySize; ++i) {
                    array[i] = dis.readInt();
                }

                long beginQueryTime = System.currentTimeMillis();
                System.err.println("read info");
                Arrays.sort(array);
                timeForTask = System.currentTimeMillis() - beginQueryTime;

                dos.writeInt(arraySize);
                for (int i = 0; i < arraySize; ++i) {
                    dos.writeInt(array[i]);
                }
                System.err.println("send back");
            }
            System.err.println("Socket close");
        } catch (IOException ignored) {
        } finally {
            try {
                timeForClient = System.currentTimeMillis() - beginClientTime;
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public long getTimeForTask() {
        return timeForTask;
    }

    public long getTimeForClient() {
        return timeForClient;
    }
}
