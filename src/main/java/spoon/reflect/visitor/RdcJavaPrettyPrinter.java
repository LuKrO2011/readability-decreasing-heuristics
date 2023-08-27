package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.compiler.Environment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.printer.CommentOffset;

/**
 * This class is a modified version of {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}.
 */
public class RdcJavaPrettyPrinter extends DefaultJavaPrettyPrinter {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RdcJavaPrettyPrinter.class);

    private TokenWriter printer;

    private RdcElementPrinterHelper elementPrinterHelper;

    private final RdcProbabilities probabilities;

    private final PrintingContext context = new PrintingContext();

    /**
     * Creates a new code generator visitor.
     *
     * @param env           The environment to use.
     * @param probabilities The probabilities to use.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The probabilities can be changed by the user at runtime
    public RdcJavaPrettyPrinter(final Environment env, final RdcProbabilities probabilities) {
        super(env);
        this.probabilities = probabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJavaPrettyPrinter setPrinterTokenWriter(final TokenWriter tokenWriter) {
        this.printer = tokenWriter;

        RdcTokenWriter rdcTokenWriter;
        if (tokenWriter instanceof RdcTokenWriter) {
            rdcTokenWriter = (RdcTokenWriter) tokenWriter;
        } else {
            LOG.warn("The token writer is not an instance of RdcTokenWriter. A new instance will be created instead of "
                    + "using the given one.");
            rdcTokenWriter = new RdcTokenWriter(tokenWriter.getPrinterHelper(), probabilities);
        }

        elementPrinterHelper = new RdcElementPrinterHelper(rdcTokenWriter, this, env);
        return super.setPrinterTokenWriter(tokenWriter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void enterCtStatement(final CtStatement s) {
        elementPrinterHelper.writeComment(s, CommentOffset.BEFORE);
        getPrinterHelper().mapLine(s, sourceCompilationUnit);
        if (!context.isNextForVariable()) {
            //TODO AnnotationLoopTest#testAnnotationDeclaredInForInit expects that annotations of next for variables
            // are not printed
            //but may be correct is that the next variables are not annotated, because they might have different
            // annotation then first param!
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
    protected void exitCtStatement(final CtStatement statement) {
        if (!(statement instanceof CtBlock || statement instanceof CtIf || statement instanceof CtFor
                || statement instanceof CtForEach || statement instanceof CtWhile || statement instanceof CtTry
                || statement instanceof CtSwitch || statement instanceof CtSynchronized || statement instanceof CtClass
                || statement instanceof CtComment)) {
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
    protected void exitCtExpression(final CtExpression<?> e) {
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
    protected void enterCtExpression(final CtExpression<?> e) {
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
