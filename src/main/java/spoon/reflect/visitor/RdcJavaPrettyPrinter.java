package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.compiler.Environment;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.printer.CommentOffset;

/**
 * This class is a modified version of {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}.
 */
public class RdcJavaPrettyPrinter extends DefaultJavaPrettyPrinter {

    private TokenWriter printer;

    private ElementPrinterHelper elementPrinterHelper;

    private final RdcProbabilities probabilities;

    private final PrintingContext context = new PrintingContext();

    /**
     * Creates a new code generator visitor.
     *
     * @param env The environment to use.
     */
    public RdcJavaPrettyPrinter(Environment env, RdcProbabilities probabilities) {
        super(env);
        this.probabilities = probabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJavaPrettyPrinter setPrinterTokenWriter(TokenWriter tokenWriter) {
        this.printer = tokenWriter;
        elementPrinterHelper = new ElementPrinterHelper(tokenWriter, this, env);
        return super.setPrinterTokenWriter(tokenWriter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void enterCtStatement(CtStatement s) {
        elementPrinterHelper.writeComment(s, CommentOffset.BEFORE);
        getPrinterHelper().mapLine(s, sourceCompilationUnit);
        if (!context.isNextForVariable()) {
            //TODO AnnotationLoopTest#testAnnotationDeclaredInForInit expects that annotations of next for variables are not printed
            //but may be correct is that the next variables are not annotated, because they might have different annotation then first param!
            elementPrinterHelper.writeAnnotations(s);
        }
        if (!context.isFirstForVariable() && !context.isNextForVariable()) {
            if (s.getLabel() != null) {
                printer.writeIdentifier(s.getLabel()).writeSpace().writeSeparator(":").writeSpace();
            }
        }
        super.enterCtStatement(s);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void exitCtStatement(CtStatement statement) {
        if (!(statement instanceof CtBlock || statement instanceof CtIf || statement instanceof CtFor || statement instanceof CtForEach || statement instanceof CtWhile || statement instanceof CtTry
            || statement instanceof CtSwitch || statement instanceof CtSynchronized || statement instanceof CtClass || statement instanceof CtComment)) {
            if (context.isStatement(statement) && !context.isFirstForVariable() && !context.isNextForVariable()) {
                printer.writeSeparator(";");
            }
        }
        elementPrinterHelper.writeComment(statement, CommentOffset.AFTER);
        super.exitCtStatement(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void exitCtExpression(CtExpression<?> e) {
        while ((!context.parenthesedExpression.isEmpty()) && e == context.parenthesedExpression.peek()) {
            context.parenthesedExpression.pop();
            printer.writeSeparator(")");
        }
        if (!(e instanceof CtStatement)) {
            elementPrinterHelper.writeComment(e, CommentOffset.AFTER);
        }
        super.exitCtExpression(e);
    }

    /**
     * Writes additional, unnecessary braces with a given probability, if the expression is a binary operator.
     * <p>
     * {@inheritDoc}
     */
    protected void enterCtExpression(CtExpression<?> e) {
        if (e instanceof CtBinaryOperator<?> && probabilities.shouldInsertBraces()) {
            context.parenthesedExpression.push(e);
            printer.writeSeparator("(");
            super.enterCtExpression(e);
        } else {
            super.enterCtExpression(e);
        }
    }

    private PrinterHelper getPrinterHelper() {
        return printer.getPrinterHelper();
    }

}
