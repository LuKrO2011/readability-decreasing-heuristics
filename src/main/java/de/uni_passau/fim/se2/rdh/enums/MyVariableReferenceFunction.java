package de.uni_passau.fim.se2.rdh.enums;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.CatchVariableReferenceFunction;
import spoon.reflect.visitor.filter.FieldReferenceFunction;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.ParameterReferenceFunction;

/**
 * This class is an adapted version of {@link spoon.reflect.visitor.filter.VariableReferenceFunction} to additionally
 * support {@link CtEnumValue}.
 * <p>
 * The mapping function, accepting {@link CtVariable}
 * <ul>
 * <li>CtLocalVariable - local variable declared in body
 * <li>CtField - member field of an type
 * <li>CtParameter - method parameter
 * <li>CtCatchVariable - try - catch variable
 * <li>CtEnumValue - enum value
 * </ul>
 * and returning all the {@link CtVariableReference}, which refers this variable
 * </p>
 */
public class MyVariableReferenceFunction implements CtConsumableFunction<CtElement> {

    protected final Visitor visitor = new Visitor();
    private final CtVariable<?> variable;
    protected CtConsumer<Object> outputConsumer;
    protected CtElement scope;

    public MyVariableReferenceFunction() {
        this.variable = null;
    }

    public MyVariableReferenceFunction(CtVariable<?> variable) {
        this.variable = variable;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2") // The scope can be changed by the user
    @Override
    public void apply(CtElement variableOrScope, CtConsumer<Object> outputConsumer) {
        scope = variableOrScope;
        CtVariable<?> var = this.variable;
        if (var == null) {
            if (variableOrScope instanceof CtVariable<?>) {
                var = (CtVariable<?>) variableOrScope;
            } else {
                throw new SpoonException("The input of VariableReferenceFunction must be a CtVariable but is a "
                        + variableOrScope.getClass().getSimpleName());
            }
        }
        this.outputConsumer = outputConsumer;
        var.accept(visitor);
    }

    protected class Visitor extends CtScanner {
        @Override
        protected void enter(CtElement e) {
            throw new SpoonException("Unsupported variable of type " + e.getClass().getName());
        }

        /**
         * calls outputConsumer for each reference of the field
         */
        @Override
        public <T> void visitCtField(CtField<T> field) {
            new FieldReferenceFunction((CtField<?>) variable).apply(scope, outputConsumer);
        }

        /**
         * calls outputConsumer for each reference of the local variable
         */
        @Override
        public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
            new LocalVariableReferenceFunction((CtLocalVariable<?>) variable).apply(scope, outputConsumer);
        }

        /**
         * calls outputConsumer for each reference of the parameter
         */
        @Override
        public <T> void visitCtParameter(CtParameter<T> parameter) {
            new ParameterReferenceFunction((CtParameter<?>) variable).apply(scope, outputConsumer);
        }

        /**
         * calls outputConsumer for each reference of the catch variable
         */
        @Override
        public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
            new CatchVariableReferenceFunction((CtCatchVariable<?>) variable).apply(scope, outputConsumer);
        }

        /**
         * calls outputConsumer for each reference of the enum
         */
        @Override
        public <T> void visitCtEnumValue(final CtEnumValue<T> enumValue) {
            new EnumValueReferenceFunction((CtEnumValue<?>) variable).apply(scope, outputConsumer);
        }
    }
}
