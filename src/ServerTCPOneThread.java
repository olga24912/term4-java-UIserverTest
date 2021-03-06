import java.io.IOException;
import java.net.Socket;

public class ServerTCPOneThread extends ServerTCP {
    public ServerTCPOneThread(int port) {
        super(port);
    }

    @Override
    protected void catchSocket() throws IOException {
        while (true) {
            Socket socket = accept();
            if (socket != null) {
                handlingQueries(socket);
                socket.close();
            } else {
                return;
            }
        }
    }
}
