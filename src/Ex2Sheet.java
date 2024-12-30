import java.io.IOException;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        // Add your code here

        Cell c = get(x,y);
        if(c!=null) {ans = c.toString();}

        /////////////////////
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;

        // שלב 1: המרת הקואורדינטות (כמו A1) לאינדקסים של עמודה ושורה
        int x = getColumnIndex(cords); // המרה של העמודה (A, B, C וכו') לאינדקס
        int y = getRowIndex(cords); // המרה של השורה (1, 2, 3 וכו') לאינדקס

        // שלב 2: בדיקה אם הקואורדינטות בתוכניות הגיליון נכונות
        if (isIn(x, y)) {
            // אם הקואורדינטות בתוקף (הם נמצאים בתוך הטווח של הגיליון), אז נקבל את התא
            ans = get(x, y);
        } else {
            // אם הקואורדינטות לא בתוקף, נשאיר ans כ-null (או אפשר להחזיר תא שגיאה)
            ans = null;  // ניתן להחזיר תא שגיאה אם תרצה
        }

        // שלב 3: החזרת התא
        return ans;

    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
        // Add your code here

        /////////////////////
    }
    @Override
    public void eval() {
        int[][] dd = depth();
        // Add your code here

        // ///////////////////
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        // Add your code here

        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        // Add your code here

        // ///////////////////
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        // Add your code here

        /////////////////////
        return ans;
        }
        // my functions:

    public static int getColumnIndex(String cell) {

        String columnPart = cell.replaceAll("[0-9]", "");

        int columnIndex = 0;
        for (int i = 0; i < columnPart.length(); i++) {
            char ch = columnPart.charAt(i);
            columnIndex = columnIndex * 26 + (ch - 'A');
        }
        return columnIndex;
    }

    public static int getRowIndex(String cell) {
        String rowPart = cell.replaceAll("[A-Za-z]", "");
        return Integer.parseInt(rowPart) - 1;
    }
}
