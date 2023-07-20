package de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers;

import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.factory.CodeFactory;

/**
 * This class implements the refactoring for adding 0 to an expression.
 */
public class CtAdd0 implements CtRefactoring {


    private CtBinaryOperator<?> target;


    /**
     * This constructor sets the target of the refactoring.
     */
    @Override
    public void refactor() {
        add0();
    }

    /**
     * This method adds 0 to the target.
     */
    private void add0() {
        // Clone the target
        CtBinaryOperator<?> newBinaryOperator = target.clone();

        // Create a plus0 literal
        CodeFactory fac = target.getFactory().Code();
        CtLiteral<Integer> plus0 = fac.createLiteral(0);

        // Replace the original operator with the new operator
        target.replace(newBinaryOperator);

        // Set the original operator as the right-hand operand of the new operator
        newBinaryOperator.setRightHandOperand(target);
        newBinaryOperator.setLeftHandOperand(plus0);
    }

    /**
     * This method returns the target of the refactoring.
     *
     * @return The target of the refactoring.
     */
    public CtBinaryOperator<?> getTarget() {
        return target;
    }

    /**
     * This method sets the target of the refactoring.
     *
     * @param target The target of the refactoring.
     */
    public void setTarget(final CtBinaryOperator<?> target) {
        this.target = target;
    }
}
