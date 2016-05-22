import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int port;
    private String host;

    private int arraySize;
    private int cntQuery;
    private int timeBetweenQuery;
    private Random rnd = new Random();
    private long workingTime = 0;
    private boolean reconnectAfterQuery = false;

    public long getWorkingTime() {
        return workingTime;
    }

    public Client(String host, int port, int arraySize, int cntQuery, int timeBetweenQuery, boolean reconnectAfterQuery) throws IOException {
        socket = new Socket();
        if (!reconnectAfterQuery) {
            socket.connect(new InetSocketAddress(host, port));

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }

        this.arraySize = arraySize;
        this.cntQuery = cntQuery;
        this.timeBetweenQuery = timeBetweenQuery;
        this.reconnectAfterQuery = reconnectAfterQuery;

        this.port = port;
        this.host = host;
    }

    public void close() {
        try {
            System.err.println("CLOSE");
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < cntQuery; ++i) {
            System.err.println(i);
            if (reconnectAfterQuery) {
                try {
                    socket.connect(new InetSocketAddress(host, port));

                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                sendQuery();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(timeBetweenQuery);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
        workingTime = System.currentTimeMillis() - beginTime;
    }

    public void sendQuery() throws IOException {
        ArrayProto.Array.Builder arrayBuilder = ArrayProto.Array.newBuilder();

        for (int i = 0; i < arraySize; ++i) {
            arrayBuilder = arrayBuilder.addData(rnd.nextInt());
        }

        ArrayProto.Array array = arrayBuilder.build();

        System.err.println("print info");
        dos.writeInt(array.getSerializedSize());
        dos.write(array.toByteArray());
        dos.flush();

        byte[] resArray = new byte[dis.readInt()];
        dis.readFully(resArray);

        array = ArrayProto.Array.parseFrom(resArray);
    }
}
