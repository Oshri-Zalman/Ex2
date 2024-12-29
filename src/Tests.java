import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    public void isNumber() {
        assertTrue(Cell.isNumber("123331"));
        assertTrue(Cell.isNumber("-7568"));
        assertTrue(Cell.isNumber("10000000"));
        assertTrue(Cell.isNumber("1601"));
        assertFalse(Cell.isNumber(""));
        assertFalse(Cell.isNumber(null));
        assertFalse(Cell.isNumber("ASDASD"));

    }

    @Test
    public void isText() {
        assertTrue(Cell.isText(null));
        assertTrue(Cell.isText("hello"));
        assertTrue(Cell.isText("n12b3mn1b3"));
        assertTrue(Cell.isText("10b03"));
        assertFalse(Cell.isText("-1010"));
        assertFalse(Cell.isText("1090"));
        assertFalse(Cell.isText("100000"));
        assertTrue(Cell.isText("{303}"));
    }


}
