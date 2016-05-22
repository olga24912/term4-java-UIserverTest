import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class ServerCommon {
    protected ServerSocket serverSocket;
    protected final int port;

    protected volatile AtomicLong timeForTask = new AtomicLong();
    protected volatile AtomicLong timeForClient = new AtomicLong();

    protected volatile AtomicInteger countOfTask = new AtomicInteger();
    private Thread catchThread;

    public ServerCommon(int port) {
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

    protected abstract void catchSocket() throws IOException;

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


    protected Socket accept() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketException e) {
            return null;
        }

    }

    protected void handlingQueries(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                processOneTask(dis, dos);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected void processOneTask(DataInputStream dis, DataOutputStream dos) throws IOException {
        long beginTimeForClient = -System.currentTimeMillis();

        countOfTask.incrementAndGet();

        byte[] byteArray;
        byteArray = new byte[dis.readInt()];
        dis.readFully(byteArray);

        ArrayProto.Array array = ArrayProto.Array.parseFrom(byteArray);

        timeForTask.addAndGet(-System.currentTimeMillis());

        array = ArrayProto.Array.newBuilder().
                addAllData(array.getDataList().stream().sorted().collect(Collectors.toList())).build();

        timeForTask.addAndGet(System.currentTimeMillis());


        dos.writeInt(array.getSerializedSize());
        dos.write(array.toByteArray());
        dos.flush();

        timeForClient.addAndGet(beginTimeForClient);
        timeForClient.addAndGet(System.currentTimeMillis());
    }

    public long getTimeForTask() {
        return timeForTask.get()/countOfTask.get();
    }

    public long getTimeForClient() {
        return timeForClient.get()/countOfTask.get();
    }
}
