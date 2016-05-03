import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Client implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int arraySize;
    private int cntQuery;
    private int timeBetweenQuery;
    private Random rnd = new Random();
    private long workingTime = 0;

    public long getWorkingTime() {
        return workingTime;
    }

    public Client(String host, int port, int arraySize, int cntQuery, int timeBetweenQuery) throws IOException {
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        this.arraySize = arraySize;
        this.cntQuery = cntQuery;
        this.timeBetweenQuery = timeBetweenQuery;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < cntQuery; ++i) {
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
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < arraySize; ++i) {
            arrayList.add(rnd.nextInt());
        }

        dos.writeInt(arraySize);
        for (int i = 0; i < arraySize; ++i) {
            dos.writeInt(arrayList.get(i));
        }
        dos.flush();

        System.err.println("print info");
        dis.readInt();
        for (int i = 0; i < arraySize; ++i) {
            dis.readInt();
        }
    }
}
