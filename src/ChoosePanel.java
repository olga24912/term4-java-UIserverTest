import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

public class ChoosePanel extends JPanel implements PropertyChangeListener {
    private static String countOfElemN = "Count of element in array(N): ";
    private static String countOfClientM = "Count of working clients in same moment(M): ";
    private static String deltaBetweenQuery = "Time between client query(delta): ";
    private static String countOfQuery = "Count of query from one client(X): ";

    private JLabel nLabel;
    private JLabel mLabel;
    private JLabel deltaLabel;
    private JLabel xLabel;

    private JFormattedTextField nField;
    private JFormattedTextField mField;
    private JFormattedTextField deltaField;
    private JFormattedTextField xField;

    public ChoosePanel() {
        super(new BorderLayout());

        nLabel = new JLabel(countOfElemN);
        mLabel = new JLabel(countOfClientM);
        deltaLabel = new JLabel(deltaBetweenQuery);
        xLabel = new JLabel(countOfQuery);

        nField = new JFormattedTextField(NumberFormat.getNumberInstance());
        nField.setColumns(10);

        mField = new JFormattedTextField(NumberFormat.getNumberInstance());
        mField.setColumns(10);

        deltaField = new JFormattedTextField(NumberFormat.getNumberInstance());
        deltaField.setColumns(10);

        xField = new JFormattedTextField(NumberFormat.getNumberInstance());
        xField.setColumns(10);

        nLabel.setLabelFor(nField);
        mLabel.setLabelFor(mField);
        deltaLabel.setLabelFor(deltaField);
        xLabel.setLabelFor(xField);

        JPanel labelPane = new JPanel(new GridLayout(0, 1));
        labelPane.add(nLabel);
        labelPane.add(mLabel);
        labelPane.add(deltaLabel);
        labelPane.add(xLabel);

        JPanel fieldPane = new JPanel(new GridLayout(0, 1));
        fieldPane.add(nField);
        fieldPane.add(mField);
        fieldPane.add(deltaField);
        fieldPane.add(xField);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(labelPane, BorderLayout.CENTER);
        add(fieldPane, BorderLayout.LINE_END);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
