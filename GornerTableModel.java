import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public int getColumnCount() {
        return 4;
    }

    public int getRowCount() {
        return new Double(Math.ceil((to - from) / step)).intValue() + 1;
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X";
            case 1:
                return  "Значение многочлена";
            case 2:
                return "Pow";
            default:
                return "Delta";
        }
    }

    public Class<?> getColumnClass(int col) {
        return Double.class;
    }

    public Object getValueAt(int row, int col) {
        double x = from + step * row;
        if (col == 0) {
            return x;
        } else if (col == 1) {
            return calculateGornerValue(x);
        } else if (col == 2) {
            return Math.pow(x, 3) + 2 * Math.pow(x, 2) - 5 * x + 6;
        } else if (col == 3) {
            Double valueFromGorner = calculateGornerValue(x);
            Double valueFromMathPow = Math.pow(x, 3) + 2 * Math.pow(x, 2) - 5 * x + 6;
            return valueFromGorner - valueFromMathPow;
        }

        return null;
    }

    private Double calculateGornerValue(double x) {
        double result = 0.0;
        for (int i = coefficients.length - 1; i >= 0; i--)
            result = result * x + coefficients[i];

        return result;
    }

}