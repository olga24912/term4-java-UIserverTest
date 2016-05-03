import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Integer.max;

public class GraphPanel extends JPanel {
    private ArrayList<Point> points = new ArrayList<>();
    private String xString = "";
    private String yString = "";

    public GraphPanel(String nameOfGraph) {
        JLabel nameOfGraphLabel = new JLabel(nameOfGraph);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));
        add(nameOfGraphLabel, BorderLayout.NORTH);
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
        System.err.println("new Points");
        repaint();
    }

    public void setxString(String xString) {
        this.xString = xString;
    }

    public void setyString(String yString) {
        this.yString = yString;
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

        g.drawLine(0, 0, 5, 5);
        g.drawLine(getWidth(), getHeight(), getWidth() - 5, getHeight() - 5);

        int stringXPos = getWidth() - 7 * xString.length();
        int stringYPos = 5;

        g.drawString(xString, stringXPos, getHeight() - 5);
        g.drawString(yString, 10, 10);

        if (points.size() == 0) {
            return;
        }

        int start = points.get(0).getX(), finish = points.get(points.size() - 1).getX();

        for (int i = 0; i < 10; ++i){
            int posX = ((finish - start)*i/10) * getWidth()/(finish - start);

            if (posX >= stringXPos) {
                break;
            }        int maxY = 1;


            g.drawLine(posX, getHeight(), posX, getHeight() - 5);
            g.drawString(String.valueOf(start + (finish - start)*i/10) ,posX, getHeight() - 5);
        }

        int maxY = 1;

        for (Point point : points) {
            maxY = max(maxY, point.getY());
        }

        for (int i = 0; i < 10; ++i) {
            int posY = (10 - i)*getHeight()/10;

            if (posY <= stringYPos) {
                break;
            }

            g.drawLine(0, posY, 5, posY);
            g.drawString(String.valueOf(i * maxY / 10), 0, posY);
        }

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
