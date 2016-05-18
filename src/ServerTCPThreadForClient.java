import java.io.*;
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
            InputStream dis = socket.getInputStream();
            OutputStream dos = socket.getOutputStream();

            while (!socket.isClosed()) {
                long beginClientTime = System.currentTimeMillis();

                System.err.println(socket.isClosed());
                System.err.print("start read\n");
                ArrayProto.Array array;
                try {
                    array = ArrayProto.Array.parseFrom(dis);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                ++countOfTask;
                System.err.println("read info");
                //System.err.println(array);

                int arraySize = array.getN();
                int[] transitArray = new int[arraySize];

                for (int i = 0; i < arraySize; ++i) {
                    transitArray[i] = array.getData(i);
                }

                long beginQueryTime = System.currentTimeMillis();
                System.err.println(arraySize);

                Arrays.sort(transitArray);
                timeForTask += System.currentTimeMillis() - beginQueryTime;


                ArrayProto.Array.Builder arrayBuilder = ArrayProto.Array.newBuilder().setN(arraySize);
                for (int i = 0; i < arraySize; ++i) {
                    arrayBuilder.addData(transitArray[i]);
                }

                arrayBuilder.build().writeTo(dos);

                timeForClient += System.currentTimeMillis() - beginClientTime;
                System.err.println("send back");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
