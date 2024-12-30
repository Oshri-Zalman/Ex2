import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    public void isNumber() {
        assertTrue(Functions.isNumber("123331"));
        assertTrue(Functions.isNumber("-7568"));
        assertTrue(Functions.isNumber("10000000"));
        assertTrue(Functions.isNumber("1601"));
        assertFalse(Functions.isNumber(""));
        assertFalse(Functions.isNumber(null));
        assertFalse(Functions.isNumber("ASDASD"));

    }

    @Test
    public void isText() {
        assertTrue(Functions.isText(null));
        assertTrue(Functions.isText("hello"));
        assertTrue(Functions.isText("n12b3mn1b3"));
        assertTrue(Functions.isText("10b03"));
        assertFalse(Functions.isText("-1010"));
        assertFalse(Functions.isText("1090"));
        assertFalse(Functions.isText("100000"));
        assertTrue(Functions.isText("{303}"));
    }

        @Test
        public void testisForm() {
            assertTrue(Functions.isForm("=1"));
            assertTrue(Functions.isForm("=1+2*2"));
            assertTrue(Functions.isForm("=(2)"));
            assertTrue(Functions.isForm("=(1+2)*2"));
            assertTrue(Functions.isForm("=1+2*(3/4)-5"));
            assertFalse(Functions.isForm("1"));
            assertFalse(Functions.isForm("=1+2*"));
            assertFalse(Functions.isForm("=1+2*2("));
            assertFalse(Functions.isForm("=1+2**)"));
            assertFalse(Functions.isForm("=1+@2"));
            assertFalse(Functions.isForm("=abc"));
            assertFalse(Functions.isForm(null));
            assertFalse(Functions.isForm(""));
        }

        @Test
        public void testValidFormulas() {
            // Valid formulas
            assertEquals(5.0, Functions.computeForm("=1+2*2"));
            assertEquals(5.0, Functions.computeForm("=((1+2)*2)-1"));
            assertEquals(8.0, Functions.computeForm("=3*(2+4)-10"));
            assertEquals(10.0, Functions.computeForm("=(2+3)*2"));
            assertEquals(7.5, Functions.computeForm("=15/2"));
            assertEquals(14.0, Functions.computeForm("=(2+3)*(4/2)+4"));
            assertEquals(8.0, Functions.computeForm("=10-((4+6)/5)"));
            assertEquals(16.0, Functions.computeForm("=(2+2)*(3+1)"));
        }
    }

