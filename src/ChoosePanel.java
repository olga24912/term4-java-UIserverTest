import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.NumberFormat;

public class ChoosePanel extends JPanel implements PropertyChangeListener, ActionListener {
    private static String countOfElemN = "Count of element in array(N): ";
    private static String countOfClientM = "Count of working clients in same moment(M): ";
    private static String deltaBetweenQuery = "Time between client query(delta): ";
    private static String countOfQuery = "Count of query from one client(X): ";

    private JRadioButton nButton;
    private JRadioButton mButton;
    private JRadioButton deltaButton;
    private JLabel xLabel;

    private JFormattedTextField nField;
    private JFormattedTextField mField;
    private JFormattedTextField deltaField;
    private JFormattedTextField xField;


    private static String tcpNewThreadForClientString = "TCP protocol. New thread for client";
    private static String tcpCashedThreadPoolForClientString = "TCP protocol. New cashed thread pool for client";
    private static String tcpNIOString = "TCP protocol. NIO processing. Query in thread pool";
    private static String tcpNewConnectOnQueryString = "TCP protocol. New connect for query";
    private static String udpNewThreadString = "UDP protocol. New thread for query";
    private static String udpThreadPoolString = "UDP protocol. Thread pool for query";

    private JButton testButton;
    private String testString = "TEST";

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.err.println("action");
        if (testString.equals(e.getActionCommand())) {
            ServerTCPThreadForClient server = new ServerTCPThreadForClient(8080);
            try {
                server.start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Client client = null;
            try {
                client = new Client("localhost", 8080, 10);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                assert client != null;
                client.sendQuery();
                System.err.println("fin");
                server.stop();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ;
        }
    }
}
