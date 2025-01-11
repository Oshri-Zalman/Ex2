import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Tests {

    @Test
    public void testIsValid_ValidIndex() {
        CellEntry cell = new CellEntry("A1");
        assertTrue(cell.isValid(), "A1 should be valid");

        cell = new CellEntry("Z99");
        assertTrue(cell.isValid(), "Z99 should be valid");

        cell = new CellEntry("B10");
        assertTrue(cell.isValid(), "B10 should be valid");
    }

    @Test
    public void testIsValid_InvalidIndex() {
        CellEntry cell = new CellEntry(null);
        assertFalse(cell.isValid(), "null should not be valid");

        cell = new CellEntry("");
        assertFalse(cell.isValid(), "Empty string should not be valid");

        cell = new CellEntry("1A");
        assertFalse(cell.isValid(), "1A should not be valid");

        cell = new CellEntry("AA1");
        assertFalse(cell.isValid(), "AA1 should not be valid");

        cell = new CellEntry("A100");
        assertFalse(cell.isValid(), "A100 should not be valid (row out of range)");

        cell = new CellEntry("#1");
        assertFalse(cell.isValid(), "#1 should not be valid");
    }

    @Test
    public void testGetX_ValidIndex() {
        CellEntry cell = new CellEntry("A1");
        assertEquals(0, cell.getX(), "A1 should return X=0");

        cell = new CellEntry("B1");
        assertEquals(1, cell.getX(), "B1 should return X=1");

        cell = new CellEntry("Z1");
        assertEquals(25, cell.getX(), "Z1 should return X=25");
    }

    @Test
    public void testGetX_InvalidIndex() {
        CellEntry cell = new CellEntry("1A");
        assertThrows(IllegalStateException.class, cell::getX, "Invalid index should throw exception for getX");

        cell = new CellEntry("AA1");
        assertThrows(IllegalStateException.class, cell::getX, "Invalid index should throw exception for getX");
    }

    @Test
    public void testGetY_ValidIndex() {
        CellEntry cell = new CellEntry("A1");
        assertEquals(0, cell.getY(), "A1 should return Y=0");

        cell = new CellEntry("A2");
        assertEquals(1, cell.getY(), "A2 should return Y=1");

        cell = new CellEntry("A99");
        assertEquals(98, cell.getY(), "A99 should return Y=98");
    }

    @Test
    public void testGetY_InvalidIndex() {
        CellEntry cell = new CellEntry("1A");
        assertThrows(IllegalStateException.class, cell::getY, "Invalid index should throw exception for getY");

        cell = new CellEntry("AA1");
        assertThrows(IllegalStateException.class, cell::getY, "Invalid index should throw exception for getY");
    }

    @Test
    public void testToString_ValidIndex() {
        CellEntry cell = new CellEntry("a1");
        assertEquals("A1", cell.toString(), "a1 should be converted to A1");

        cell = new CellEntry("B10");
        assertEquals("B10", cell.toString(), "B10 should return B10");
    }

    @Test
    public void testToString_InvalidIndex() {
        CellEntry cell = new CellEntry("1A");
        assertEquals("", cell.toString(), "Invalid index should return empty string");

        cell = new CellEntry(null);
        assertEquals("", cell.toString(), "Null index should return empty string");
    }

    @Test
    public void testInitialization_DefaultSize() {
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals(Ex2Utils.WIDTH, sheet.width(), "Default width should match Ex2Utils.WIDTH");
        assertEquals(Ex2Utils.HEIGHT, sheet.height(), "Default height should match Ex2Utils.HEIGHT");
    }

    @Test
    public void testInitialization_CustomSize() {
        Ex2Sheet sheet = new Ex2Sheet(10, 20);
        assertEquals(10, sheet.width(), "Width should be 10");
        assertEquals(20, sheet.height(), "Height should be 20");
    }

    @Test
    public void testSetAndGetCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(2, 3, "Hello");
        assertEquals("Hello", sheet.get(2, 3).getData(), "Cell (2,3) should contain 'Hello'");
    }

    @Test
    public void testIsInBounds() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertTrue(sheet.isIn(0, 0), "Cell (0,0) should be in bounds");
        assertFalse(sheet.isIn(-1, 0), "Cell (-1,0) should be out of bounds");
        assertFalse(sheet.isIn(5, 5), "Cell (5,5) should be out of bounds");
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "Test");
        sheet.save("test_sheet.csv");

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load("test_sheet.csv");
        assertEquals("Test", loadedSheet.get(0, 0).getData(), "Loaded cell (0,0) should contain 'Test'");
    }

    @Test
    public void testEvaluateCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "42");
        assertEquals("42", sheet.eval(0, 0), "Cell (0,0) should evaluate to '42'");
    }


    @Test
    public void testSetData() {
        SCell cell = new SCell("Test");
        assertEquals("Test", cell.getData(), "Cell should contain 'Test'");
        cell.setData("NewValue");
        assertEquals("NewValue", cell.getData(), "Cell should contain 'NewValue'");
    }

    @Test
    public void testEvaluate_Literal() {
        SCell cell = new SCell("42");
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals("42", cell.evaluate(sheet, 0, 0, new HashSet<>()), "Literal cell should evaluate to its value");
    }

    @Test
    public void testEvaluate_Formula() {
        SCell cell = new SCell("=A0+B0");
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "20");

        assertEquals("30.0", cell.evaluate(sheet, 2, 0, new HashSet<>()), "Formula should evaluate to sum of A1 and B1");
    }

    @Test
    public void testEvaluate_CyclicReference() {
        SCell cell = new SCell("=A0");
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=A0");

        assertEquals("ERR_CYCLE", cell.evaluate(sheet, 0, 0, new HashSet<>()), "Cyclic reference should return 'ERR_Cycle'");
    }
}


