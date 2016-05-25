import java.io.IOException;
import java.util.Random;

public abstract class Client implements Runnable {
    protected int port;
    protected String host;

    protected int arraySize;
    protected int cntQuery;
    protected int timeBetweenQuery;
    protected Random rnd = new Random();
    protected long workingTime = 0;

    public long getWorkingTime() {
        return workingTime;
    }

    public Client(String host, int port, int arraySize, int cntQuery, int timeBetweenQuery) {
        this.arraySize = arraySize;
        this.cntQuery = cntQuery;
        this.timeBetweenQuery = timeBetweenQuery;

        this.port = port;
        this.host = host;
    }

    @Override
    public void run() {
        System.err.println("Start client " + port + " " + host);
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < cntQuery; ++i) {
            try {
                sendQuery();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(timeBetweenQuery);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
        workingTime = System.currentTimeMillis() - beginTime;
    }

    protected ArrayProto.Array buildArray() {
        ArrayProto.Array.Builder arrayBuilder = ArrayProto.Array.newBuilder();

        for (int i = 0; i < arraySize; ++i) {
            arrayBuilder = arrayBuilder.addData(rnd.nextInt());
        }

        return arrayBuilder.build();
    }

    public abstract void sendQuery() throws IOException;

    public abstract void close();
}