import javax.swing.*;
import java.awt.*;

public final class Main {
    private static String serverHost = "localhost";
    private static JFrame frame;
    private static ChoosePanel mainChoosePane;

    private Main() {
    }

    public static void main(String[] args) {
        frame = new JFrame("Server Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JMenuBar menubar = buildMenuBar();

        GraphPanel taskTimeGraphPanel = new GraphPanel("Time for one task"),
                clientTimeGraphPanel = new GraphPanel("Time for client on server"),
                averageTimeGraphPanel = new GraphPanel("Time of client working");

        mainChoosePane = new ChoosePanel();

        mainChoosePane.setTaskTimeGraphPanel(taskTimeGraphPanel);
        mainChoosePane.setClientTimeGraphPanel(clientTimeGraphPanel);
        mainChoosePane.setAverageTimeGraphPanel(averageTimeGraphPanel);

        frame.setJMenuBar(menubar);

        frame.setLayout(new GridLayout(0, 2));

        frame.add(mainChoosePane);
        frame.add(taskTimeGraphPanel);
        frame.add(clientTimeGraphPanel);
        frame.add(averageTimeGraphPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static JMenuBar buildMenuBar() {
        JMenuItem itemServerHost = new JMenuItem("Choose server host");

        itemServerHost.addActionListener(e -> {
            serverHost = JOptionPane.showInputDialog(frame, "Write server host");
            mainChoosePane.setServerHost(serverHost);
        });

        JMenu menu = new JMenu("Menu");
        menu.add(itemServerHost);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        return menuBar;
    }
}
