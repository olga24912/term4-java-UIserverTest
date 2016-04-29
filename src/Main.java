import javax.swing.*;
import java.awt.*;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Server Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setLayout(new GridLayout(0, 2));
        frame.add(new ChoosePanel());
        frame.add(new GraphPanel());
        frame.add(new GraphPanel());
        frame.add(new GraphPanel());

        frame.pack();
        frame.setVisible(true);
    }
}
