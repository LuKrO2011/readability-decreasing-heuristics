package de.uni_passau.fim.se2.rdm.old;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException(String todo) {
        super(todo);
    }
}
