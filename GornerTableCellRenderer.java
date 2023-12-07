//Исходный код визуализатора ячеек

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {

    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private boolean checker = false;

    public GornerTableCellRenderer() {
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
        panel.add(label);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);
        if(Double.parseDouble(formattedDouble) > 0){
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        }
        if(Double.parseDouble(formattedDouble) < 0){
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        }
        if(Double.parseDouble(formattedDouble) == 0){
            panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        }
        if (col == 1 && needle != null && needle.equals(formattedDouble)) {
            panel.setBackground(Color.RED);
        } else {
            panel.setBackground(Color.WHITE);
        }
        if (checker)
            for (Component c: panel.getComponents()) {
            if (c instanceof JLabel) {
                String str = ((JLabel) c).getText();
                double D = Double.parseDouble(str);
                Double firstPart = Math.floor(D);
                Double secondPart = firstPart + 1.0;
                if (firstPart.equals(D + 0.1) || firstPart.equals(D - 0.1) || secondPart.equals(D + 0.1) || secondPart.equals(D - 0.1)) {
                    panel.setBackground(Color.CYAN);
                }
            }
        }
        table.repaint();
        return panel;
    }

    public void setChecker(boolean ch) {
        checker = ch;
    }

    public void setNeedle(String needle) {
        this.needle = needle;
    }
}