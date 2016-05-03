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

    private int countOfTask = 0;

    private Thread catchThread;

    public ServerTCPThreadForClient(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        catchThread = new Thread(() -> {
            try {
                catchSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        catchThread.start();
    }

    public synchronized void stop() {
        try {
            serverSocket.close();
            catchThread.interrupt();
            try {
                catchThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            if (socket != null) {
                (new Thread(()->{
                    handlingQueries(socket);})).start();
            } else {
                return;
            }
        }
    }

    private void handlingQueries(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                int arraySize = dis.readInt();
                long beginClientTime = System.currentTimeMillis();
                ++countOfTask;
                int[] array = new int[arraySize];

                for (int i = 0; i < arraySize; ++i) {
                    array[i] = dis.readInt();
                }

                long beginQueryTime = System.currentTimeMillis();
                System.err.println("read info");
                Arrays.sort(array);
                timeForTask += System.currentTimeMillis() - beginQueryTime;

                dos.writeInt(arraySize);
                for (int i = 0; i < arraySize; ++i) {
                    dos.writeInt(array[i]);
                }
                timeForClient += System.currentTimeMillis() - beginClientTime;
                System.err.println("send back");
            }
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public long getTimeForTask() {
        return timeForTask/countOfTask;
    }

    public long getTimeForClient() {
        return timeForClient/countOfTask;
    }
}
