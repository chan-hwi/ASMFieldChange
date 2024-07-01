package Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAdder {
    @Test
    public void testAdderInit() {
        Adder adder = new Adder();

        assertEquals(0, adder.getNum());
    }

    @Test
    public void testAdderInitWithValue() {
        Adder adder = new Adder(5);

        assertEquals(5, adder.getNum());
    }

    @Test
    public void testAdderAdd() {
        Adder adder = new Adder();

        adder.add(5);
        assertEquals(5, adder.getNum());
    }

    @Test
    public void testAdderSub() {
        Adder adder = new Adder();

        adder.add(-5);
        assertEquals(-5, adder.getNum());
    }
}
