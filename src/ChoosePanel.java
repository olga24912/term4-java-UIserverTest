import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ChoosePanel extends JPanel implements PropertyChangeListener, ActionListener {
    private static final int SERVER_TCP_THREAD_FOR_CLIENT_PORT = 8081;
    private static final int SERVER_TCP_ONE_THREAD_PORT = 8082;
    private static final int SERVER_TCP_NON_BLOCKING_PORT = 8083;
    private static final int SERVER_TCP_THREAD_POOL_PORT = 8084;

    private static final int SERVER_UDP_THREAD_FOR_QUERY_PORT = 8085;
    private static final int SERVER_UDP_THREAD_POOL_PORT = 8086;

    private static String tcpNewThreadForClientString = "TCP. New thread for client";
    private static String tcpCashedThreadPoolForClientString = "TCP. New cashed thread pool for client";
    private static String tcpNIOString = "TCP. NIO processing. Query in thread pool";
    private static String tcpNewConnectOnQueryString = "TCP. New connect for query";
    private static String udpNewThreadString = "UDP. New thread for query";
    private static String udpThreadPoolString = "UDP. Thread pool for query";

    private static String countOfElemN = "(N)Count of element in array: ";
    private static String countOfClientM = "(M)Count of working clients in same moment: ";
    private static String deltaBetweenQuery = "(delta)Time between client query: ";
    private static String countOfQuery = "(X)Count of query from one client: ";

    private static String beginOfLimitString = "Minimal value of chosen parameter";
    private static String endOfLimitString = "Maximal value of chosen parameter";
    private static String stepString = "step of change parameter";

    private static final String Y_STRING = "time, ms";
    private static final String X_N_STRING = "N, array size";
    private static final String X_M_STRING = "M, clients count";
    private static final String X_DELTA_STRING = "delta, between query time";

    private GraphPanel taskTimeGraphPanel, clientTimeGraphPanel, averageTimeGraphPanel;

    private JRadioButton nButton;
    private JRadioButton mButton;
    private JRadioButton deltaButton;
    private JLabel xLabel;

    private JLabel beginLimitLabel;
    private JLabel endLimitLabel;
    private JLabel stepLabel;

    private JFormattedTextField nField;
    private JFormattedTextField mField;
    private JFormattedTextField deltaField;
    private JFormattedTextField xField;

    private JFormattedTextField beginLimitField;
    private JFormattedTextField endLimitField;
    private JFormattedTextField stepField;

    private JButton testButton;
    private static String testString = "TEST";

    private String currentArchitecture = tcpNewThreadForClientString;
    private String currentParametr = countOfElemN;
    private int beginLimit = 0;
    private int endLimit = 0;
    private int step = 1;

    private int xCountOfQuery = 0;
    private int nCountOfElem = 0;
    private int mCountOfClient = 0;
    private int deltaTimeBetweenQuery = 0;
    private String serverHost = "localhost";

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;


    public ChoosePanel() {
        super(new BorderLayout());

        nButton = new JRadioButton(countOfElemN);
        mButton = new JRadioButton(countOfClientM);
        deltaButton = new JRadioButton(deltaBetweenQuery);
        xLabel = new JLabel(countOfQuery);

        beginLimitLabel = new JLabel(beginOfLimitString);
        endLimitLabel = new JLabel(endOfLimitString);
        stepLabel = new JLabel(stepString);

        nField = new JFormattedTextField(NumberFormat.getNumberInstance());
        nField.setColumns(5);
        nField.addPropertyChangeListener("value", this);

        mField = new JFormattedTextField(NumberFormat.getNumberInstance());
        mField.setColumns(5);
        mField.addPropertyChangeListener("value", this);

        deltaField = new JFormattedTextField(NumberFormat.getNumberInstance());
        deltaField.setColumns(5);
        deltaField.addPropertyChangeListener("value", this);

        xField = new JFormattedTextField(NumberFormat.getNumberInstance());
        xField.setColumns(5);
        xField.addPropertyChangeListener("value", this);

        beginLimitField = new JFormattedTextField(NumberFormat.getNumberInstance());
        beginLimitField.setColumns(5);
        beginLimitField.addPropertyChangeListener("value", this);

        endLimitField = new JFormattedTextField(NumberFormat.getNumberInstance());
        endLimitField.setColumns(5);
        endLimitField.addPropertyChangeListener("value", this);

        stepField = new JFormattedTextField(NumberFormat.getNumberInstance());
        stepField.setColumns(5);
        stepField.addPropertyChangeListener("value", this);

        nButton.setActionCommand(countOfElemN);
        mButton.setActionCommand(countOfClientM);
        deltaButton.setActionCommand(deltaBetweenQuery);
        xLabel.setLabelFor(xField);
        beginLimitLabel.setLabelFor(beginLimitField);
        endLimitLabel.setLabelFor(endLimitField);
        stepLabel.setLabelFor(stepField);


        ButtonGroup changingGroup = new ButtonGroup();
        changingGroup.add(nButton);
        changingGroup.add(mButton);
        changingGroup.add(deltaButton);

        JRadioButton tcpNewThreadForClientButton = new JRadioButton(tcpNewThreadForClientString);
        tcpNewThreadForClientButton.setActionCommand(tcpNewThreadForClientString);

        JRadioButton tcpCashedThreadPoolForClientButton = new JRadioButton(tcpCashedThreadPoolForClientString);
        tcpCashedThreadPoolForClientButton.setActionCommand(tcpCashedThreadPoolForClientString);

        JRadioButton tcpNIOButton = new JRadioButton(tcpNIOString);
        tcpNIOButton.setActionCommand(tcpNIOString);

        JRadioButton tcpNewConnectOnQueryButton = new JRadioButton(tcpNewConnectOnQueryString);
        tcpNewConnectOnQueryButton.setActionCommand(tcpNewConnectOnQueryString);

        JRadioButton udpNewThreadButton = new JRadioButton(udpNewThreadString);
        udpNewThreadButton.setActionCommand(udpNewThreadString);

        JRadioButton udpThreadPoolButton = new JRadioButton(udpThreadPoolString);
        udpThreadPoolButton.setActionCommand(udpThreadPoolString);

        tcpNewThreadForClientButton.addActionListener(this);
        tcpCashedThreadPoolForClientButton.addActionListener(this);
        tcpNIOButton.addActionListener(this);
        tcpNewConnectOnQueryButton.addActionListener(this);
        udpNewThreadButton.addActionListener(this);
        udpThreadPoolButton.addActionListener(this);

        nButton.addActionListener(this);
        mButton.addActionListener(this);
        deltaButton.addActionListener(this);

        ButtonGroup group = new ButtonGroup();
        group.add(tcpNewThreadForClientButton);
        group.add(tcpCashedThreadPoolForClientButton);
        group.add(tcpNIOButton);
        group.add(tcpNewConnectOnQueryButton);
        group.add(udpNewThreadButton);
        group.add(udpThreadPoolButton);

        testButton = new JButton(testString);
        testButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        testButton.setHorizontalTextPosition(AbstractButton.LEADING);
        testButton.setActionCommand(testString);

        testButton.addActionListener(this);

        JPanel radioButtonPane = new JPanel(new GridLayout(0, 1));
        radioButtonPane.add(tcpNewThreadForClientButton);
        radioButtonPane.add(tcpCashedThreadPoolForClientButton);
        radioButtonPane.add(tcpNIOButton);
        radioButtonPane.add(tcpNewConnectOnQueryButton);
        radioButtonPane.add(udpNewThreadButton);
        radioButtonPane.add(udpThreadPoolButton);

        JPanel labelPane = new JPanel(new GridLayout(0, 1));
        labelPane.add(nButton);
        labelPane.add(mButton);
        labelPane.add(deltaButton);
        labelPane.add(xLabel);
        labelPane.add(beginLimitLabel);
        labelPane.add(endLimitLabel);
        labelPane.add(stepLabel);

        JPanel fieldPane = new JPanel(new GridLayout(0, 1));
        fieldPane.add(nField);
        fieldPane.add(mField);
        fieldPane.add(deltaField);
        fieldPane.add(xField);
        fieldPane.add(beginLimitField);
        fieldPane.add(endLimitField);
        fieldPane.add(stepField);

        JPanel testButtonPane = new JPanel(new GridLayout(0, 1));
        testButtonPane.add(testButton);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));
        add(labelPane, BorderLayout.CENTER);
        add(fieldPane, BorderLayout.LINE_END);
        add(radioButtonPane, BorderLayout.LINE_START);
        add(testButtonPane, BorderLayout.AFTER_LAST_LINE);
    }

    public void setTaskTimeGraphPanel(GraphPanel graphPanel) {
        taskTimeGraphPanel = graphPanel;
    }

    public void setClientTimeGraphPanel(GraphPanel clientTimeGraphPanel) {
        this.clientTimeGraphPanel = clientTimeGraphPanel;
    }

    public void setAverageTimeGraphPanel(GraphPanel averageTimeGraphPanel) {
        this.averageTimeGraphPanel = averageTimeGraphPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (((JFormattedTextField)source).getValue() == null) {
            return;
        }
        if (source == beginLimitField) {
            beginLimit = ((Number)beginLimitField.getValue()).intValue();
        } else if (source == endLimitField) {
            endLimit = ((Number)endLimitField.getValue()).intValue();
        } else if (source == stepField) {
            step = ((Number)stepField.getValue()).intValue();
        } else if (source == xField) {
            xCountOfQuery = ((Number)xField.getValue()).intValue();
        } else if (source == nField) {
            nCountOfElem = ((Number)nField.getValue()).intValue();
        } else if (source == mField) {
            mCountOfClient = ((Number)mField.getValue()).intValue();
        } else if (source == deltaField) {
            deltaTimeBetweenQuery = ((Number)deltaField.getValue()).intValue();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (testString.equals(e.getActionCommand())) {
            testServer();
        } else if (tcpNewThreadForClientString.equals(e.getActionCommand())) {
            currentArchitecture = tcpNewThreadForClientString;
        } else if (tcpNewConnectOnQueryString.equals(e.getActionCommand())) {
            currentArchitecture = tcpNewConnectOnQueryString;
        } else if (tcpCashedThreadPoolForClientString.equals(e.getActionCommand())) {
            currentArchitecture = tcpCashedThreadPoolForClientString;
        } else if (tcpNIOString.equals(e.getActionCommand())) {
            currentArchitecture = tcpNIOString;
        } else if (udpNewThreadString.equals(e.getActionCommand())) {
            currentArchitecture = udpNewThreadString;
        } else if (udpThreadPoolString.equals(e.getActionCommand())) {
            currentArchitecture = udpThreadPoolString;
        } else if (countOfElemN.equals(e.getActionCommand())) {
            nField.setEnabled(false);
            mField.setEnabled(true);
            deltaField.setEnabled(true);

            currentParametr = countOfElemN;
        } else if (countOfClientM.equals(e.getActionCommand())) {
            mField.setEnabled(false);
            nField.setEnabled(true);
            deltaField.setEnabled(true);

            currentParametr = countOfClientM;
        } else if (deltaBetweenQuery.equals(e.getActionCommand())) {
            deltaField.setEnabled(false);
            nField.setEnabled(true);
            mField.setEnabled(true);

            currentParametr = deltaBetweenQuery;
        }
    }

    private int getPort() {
        if (currentArchitecture.equals(tcpNewThreadForClientString)) {
            return SERVER_TCP_THREAD_FOR_CLIENT_PORT;
        } else if (currentArchitecture.equals(tcpCashedThreadPoolForClientString)) {
            return SERVER_TCP_THREAD_POOL_PORT;
        } else if (currentArchitecture.equals(tcpNewConnectOnQueryString)) {
            return SERVER_TCP_ONE_THREAD_PORT;
        } else if (currentArchitecture.equals(tcpNIOString)) {
            return SERVER_TCP_NON_BLOCKING_PORT;
        } else if (currentArchitecture.equals(udpNewThreadString)) {
            return SERVER_UDP_THREAD_FOR_QUERY_PORT;
        } else if (currentArchitecture.equals(udpThreadPoolString)) {
            return SERVER_UDP_THREAD_POOL_PORT;
        }

        throw new NotImplementedException();
    }

    private Client getClient(int n, int x, int delta) throws IOException {
        if (currentArchitecture.equals(tcpNewThreadForClientString)) {
            return new ClientTCP(serverHost, SERVER_TCP_THREAD_FOR_CLIENT_PORT, n, x, delta, false);
        } else if (currentArchitecture.equals(tcpCashedThreadPoolForClientString)) {
            return new ClientTCP(serverHost, SERVER_TCP_THREAD_POOL_PORT, n, x, delta, false);
        } else if (currentArchitecture.equals(tcpNewConnectOnQueryString)) {
            return new ClientTCP(serverHost, SERVER_TCP_ONE_THREAD_PORT, n, x, delta, true);
        } else if (currentArchitecture.equals(tcpNIOString)) {
            return new ClientTCP(serverHost, SERVER_TCP_NON_BLOCKING_PORT, n, x, delta, false);
        } else if (currentArchitecture.equals(udpNewThreadString)) {
            return new ClientUDP(serverHost, SERVER_UDP_THREAD_FOR_QUERY_PORT, n, x, delta);
        } else if (currentArchitecture.equals(udpThreadPoolString)) {
            return new ClientUDP(serverHost, SERVER_UDP_THREAD_POOL_PORT, n, x, delta);
        }

        throw new NotImplementedException();
    }

    private void testServer() {
        ArrayList<Point> taskTimeStatistic = new ArrayList<>();
        ArrayList<Point> clientTimeStatistic = new ArrayList<>();
        ArrayList<Point> averageTimeStatistic = new ArrayList<>();

        taskTimeGraphPanel.setyString(Y_STRING);
        clientTimeGraphPanel.setyString(Y_STRING);
        averageTimeGraphPanel.setyString(Y_STRING);

        if (currentParametr.equals(countOfElemN)) {
            taskTimeGraphPanel.setxString(X_N_STRING);
            clientTimeGraphPanel.setxString(X_N_STRING);
            averageTimeGraphPanel.setxString(X_N_STRING);

            for (int n = beginLimit; n < endLimit; n += step) {
                ArrayList<Integer> res = testServerWithParameters(n, mCountOfClient, deltaTimeBetweenQuery);

                taskTimeStatistic.add(new Point(n, res.get(0)));
                clientTimeStatistic.add(new Point(n, res.get(1)));
                averageTimeStatistic.add(new Point(n, res.get(2)));
            }

        } else if (currentParametr.equals(countOfClientM)) {
            taskTimeGraphPanel.setxString(X_M_STRING);
            clientTimeGraphPanel.setxString(X_M_STRING);
            averageTimeGraphPanel.setxString(X_M_STRING);

            for (int m = beginLimit; m < endLimit; m += step) {
                ArrayList<Integer> res = testServerWithParameters(nCountOfElem, m, deltaTimeBetweenQuery);

                taskTimeStatistic.add(new Point(m, res.get(0)));
                clientTimeStatistic.add(new Point(m, res.get(1)));
                averageTimeStatistic.add(new Point(m, res.get(2)));
            }
        } else if (currentParametr.equals(deltaBetweenQuery)) {
            taskTimeGraphPanel.setxString(X_DELTA_STRING);
            clientTimeGraphPanel.setxString(X_DELTA_STRING);
            averageTimeGraphPanel.setxString(X_DELTA_STRING);

            for (int delta = beginLimit; delta < endLimit; delta += step) {
                ArrayList<Integer> res = testServerWithParameters(nCountOfElem, mCountOfClient, delta);

                taskTimeStatistic.add(new Point(delta, res.get(0)));
                clientTimeStatistic.add(new Point(delta, res.get(1)));
                averageTimeStatistic.add(new Point(delta, res.get(2)));
            }

        }



        taskTimeGraphPanel.setPoints(taskTimeStatistic);
        clientTimeGraphPanel.setPoints(clientTimeStatistic);
        averageTimeGraphPanel.setPoints(averageTimeStatistic);

        writeInfoToFiles(taskTimeStatistic, clientTimeStatistic, averageTimeStatistic);
    }

    private void writeInfoToFiles(ArrayList<Point> taskTimeStatistic,
                                  ArrayList<Point> clientTimeStatistic,
                                  ArrayList<Point> averageTimeStatistic) {
        PrintWriter writer = null;
        long time = System.currentTimeMillis();

        try {
            writer = new PrintWriter("Info" + time);
            writer.println("Server: " + currentArchitecture);
            writer.println(currentParametr + " in limit from " + beginLimit + " to " + endLimit);
            if (!Objects.equals(currentParametr, countOfElemN)) writer.println("N: " + nCountOfElem);
            if (!Objects.equals(currentParametr, countOfClientM))writer.println("M: " + mCountOfClient);
            if (!Objects.equals(currentParametr, deltaBetweenQuery))writer.println("delta: " + deltaTimeBetweenQuery);
            writer.println("X: " + xCountOfQuery);
            writer.close();

            writer = new PrintWriter("taskTimeStatistic" + time);
            for (Point aTaskTimeStatistic : taskTimeStatistic) {
                writer.println(aTaskTimeStatistic.getX() + " " + aTaskTimeStatistic.getY());
            }
            writer.close();


            writer = new PrintWriter("clientTimeStatistic" + time);
            for (Point aClientTimeStatistic : clientTimeStatistic) {
                writer.println(aClientTimeStatistic.getX() + " " + aClientTimeStatistic.getY());
            }
            writer.close();


            writer = new PrintWriter("averageTimeStatistic" + time);
            for (Point anAverageTimeStatistic : averageTimeStatistic) {
                writer.println(anAverageTimeStatistic.getX() + " " + anAverageTimeStatistic.getY());
            }
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    ArrayList<Integer> testServerWithParameters(int n, int m, int delta) {
        ArrayList<Integer> res = new ArrayList<>();
        res.add(0);
        res.add(0);
        res.add(0);


        int server_id = getPort();
        try {
            dos.writeUTF("START");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.writeInt(server_id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Client> clients = new ArrayList<>();

        for (int i = 0; i < m; ++i) {
            Client client = null;
            try {
                client = getClient(n, xCountOfQuery, delta);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            clients.add(client);

            assert client != null;
            Thread newThread = new Thread(client);

            threads.add(newThread);
            newThread.start();
        }

        System.err.println("Create client");

        for (int i = 0; i < m; ++i) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.err.println("Finish");

        try {
            dos.writeUTF("STOP");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos.writeInt(server_id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long clintTime = 0;
        for (int i = 0; i < m; ++i) {
            clintTime += clients.get(i).getWorkingTime();
        }

        res.set(2,(int)clintTime/m);
        try {
            dos.writeUTF("GET_CLIENT");
            dos.writeInt(server_id);
            res.set(1, (int)dis.readLong());
            dos.writeUTF("GET_TASK");
            dos.writeInt(server_id);
            res.set(0, (int)dis.readLong());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
        try {
            socket = new Socket(serverHost, 8080);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
