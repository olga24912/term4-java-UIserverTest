import com.google.protobuf.InvalidProtocolBufferException;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerUDPThreadPool extends ServerUDP {
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    protected ServerUDPThreadPool(int port) {
        super(port);
    }

    @Override
    protected void processOneTask(ArrayProto.Array array, SocketAddress address) throws InvalidProtocolBufferException {
        threadPool.execute(new handlingQuery(array, address));
    }

    @Override
    public void stop() {
        super.stop();
        threadPool.shutdown();
    }
}
