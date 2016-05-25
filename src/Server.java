import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    protected ArrayProto.Array sort(ArrayProto.Array array) {
        ArrayProto.Array res;
        List<Integer> list = array.getDataList();

        ArrayList<Integer> arrayToSort = new ArrayList<>();

        for (int i = 0; i < list.size(); ++i) {
            arrayToSort.add(list.get(i));
        }

        for (int i = 0; i < arrayToSort.size(); ++i) {
            for (int j = 1; j < arrayToSort.size(); ++j) {
                if (arrayToSort.get(j - 1) > arrayToSort.get(j)) {
                    int k = arrayToSort.get(j - 1);
                    arrayToSort.set(j - 1, arrayToSort.get(j));
                    arrayToSort.set(j, k);
                }
            }
        }

        res = ArrayProto.Array.newBuilder().addAllData(arrayToSort).build();
        return res;
    }
}
