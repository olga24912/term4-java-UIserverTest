import com.google.protobuf.InvalidProtocolBufferException;

import java.net.SocketAddress;

public class ServerUDPThreadForQuery extends ServerUDP {
    protected ServerUDPThreadForQuery(int port) {
        super(port);
    }

    @Override
    protected void processOneTask(ArrayProto.Array array, SocketAddress address) throws InvalidProtocolBufferException {
        new Thread( new handlingQuery(array, address)).start();
    }
}
