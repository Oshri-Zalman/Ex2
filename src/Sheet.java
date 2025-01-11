import java.io.IOException;

public interface Sheet {

    boolean isIn(int x, int y);

    public int width();

    public int height();

    public void set(int x, int y, String c);

    public Cell get(int x, int y);

    public Cell get(String entry); // G12

    public String value(int x, int y);

    public String eval(int x, int y);

    public void eval();

    public int[][] depth();

    public void save(String fileName) throws IOException;

    public void load(String fileName) throws IOException;
}
