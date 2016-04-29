import javax.swing.*;
import java.awt.*;
import java.util.AbstractList;
import java.util.ArrayList;

import static java.lang.Integer.max;

public class GraphPanel extends JPanel {
    private AbstractList<Point> points = new ArrayList<>();

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);

        int startX = getWidth()/10;
        int startY = 9*getHeight()/10;

        g.drawOval(startX - 1, startY - 1, 2, 2);

        g.drawLine(startX, 0, startX, getHeight());
        g.drawLine(0, startY, getWidth(), startY);

        if (points.size() == 0) {
            return;
        }

        int maxY = 0;
        int start = points.get(0).getX(), finish = points.get(points.size() - 1).getX();

        for (int i = 0; i < points.size(); ++i) {
            maxY = max(maxY, points.get(i).getY());
        }

        for (int i = 1; i < points.size(); ++i) {
            int oldX = points.get(i - 1).getX(), oldY = points.get(i - 1).getY();
            int newX = points.get(i - 1).getX(), newY = points.get(i - 1).getY();

            oldX -= start;
            oldX = (finish - start)*oldX/getWidth();
            oldX += startX;

            oldY = maxY * oldY/getHeight();

            newX -= start;
            newX = (finish - start)*newX/getWidth();
            newX += startX;

            newY = maxY * newY/getHeight();

            g.drawLine(oldX, oldY, newX, newY);
        }
    }
}
