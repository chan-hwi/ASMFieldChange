package Test;

import Test.Testing.Wrapper;

public class Main {
    static int tmp = 0;
    public static void main(String[] args) {
        System.out.println("Start Program");

        Adder adder1 = new Adder(0);
        adder1.add(5);
        adder1.add(10);
        tmp++;
        System.out.println("Value: " + adder1.getNum());
        System.out.println("Tmp: " + tmp);

        Adder adder2 = new Adder(5);
        adder2.add(-10);
        tmp++;
        System.out.println("Value: " + adder2.getNum());
        System.out.println("Tmp: " + tmp);

        Wrapper wrapper = new Wrapper();
        wrapper.setTarget(adder1);
        wrapper.setTarget(adder2);

        if (tmp == 1) {
            System.out.println("tmp: 1");
        } else {
            System.out.println("tmp: 2");
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("i: " + i);
        }

        System.out.println("End Program");
    }
}

