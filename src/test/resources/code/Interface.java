public interface Interface {

    void normalMethod();

    default void defaultMethod() {
        System.out.println("defaultMethod");
    }

    static void staticMethod() {
        System.out.println("staticMethod");
    }

}