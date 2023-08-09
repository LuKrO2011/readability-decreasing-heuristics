public abstract class AbstractClass {

    protected final String variable;

    public AbstractClass(String variable) {
        this.variable = variable;
    }

    public abstract String method(String variable);

    public static void staticMethod() {
        System.out.println("staticMethod");
    }

    public default void defaultMethod() {
        System.out.println("defaultMethod");
    }
}
