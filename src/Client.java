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
    private Random rnd = new Random();

    public Client(String host, int port, int arraySize) throws IOException {
        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        this.arraySize = arraySize;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
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

        dis.readInt();
        for (int i = 0; i < arraySize; ++i) {
            dis.readInt();
        }
    }
}
