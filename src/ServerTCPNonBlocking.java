import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ServerTCPNonBlocking extends ServerCommon {
    private static final int BUFFER_SIZE = 100000000;
    private ServerSocketChannel serverSocketChannel;
    private Map<SocketChannel, ByteBuffer> buffer = new HashMap<>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public ServerTCPNonBlocking(int port) {
        super(port);
    }

    @Override
    protected void catchSocket() throws IOException {

    }

    @Override
    public void start() throws IOException {
        Selector selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverSocketChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null) {
                        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
                        buf.clear();

                        buffer.put(socketChannel, buf);

                        socketChannel.configureBlocking(false);
                        socketChannel.socket().setTcpNoDelay(true);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    ByteBuffer buf = buffer.get(socketChannel);

                    int bytesRead = socketChannel.read(buf);

                    buf.flip();

                    int bufSize = buf.getInt();
                    byte[] byteArray = new byte[bufSize];

                    int i = 0;
                    while(buf.hasRemaining()) {
                        byteArray[i] = buf.get();
                        ++i;
                    }

                    buf.clear();
                    bytesRead = socketChannel.read(buf);

                    while (bytesRead != -1) {
                        buf.flip();
                        while(buf.hasRemaining()) {
                            byteArray[i] = buf.get();
                            ++i;
                        }
                        buf.clear();
                        bytesRead = socketChannel.read(buf);
                    }

                    ArrayProto.Array array = ArrayProto.Array.parseFrom(byteArray);

                    array = ArrayProto.Array.newBuilder().
                                    addAllData(array.getDataList().stream().sorted().
                                            collect(Collectors.toList())).build();

                    buf.clear();

                    buf.putInt(array.getSerializedSize());
                    buf.put(array.toByteArray());

                    socketChannel.configureBlocking(false);
                    socketChannel.socket().setTcpNoDelay(true);
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buf = buffer.get(socketChannel);

                    if (buf.hasRemaining()) {
                        socketChannel.write(buf);
                    } else {
                        socketChannel.finishConnect();
                        socketChannel.close();
                    }
                } else {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.finishConnect();
                    socketChannel.close();
                }
                keyIterator.remove();
            }
        }
    }

    @Override
    public void stop() {
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }
}
