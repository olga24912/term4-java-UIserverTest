import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public abstract class ServerUDP extends Server {

    protected DatagramSocket datagramSocket;
    protected byte[] buffer = new byte[100000000];

    protected ServerUDP(int port) {
        super(port);
    }

    @Override
    public void start() throws IOException {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while (!datagramSocket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try {
                    datagramSocket.receive(packet);
                } catch (IOException e) {
                    return;
                }

                countOfTask.incrementAndGet();

                try {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                    int size = byteBuffer.getInt();
                    byte[] data = new byte[size];
                    byteBuffer.get(data);

                    ArrayProto.Array array = ArrayProto.Array.parseFrom(data);

                    processOneTask(array, packet.getSocketAddress());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected abstract void processOneTask(ArrayProto.Array array, SocketAddress address) throws InvalidProtocolBufferException;

    @Override
    public void stop() {
        System.err.println("CLOSE");
        datagramSocket.close();
    }

    protected class handlingQuery implements Runnable {
        ArrayProto.Array array;
        SocketAddress address;

        public handlingQuery(ArrayProto.Array array, SocketAddress address) {
            this.array = array;
            this.address = address;
        }

        @Override
        public void run() {
            long beginClientTime = System.currentTimeMillis();

            timeForTask.addAndGet(-System.currentTimeMillis());
            countOfTask.incrementAndGet();

            array = sort(array);

            timeForTask.addAndGet(System.currentTimeMillis());

            ByteBuffer byteBuffer = ByteBuffer.allocate(array.getSerializedSize() + 4);
            byteBuffer.putInt(array.getSerializedSize());
            byteBuffer.put(array.toByteArray());

            DatagramPacket packet = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length,
                    address);

            try {
                datagramSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            timeForClient.addAndGet(-beginClientTime);
            timeForClient.addAndGet(System.currentTimeMillis());
        }
    }
}
