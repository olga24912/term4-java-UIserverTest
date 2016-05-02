import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Client {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int arraySize;
    private int cntQuery;
    private int timeBetweenQuery;
    private Random rnd = new Random();

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

    public void run() throws IOException, InterruptedException {
        for (int i = 0; i < cntQuery; ++i) {
            sendQuery();
            Thread.sleep(timeBetweenQuery);
        }
        close();
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
