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
    private static String tcpNewThreadForClientString = "TCP protocol. New thread for client";
    private static String tcpCashedThreadPoolForClientString = "TCP protocol. New cashed thread pool for client";
    private static String tcpNIOString = "TCP protocol. NIO processing. Query in thread pool";
    private static String tcpNewConnectOnQueryString = "TCP protocol. New connect for query";
    private static String udpNewThreadString = "UDP protocol. New thread for query";
    private static String udpThreadPoolString = "UDP protocol. Thread pool for query";

    private static String countOfElemN = "Count of element in array(N): ";
    private static String countOfClientM = "Count of working clients in same moment(M): ";
    private static String deltaBetweenQuery = "Time between client query(delta): ";
    private static String countOfQuery = "Count of query from one client(X): ";

    private GraphPanel taskTimeGraphPanel, clientTimeGraphPanel, averageTimeGraphPanel;

    private JRadioButton nButton;
    private JRadioButton mButton;
    private JRadioButton deltaButton;
    private JLabel xLabel;

    private JFormattedTextField nField;
    private JFormattedTextField mField;
    private JFormattedTextField deltaField;
    private JFormattedTextField xField;

    private JButton testButton;
    private static String testString = "TEST";

    private String currentArchitecture = tcpNewThreadForClientString;

    public ChoosePanel() {
        super(new BorderLayout());

        nButton = new JRadioButton(countOfElemN);
        mButton = new JRadioButton(countOfClientM);
        deltaButton = new JRadioButton(deltaBetweenQuery);
        xLabel = new JLabel(countOfQuery);

        nField = new JFormattedTextField(NumberFormat.getNumberInstance());
        nField.setColumns(10);

        mField = new JFormattedTextField(NumberFormat.getNumberInstance());
        mField.setColumns(10);

        deltaField = new JFormattedTextField(NumberFormat.getNumberInstance());
        deltaField.setColumns(10);

        xField = new JFormattedTextField(NumberFormat.getNumberInstance());
        xField.setColumns(10);

        nButton.setActionCommand(countOfElemN);
        mButton.setActionCommand(countOfClientM);
        deltaButton.setActionCommand(deltaBetweenQuery);
        xLabel.setLabelFor(xField);


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

        JPanel fieldPane = new JPanel(new GridLayout(0, 1));
        fieldPane.add(nField);
        fieldPane.add(mField);
        fieldPane.add(deltaField);
        fieldPane.add(xField);

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
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.err.println("action");
        if (testString.equals(e.getActionCommand())) {
            testTime();
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
        }
    }

    private void testTime() {
        if (currentArchitecture.equals(tcpNewThreadForClientString)) {
            testTaskTimeTcpNewThreadForClient();
        } else if (currentArchitecture.equals(tcpNewThreadForClientString)) {

        } else if (currentArchitecture.equals(tcpCashedThreadPoolForClientString)) {

        } else if (currentArchitecture.equals(tcpNIOString)) {

        } else if (currentArchitecture.equals(udpNewThreadString)) {

        } else if (currentArchitecture.equals(udpThreadPoolString)) {

        }
    }

    private void testTaskTimeTcpNewThreadForClient() {
        int N = 100000000, M = 1, X = 1, delta = 1;

        ArrayList<Point> taskTimeStatistic = new ArrayList<>();

        for (int n = 0; n < N; n += N/10) {
            ServerTCPThreadForClient server = new ServerTCPThreadForClient(8080);
            try {
                server.start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Client client = null;
            try {
                client = new Client("localhost", 8080, n, X, delta);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                assert client != null;
                client.run();
                System.err.println("fin");
                server.stop();

                System.err.println("time: "  +  server.getTimeForTask() );
                taskTimeStatistic.add(new Point(n, (int) server.getTimeForTask()));
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        taskTimeGraphPanel.setPoints(taskTimeStatistic);
    }
}
