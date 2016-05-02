import javax.swing.*;
import java.awt.*;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Server Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphPanel taskTimeGraphPanel = new GraphPanel("Time for one task"),
                clientTimeGraphPanel = new GraphPanel("Time for client on server"),
                averageTimeGraphPanel = new GraphPanel("Time of client working");

        ChoosePanel mainChoosePane = new ChoosePanel();

        mainChoosePane.setTaskTimeGraphPanel(taskTimeGraphPanel);
        mainChoosePane.setClientTimeGraphPanel(clientTimeGraphPanel);
        mainChoosePane.setAverageTimeGraphPanel(averageTimeGraphPanel);

        frame.setLayout(new GridLayout(0, 2));

        frame.add(mainChoosePane);
        frame.add(taskTimeGraphPanel);
        frame.add(clientTimeGraphPanel);
        frame.add(averageTimeGraphPanel);

        frame.pack();
        frame.setVisible(true);
    }
}
