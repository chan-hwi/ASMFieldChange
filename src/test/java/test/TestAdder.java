package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAdder {
    @Test
    public void testAdderInit() {
        Adder adder = new Adder();

        assertEquals(0, adder.getNum());
        assertEquals(0, adder.getNumSq());
    }

    @Test
    public void testAdderInitWithValue() {
        Adder adder = new Adder(5);

        assertEquals(5, adder.getNum());
        assertEquals(25, adder.getNumSq());
    }

    @Test
    public void testAdderAdd() {
        Adder adder = new Adder();
        adder.add(5);

        assertEquals(5, adder.getNum());
        assertEquals(25, adder.getNumSq());
    }

    @Test
    public void testAdderSub() {
        Adder adder = new Adder();
        adder.add(-5);

        assertEquals(-5, adder.getNum());
        assertEquals(25, adder.getNumSq());
    }
}
