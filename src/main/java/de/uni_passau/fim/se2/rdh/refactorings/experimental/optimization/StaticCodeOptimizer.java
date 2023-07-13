package de.uni_passau.fim.se2.rdh.refactorings.experimental.optimization;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

// TODO: Use RdcProbabilities
public class StaticCodeOptimizer extends AbstractModification {

    private static final Logger LOG = LoggerFactory.getLogger(StaticCodeOptimizer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public StaticCodeOptimizer(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        optimize();
    }

    /**
     * Evaluates whatever is possible.
     * E.g.:
     * private static final int FOO = 42;
     * private static final int BAR = 42;
     * private static final int FOO_BAR = FOO + BAR; -> private static final int FOO_BAR = 84;
     * TODO: Consider array accesses
     */
    private void optimize() {

        // Get all binary operations and unary operations
        List<CtBinaryOperator<?>> binaryOperations = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtBinaryOperator.class));
        // List<CtUnaryOperator<?>> unaryOperations = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtUnaryOperator.class));

        // Check if the operations use only constants or final variables or fields
        for (CtBinaryOperator<?> binaryOperation : binaryOperations) {
            if (isAllOperandsConstant(binaryOperation)) {
                CtExpression<?> result = binaryOperation.partiallyEvaluate();
                binaryOperation.replace(result);
            }
        }

    }

    private static boolean isAllOperandsConstant(CtBinaryOperator<?> binaryOperator) {
        CtExpression<?> leftHandOperand = binaryOperator.getLeftHandOperand();
        CtExpression<?> rightHandOperand = binaryOperator.getRightHandOperand();

        return isConstant(leftHandOperand) && isConstant(rightHandOperand);
    }

    private static boolean isAllOperandsConstant(CtUnaryOperator<?> unaryOperator) {
        CtExpression<?> operand = unaryOperator.getOperand();
        return isConstant(operand);
    }

    private static boolean isConstant(CtExpression<?> operand) {
        if (operand instanceof CtLiteral<?>){
            return true;
        }

        if (operand instanceof CtFieldRead<?> fieldAccess){
            CtField<?> field = fieldAccess.getVariable().getFieldDeclaration();
            return field.isFinal() && isPrimitive(field);
        }

        if (operand instanceof CtVariableRead<?> variableRead){
            CtVariable<?> variable = variableRead.getVariable().getDeclaration();
            return variable.isFinal() && isPrimitive(variable);
        }

        return false;
    }

    private static boolean isPrimitive(CtVariable<?> fieldAccess) {
        return fieldAccess.getType().isPrimitive();
    }

    // TODO: Delete me
    private static CtExpression<?> evaluate(CtBinaryOperator<?> binaryOperation) {
        // Get the default values of both operands
        CtExpression<?> rightHandOperand = binaryOperation.getRightHandOperand().partiallyEvaluate();
        CtExpression<?> leftHandOperand = binaryOperation.getLeftHandOperand();

        // Get the operator
        // tBinaryOperatorKind operatorKind = binaryOperation.getKind();

        return null;
    }


}
