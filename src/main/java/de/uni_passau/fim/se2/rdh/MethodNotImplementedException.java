package de.uni_passau.fim.se2.rdh;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException(String todo) {
        super(todo);
    }
}
