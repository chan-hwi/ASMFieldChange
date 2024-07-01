package Test.Testing;

public class Wrapper {
    private Object target = null;

    public Wrapper(Object target) {
        this.target = target;
    }

    public Wrapper() {
        this(null);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
