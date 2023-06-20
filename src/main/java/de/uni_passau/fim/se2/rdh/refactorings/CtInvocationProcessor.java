package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

// TODO: Remove or it make work

/**
 * This class is an alternative implementation of the CtInlineMethodRefactoring.
 * It does not work yet.
 */
public class CtInvocationProcessor extends AbstractProcessor<CtElement> {

    /**
     * The executable reference of the method that is currently being inlined.
     */
    private final CtExecutableReference<?> executableRef;

    /**
     * This method returns the method body of the given invocation.
     *
     * @param executableRef the executable reference
     */
    public CtInvocationProcessor(final CtExecutableReference<?> executableRef) {
        this.executableRef = executableRef;
    }

    /**
     * This method is called for every element in the AST. It checks if the element is a statement and if it contains
     * an invocation of the method that is currently being inlined. If so, it replaces the invocation with the method
     * body.
     *
     * @param element the element that is currently being scanned
     */
    @Override
    public void process(final CtElement element) {
        if (element instanceof CtStatement statement && callsInvocation((CtStatement) element)) {
            CtInvocation<?> invocation = findInvocationInStatement(statement);

            if (invocation != null) {

                // Replace the invocation with the method body
                List<CtStatement> methodBody = getMethodBody(invocation);

                if (!methodBody.isEmpty()) {
                    List<CtElement> directChildren = executableRef.getDirectChildren();
                    // TODO: Replace invocation with method body
                }
            }
        }
    }

    /**
     * This method checks if the given statement contains an invocation of the method that is currently being inlined.
     *
     * @param statement the statement that is being checked
     * @return true if the statement contains an invocation of the method that is currently being inlined, false
     */
    private boolean callsInvocation(final CtStatement statement) {

        // Check if the statement contains an invocation with the same executable reference
        return findInvocationInStatement(statement) != null;
    }

    /**
     * This method finds the invocation of the method that is currently being inlined in the given statement.
     *
     * @param statement the statement that is being checked
     * @return the invocation of the method that is currently being inlined in the given statement, null if there is
     * no such invocation
     */
    private CtInvocation<?> findInvocationInStatement(final CtStatement statement) {

        // Find the invocation in the statement using a TypeFilter
        return statement.getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(invocation -> invocation.getExecutable().equals(executableRef))
                .findFirst()
                .orElse(null);
    }

    /**
     * This method returns the method body of the given invocation.
     *
     * @param invocation the invocation whose method body is returned
     * @return the method body of the given invocation
     */
    private List<CtStatement> getMethodBody(final CtInvocation<?> invocation) {

        // Get the method body by traversing the invocation's target
        CtElement target = invocation.getTarget();
        while (target != null && !(target instanceof CtBlock)) {
            target = target.getParent();
        }

        // Return the method body
        List<CtStatement> methodBody = new ArrayList<>();
        if (target != null) {
            CtBlock<?> block = (CtBlock<?>) target;
            methodBody.addAll(block.getStatements());
        }

        return methodBody;
    }

}
