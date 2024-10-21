package test;

public class Adder {
    public static int totalOperationCount = 0;
    private int value;
    private int sqValue;

    public Adder(int value) {
        this.value = value;
        this.sqValue = value * value;
    }

    public Adder() {
        this(0);
    }

    public void add(int value) {
        this.value += value;
        this.sqValue = value * value;
    }

    public int getValue() {
        return this.value;
    }

    public int getSqValue() {
        return this.sqValue;
    }

    public boolean isZero() {
        if (this.value == 0) return true;
        return false;
    }

    public void setValue(int value) {
        this.value = value;
        this.sqValue = value * value;
    }
}
