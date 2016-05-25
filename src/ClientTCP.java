import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientTCP extends Client {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean reconnectAfterQuery = false;

    public ClientTCP(String host, int port, int arraySize, int cntQuery, int timeBetweenQuery,
                     boolean reconnectAfterQuery) throws IOException {
        super(host, port, arraySize, cntQuery, timeBetweenQuery);

        socket = new Socket();
        if (!reconnectAfterQuery) {
            socket.connect(new InetSocketAddress(host, port));

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }
        this.reconnectAfterQuery = reconnectAfterQuery;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void sendQuery() throws IOException {
        if (reconnectAfterQuery) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, port));

                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayProto.Array array = buildArray();

        dos.writeInt(array.getSerializedSize());
        dos.write(array.toByteArray());
        dos.flush();

        int resLength = dis.readInt();
        byte[] resArray = new byte[resLength];
        dis.readFully(resArray);

        array = ArrayProto.Array.parseFrom(resArray);

        if (reconnectAfterQuery) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
