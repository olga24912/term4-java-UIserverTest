import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Integer.max;

public class GraphPanel extends JPanel {
    private ArrayList<Point> points = new ArrayList<>();

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
        System.err.println("new Points");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);

        int startX = 0;
        int startY = getHeight() - 1;

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

        System.err.println("Finish " + finish + " start " + start);

        for (int i = 1; i < points.size(); ++i) {
            int oldX = points.get(i - 1).getX(), oldY = points.get(i - 1).getY();
            int newX = points.get(i).getX(), newY = points.get(i).getY();

            System.err.println( "Old x : " + oldX + " NewX " + newX);

            oldX -= start;
            oldX = getWidth()*oldX/(finish - start);
            oldX += startX;

            oldY = getHeight() - getHeight() * oldY/maxY;

            newX -= start;
            newX = getWidth()*newX/(finish - start);
            newX += startX;

            newY = getHeight() - getHeight() * newY/maxY;

            System.err.println("print Line " + oldX + " "  + oldY + " " + newX + " " + newY);
            g.drawLine(oldX, oldY, newX, newY);
        }
    }
}
