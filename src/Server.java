import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Server {
    protected final int port;

    protected volatile AtomicLong timeForTask = new AtomicLong();
    protected volatile AtomicLong timeForClient = new AtomicLong();

    protected volatile AtomicInteger countOfTask = new AtomicInteger();

    protected Server(int port) {
        this.port = port;
    }

    public abstract void start() throws IOException;

    public abstract void stop();

    public long getTimeForTask() {
        return timeForTask.get()/countOfTask.get();
    }

    public long getTimeForClient() {
        return timeForClient.get()/countOfTask.get();
    }
}
