import java.io.IOException;
import java.net.Socket;

public class ServerTCPThreadForClient extends ServerTCP {
    public ServerTCPThreadForClient(int port) {
        super(port);
    }

    @Override
    protected void catchSocket() throws IOException {
        while (true) {
            Socket socket = accept();
            if (socket != null) {
                (new Thread(() -> {
                    handlingQueries(socket);
                })).start();
            } else {
                return;
            }
        }
    }
}
