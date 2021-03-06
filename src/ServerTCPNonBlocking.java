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

public class ServerTCPNonBlocking extends ServerTCP {
    private ServerSocketChannel serverSocketChannel;
    private Map<SocketChannel, ClientContext> context = new HashMap<>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    private Selector selector;

    public ServerTCPNonBlocking(int port) {
        super(port);
    }

    private class ClientContext {
        private static final int WAITING_SIZE_STATE = 0;
        private static final int WAITING_DATA_STATE = 1;

        private ByteBuffer sizeBuffer;
        private ByteBuffer inBuffer;
        private ByteBuffer outBuffer;

        private int state;
        private int size;

        private SocketChannel socketChannel;
    }

    @Override
    protected void catchSocket() throws IOException {

    }

    @Override
    public void start() throws IOException {
        selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        new Thread(() -> {
            try {
                work();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void work() throws IOException {
        while (serverSocketChannel.isOpen()) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null) {
                        ClientContext clientContext = new ClientContext();

                        clientContext.sizeBuffer = ByteBuffer.allocate(4);
                        clientContext.sizeBuffer.clear();
                        clientContext.state = ClientContext.WAITING_SIZE_STATE;
                        clientContext.socketChannel = socketChannel;

                        context.put(socketChannel, clientContext);

                        socketChannel.configureBlocking(false);
                        socketChannel.socket().setTcpNoDelay(true);
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                }
                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buf = context.get(socketChannel).outBuffer;

                    if (buf != null && buf.position() != buf.limit()) {
                        writeFromBuffer(socketChannel, buf);
                    }
                }
                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel)key.channel();

                    ClientContext clientContext = context.get(socketChannel);
                    if (clientContext.state == ClientContext.WAITING_SIZE_STATE) {
                        readToBuffer(clientContext.socketChannel, clientContext.sizeBuffer);

                        if (clientContext.sizeBuffer.position() == 4) {
                            clientContext.sizeBuffer.flip();
                            clientContext.size = clientContext.sizeBuffer.getInt();

                            clientContext.state = ClientContext.WAITING_DATA_STATE;

                            clientContext.inBuffer = ByteBuffer.allocate(clientContext.size);
                        }
                    }
                    if (clientContext.state == ClientContext.WAITING_DATA_STATE) {
                        readToBuffer(socketChannel, clientContext.inBuffer);

                        if (clientContext.inBuffer.position() == clientContext.size) {
                            clientContext.inBuffer.flip();

                            byte[] byteArray = new byte[clientContext.size];
                            clientContext.inBuffer.get(byteArray);

                            ArrayProto.Array array = ArrayProto.Array.parseFrom(byteArray);

                            threadPool.submit(new sortArrayTask(array, clientContext));
                        }

                    }
                }
                keyIterator.remove();
            }
        }
    }

    private void readToBuffer(SocketChannel socketChannel, ByteBuffer buf) throws IOException {
        try {
            int bytesRead = socketChannel.read(buf);

            while (bytesRead != -1 && buf.position() < buf.limit()) {
                bytesRead = socketChannel.read(buf);
            }
        } catch (IOException e) {
            socketChannel.finishConnect();
            socketChannel.close();

        }
    }

    private void writeFromBuffer(SocketChannel socketChannel, ByteBuffer buf) throws IOException {
        int bytesWrite = 0;

        while (bytesWrite != -1 && buf.position() < buf.limit() && buf.hasRemaining()) {
            bytesWrite = socketChannel.write(buf);
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

    private class sortArrayTask implements Runnable {
        ArrayProto.Array array;
        ClientContext context;

        private sortArrayTask(ArrayProto.Array array, ClientContext context) {
            this.array = array;
            this.context = context;
        }

        @Override
        public void run() {
            long BeginClientTime = System.currentTimeMillis();

            timeForTask.addAndGet(-System.currentTimeMillis());
            countOfTask.incrementAndGet();

            array = sort(array);

            timeForTask.addAndGet(System.currentTimeMillis());

            if (context.outBuffer != null && context.outBuffer.hasRemaining()) {
                throw new AssertionError();
            }
            int serializedSize = array.getSerializedSize();
            ByteBuffer outBuffer = ByteBuffer.allocate(serializedSize + 4);
            outBuffer.clear();
            outBuffer.putInt(serializedSize);
            outBuffer.put(array.toByteArray());

            outBuffer.flip();

            context.sizeBuffer.clear();
            context.inBuffer.clear();
            context.outBuffer = outBuffer;
            context.state = ClientContext.WAITING_SIZE_STATE;

            timeForClient.addAndGet(-BeginClientTime);
            timeForClient.addAndGet(System.currentTimeMillis());
        }
    }
}
