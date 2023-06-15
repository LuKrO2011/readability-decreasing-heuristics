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

public class CtInvocationProcessor extends AbstractProcessor<CtElement> {
    private final CtExecutableReference<?> executableRef;

    public CtInvocationProcessor(CtExecutableReference<?> executableRef) {
        this.executableRef = executableRef;
    }

    @Override
    public void process(CtElement element) {
        if (element instanceof CtStatement && callsInvocation((CtStatement) element)) {
            CtStatement statement = (CtStatement) element;
            CtInvocation<?> invocation = findInvocationInStatement(statement);

            if (invocation != null) {
                // Replace the invocation with the method body
                List<CtStatement> methodBody = getMethodBody(invocation);

                if (!methodBody.isEmpty()) {
                    List<CtElement> directChildren = executableRef.getDirectChildren();
                    for (CtElement s : directChildren) {
                        // executableRef.insertBefore(s);
                    }
                }
            }
        }
    }

    private boolean callsInvocation(CtStatement statement) {
        // Check if the statement contains an invocation with the same executable reference
        return findInvocationInStatement(statement) != null;
    }

    private CtInvocation<?> findInvocationInStatement(CtStatement statement) {
        // Find the invocation in the statement using a TypeFilter
        return statement.getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(invocation -> invocation.getExecutable().equals(executableRef))
                .findFirst()
                .orElse(null);
    }


    private List<CtStatement> getMethodBody(CtInvocation<?> invocation) {
        // Get the method body by traversing the invocation's target
        CtElement target = invocation.getTarget();
        while (target != null && !(target instanceof CtBlock)) {
            target = target.getParent();
        }

        List<CtStatement> methodBody = new ArrayList<>();
        if (target != null) {
            CtBlock<?> block = (CtBlock<?>) target;
            methodBody.addAll(block.getStatements());
        }

        return methodBody;
    }

}
