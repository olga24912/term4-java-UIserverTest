import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class Canvas extends JPanel implements DefaultMouseListener {
    private static final int EPSILON = 5;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> stack = new ArrayList<>();

    private Point mousePosition;

    Canvas() {
        addMouseListener(this);
        popupMenu.add(buildPopupMenuItem());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                points.add(new Point(e.getX(), e.getY()));
                stack.clear();
                repaint();
                break;
            case MouseEvent.BUTTON3:
                mousePosition = new Point(e.getX(), e.getY());
                popupMenu.show(this, e.getX(), e.getY());
                break;
            default:
        }
    }

    public void calculate() {
    }

    public void clear() {
        points.clear();
        stack.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
    }

    private JMenuItem buildPopupMenuItem() {
        // Return JMenuItem called "Remove point"
        // Point should be removed after click

        JMenuItem item = new JMenuItem("Remove point");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        return item;
    }
}
