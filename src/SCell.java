// Add your documentation below:


import java.util.ArrayList;

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;

    public SCell(String s) {
        // Add your code here
        setData(s);
        // Determine the type of the cell based on its data
        if (Functions.isNumber(line)) {
            setType(Ex2Utils.NUMBER);
            setOrder(0);
        } else if (Functions.isText(line)) {
            setType(Ex2Utils.TEXT);
            setOrder(0);
        } else if (Functions.isForm(line)) {
            setType(Ex2Utils.FORM);
        } else {
            setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

    @Override
    public int getOrder() {
        // Add your code here
        if (type == Ex2Utils.NUMBER || type == Ex2Utils.TEXT) {
            return 0; // Numbers and text have an order of 0
        }
        if (type == Ex2Utils.FORM) {
            try {
                // Compute order based on dependent cells
                ArrayList<String> dependentCells = Functions.findDependentCells(line);
                int maxOrder = 0;
                for (String cell : dependentCells) {
                    Ex2Sheet sheet = new Ex2Sheet();  // יצירת אובייקט של Ex2Sheet (אם אין לך אחד קיים)
                    SCell dependentCell = (SCell) sheet.get(cell);
                    maxOrder = Math.max(maxOrder, dependentCell.getOrder());
                }
                order = maxOrder + 1; // The order is one more than the max of dependent cells
            } catch (Exception e) {
                setType(Ex2Utils.ERR_FORM_FORMAT); // If an error occurs, mark as invalid
                order = -1;
            }
        }
        return order;
        // ///////////////////
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
public void setData(String s) {
        // Add your code here
        line = s;
        // עדכון הסוג בהתאם לקלט החדש
        if (Functions.isNumber(s)) {
            setType(Ex2Utils.NUMBER);
        }
        else if (Functions.isForm(s)) {
            setType(Ex2Utils.FORM);
        }
        else if (Functions.isText(s)) {
            setType(Ex2Utils.TEXT);
        }
        else {
            setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        // Add your code here
        order = t;
    }
}
