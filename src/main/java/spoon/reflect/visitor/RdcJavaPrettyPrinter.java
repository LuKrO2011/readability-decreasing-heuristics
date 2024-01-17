package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
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
 * This class is a modified version of {@link JavaPrettyPrinterC}, which is a copy of {@link DefaultJavaPrettyPrinter}.
 * We can not use the Sniper pretty printer, because it only works within the spoon framework itself. We can set the
 * disableInlineImports in {@link JavaPrettyPrinterC} to true to achieve this, but this might cause, that the code does
 * not compile anymore.
 */
public class RdcJavaPrettyPrinter extends JavaPrettyPrinterC {

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
        super(env, probabilities);
        this.probabilities = probabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaPrettyPrinterC setPrinterTokenWriter(final RdcTokenWriter tokenWriter) {
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
        super.enterCtStatement(s);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void exitCtStatement(final CtStatement statement) {
        super.exitCtStatement(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Method copied from {@link DefaultJavaPrettyPrinter}.
     */
    @Override
    protected void exitCtExpression(final CtExpression<?> e) {
        super.exitCtExpression(e);
    }

    /**
     * Writes additional, unnecessary braces with a given probability, if the expression is a binary operator.
     * <p>
     * {@inheritDoc}
     */
    protected void enterCtExpression(final CtExpression<?> e) {
        super.enterCtExpression(e);
    }

    private PrinterHelper getPrinterHelper() {
        return printer.getPrinterHelper();
    }

}
