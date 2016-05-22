import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ClientUDP extends Client {
    private DatagramSocket datagramSocket;
    private byte[] buffer;

    public ClientUDP(String host, int port, int arraySize, int cntQuery, int timeBetweenQuery) {
        super(host, port, arraySize, cntQuery, timeBetweenQuery);

        buffer = new byte[arraySize*4 + 4];
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendQuery() throws IOException {
        ArrayProto.Array array = buildArray();

        ByteBuffer byteBuffer = ByteBuffer.allocate(array.getSerializedSize() + 4);
        byteBuffer.putInt(array.getSerializedSize());
        byteBuffer.put(array.toByteArray());

        DatagramPacket packet = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length,
                new InetSocketAddress(host, port));

        datagramSocket.send(packet);

        DatagramPacket answerPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(answerPacket);
    }

    @Override
    public void close() {
        datagramSocket.close();
    }
}
