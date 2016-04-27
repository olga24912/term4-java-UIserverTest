import javax.swing.*;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Server Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(new ChoosePanel());

        frame.pack();
        frame.setVisible(true);
    }
}
