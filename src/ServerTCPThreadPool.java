import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTCPThreadPool extends ServerTCP {
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public ServerTCPThreadPool(int port) {
        super(port);
    }

    @Override
    protected void catchSocket() throws IOException {
        while (true) {
            Socket socket = accept();
            if (socket != null) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        handlingQueries(socket);
                    }
                });
            } else {
                return;
            }
        }
    }
}
