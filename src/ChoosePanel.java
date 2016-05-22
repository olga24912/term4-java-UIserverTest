import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ChoosePanel extends JPanel implements PropertyChangeListener, ActionListener {
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
        System.err.println("action");
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

    private Server getServer(int port) {
        if (currentArchitecture.equals(tcpNewThreadForClientString)) {
            return new ServerTCPThreadForClient(port);
        } else if (currentArchitecture.equals(tcpCashedThreadPoolForClientString)) {
            return new ServerTCPThreadPool(port);
        } else if (currentArchitecture.equals(tcpNewConnectOnQueryString)) {
            return new ServerTCPOneThread(port);
        } else if (currentArchitecture.equals(tcpNIOString)) {

        } else if (currentArchitecture.equals(udpNewThreadString)) {
            return new ServerUDPThreadForQuery(port);
        } else if (currentArchitecture.equals(udpThreadPoolString)) {

        }

        throw new NotImplementedException();
    }

    private Client getClient(int n, int x, int delta) throws IOException {
        if (currentArchitecture.equals(tcpNewThreadForClientString)) {
            return new ClientTCP("localhost", 8080, n, x, delta, false);
        } else if (currentArchitecture.equals(tcpCashedThreadPoolForClientString)) {
            return new ClientTCP("localhost", 8080, n, x, delta, false);
        } else if (currentArchitecture.equals(tcpNewConnectOnQueryString)) {
            return new ClientTCP("localhost", 8080, n, x, delta, true);
        } else if (currentArchitecture.equals(tcpNIOString)) {
            return new ClientTCP("localhost", 8080, n, x, delta, false);
        } else if (currentArchitecture.equals(udpNewThreadString)) {
            return new ClientUDP("localhost", 8080, n, x, delta);
        } else if (currentArchitecture.equals(udpThreadPoolString)) {
            return new ClientUDP("localhost", 8080, n, x, delta);
        }

        throw new NotImplementedException();
    }

    private void testServer() {
        ArrayList<Point> taskTimeStatistic = new ArrayList<>();
        ArrayList<Point> clientTimeStatistic = new ArrayList<>();
        ArrayList<Point> averageTimeStatistic = new ArrayList<>();

        if (currentParametr.equals(countOfElemN)) {
            for (int n = beginLimit; n < endLimit; n += step) {
                ArrayList<Integer> res = testServerWithParameters(n, mCountOfClient, deltaTimeBetweenQuery);

                taskTimeStatistic.add(new Point(n, res.get(0)));
                clientTimeStatistic.add(new Point(n, res.get(1)));
                averageTimeStatistic.add(new Point(n, res.get(2)));
            }

            taskTimeGraphPanel.setxString(X_N_STRING);
            clientTimeGraphPanel.setxString(X_N_STRING);
            averageTimeGraphPanel.setxString(X_N_STRING);
        } else if (currentParametr.equals(countOfClientM)) {
            for (int m = beginLimit; m < endLimit; m += step) {
                ArrayList<Integer> res = testServerWithParameters(nCountOfElem, m, deltaTimeBetweenQuery);

                taskTimeStatistic.add(new Point(m, res.get(0)));
                clientTimeStatistic.add(new Point(m, res.get(1)));
                averageTimeStatistic.add(new Point(m, res.get(2)));
            }
            taskTimeGraphPanel.setxString(X_M_STRING);
            clientTimeGraphPanel.setxString(X_M_STRING);
            averageTimeGraphPanel.setxString(X_M_STRING);
        } else if (currentParametr.equals(deltaBetweenQuery)) {
            for (int delta = beginLimit; delta < endLimit; delta += step) {
                ArrayList<Integer> res = testServerWithParameters(nCountOfElem, mCountOfClient, delta);

                taskTimeStatistic.add(new Point(delta, res.get(0)));
                clientTimeStatistic.add(new Point(delta, res.get(1)));
                averageTimeStatistic.add(new Point(delta, res.get(2)));
            }

            taskTimeGraphPanel.setxString(X_DELTA_STRING);
            clientTimeGraphPanel.setxString(X_DELTA_STRING);
            averageTimeGraphPanel.setxString(X_DELTA_STRING);
        }


        taskTimeGraphPanel.setyString(Y_STRING);
        clientTimeGraphPanel.setyString(Y_STRING);
        averageTimeGraphPanel.setyString(Y_STRING);

        taskTimeGraphPanel.setPoints(taskTimeStatistic);
        clientTimeGraphPanel.setPoints(clientTimeStatistic);
        averageTimeGraphPanel.setPoints(averageTimeStatistic);
    }

    ArrayList<Integer> testServerWithParameters(int n, int m, int delta) {
        ArrayList<Integer> res = new ArrayList<>();
        res.add(0);
        res.add(0);
        res.add(0);


        Server server = getServer(8080);
        try {
            server.start();
        } catch (IOException e1) {
            e1.printStackTrace();
            return res;
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

        for (int i = 0; i < m; ++i) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        server.stop();

        long clintTime = 0;
        for (int i = 0; i < m; ++i) {
            clintTime += clients.get(i).getWorkingTime();
        }

        res.set(2,(int)clintTime/m);
        res.set(1, (int)server.getTimeForClient());
        res.set(0, (int)server.getTimeForTask());

        return res;
    }
}
