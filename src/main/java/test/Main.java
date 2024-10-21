package test;

public class Main {
    public static void main(String[] args) {
        System.out.println("Program Start");
        Adder myAdder = new Adder(0);
        myAdder.add(10);
        System.out.println("value: " + myAdder.getValue());
        System.out.println("sqValue: " + myAdder.getSqValue());
        myAdder.add(20);
        System.out.println("value: " + myAdder.getValue());
        System.out.println("sqValue: " + myAdder.getSqValue());
        myAdder.setValue(5);
        System.out.println("value: " + myAdder.getValue());
        System.out.println("sqValue: " + myAdder.getSqValue());
        System.out.println("Program End");
    }
}
