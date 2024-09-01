package test;

import java.util.ArrayList;
import java.util.List;

public class Adder {
    private int num;
    private int numSq;
    private final List<Integer> history = new ArrayList<>();

    static int opCount = 0;

    public Adder(int num) {
        this.num = num;
        this.numSq = num * num;
        history.add(num);
    }

    public Adder() {
        this(0);
    }

    public int getNum() {
        return num;
    }

    public int getNumSq() {
        return numSq;
    }

    public List<Integer> getHistory() {
        return this.history;
    }

    public void add(int addend) {
        num += addend;
        numSq = num * num + 1;
        opCount++;

        history.add(num);
    }
}
