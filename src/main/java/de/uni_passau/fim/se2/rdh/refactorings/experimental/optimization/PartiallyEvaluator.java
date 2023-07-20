package de.uni_passau.fim.se2.rdh.refactorings.experimental.optimization;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * This class partially evaluates expressions.
 */
public class PartiallyEvaluator extends AbstractModification {

    private static final Logger LOG = LoggerFactory.getLogger(PartiallyEvaluator.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public PartiallyEvaluator(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        partiallyEvaluate();
    }

    /**
     * Evaluates whatever is possible. E.g.: private static final int FOO = 42; private static final int BAR = 42;
     * private static final int FOO_BAR = FOO + BAR; -> private static final int FOO_BAR = 84;
     * TODO: Consider array accesses
     */
    private void partiallyEvaluate() {

        // Get all binary operations and unary operations
        List<CtUnaryOperator<?>> unaryOperations =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtUnaryOperator.class));
        List<CtBinaryOperator<?>> binaryOperations =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtBinaryOperator.class));

        // Check if the unary operations use only constants or final variables or fields
        for (CtUnaryOperator<?> unaryOperation : unaryOperations) {
            if (isAllOperandsConstant(unaryOperation)) {
                if (probabilities.shouldPartiallyEvaluate()) {
                    // Partially evaluate the operation
                    CtExpression<?> result = unaryOperation.partiallyEvaluate();
                    unaryOperation.replace(result);
                }
            }
        }

        // Check if the binary operations use only constants or final variables or fields
        for (CtBinaryOperator<?> binaryOperation : binaryOperations) {
            if (isAllOperandsConstant(binaryOperation)) {
                if (probabilities.shouldPartiallyEvaluate()) {
                    // Partially evaluate the operation
                    CtExpression<?> result = binaryOperation.partiallyEvaluate();
                    binaryOperation.replace(result);
                }
            }
        }
    }

    private static boolean isAllOperandsConstant(final CtBinaryOperator<?> binaryOperator) {
        CtExpression<?> leftHandOperand = binaryOperator.getLeftHandOperand();
        CtExpression<?> rightHandOperand = binaryOperator.getRightHandOperand();

        return isConstant(leftHandOperand) && isConstant(rightHandOperand);
    }

    private static boolean isAllOperandsConstant(final CtUnaryOperator<?> unaryOperator) {
        CtExpression<?> operand = unaryOperator.getOperand();
        return isConstant(operand);
    }

    private static boolean isConstant(final CtExpression<?> operand) {
        if (operand instanceof CtLiteral<?>) {
            return true;
        }

        if (operand instanceof CtFieldRead<?> fieldAccess) {
            CtField<?> field = fieldAccess.getVariable().getFieldDeclaration();
            return field.isFinal() && isPrimitive(field);
        }

        if (operand instanceof CtVariableRead<?> variableRead) {
            CtVariable<?> variable = variableRead.getVariable().getDeclaration();
            return variable.isFinal() && isPrimitive(variable);
        }

        return false;
    }

    private static boolean isPrimitive(final CtVariable<?> fieldAccess) {
        return fieldAccess.getType().isPrimitive();
    }

}
