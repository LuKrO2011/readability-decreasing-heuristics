package spoon.reflect.visitor;

import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.printer.CommentOffset;

import java.util.List;

/**
 * This class is a modified version of {@link ElementPrinterHelperC}, which is a copy of {@link ElementPrinterHelper}.
 * The purpose of this class is to make sure that a newline is written after each comment.
 */
public class RdcElementPrinterHelper extends ElementPrinterHelperC {

    private final JavaPrettyPrinterC prettyPrinter;
    private final Environment env;
    private RdcTokenWriter printer;

    /**
     * Creates a new element printer helper.
     *
     * @param printerTokenWriter The token writer to use.
     * @param prettyPrinter      The pretty printer to use.
     * @param env                The environment to use.
     */
    public RdcElementPrinterHelper(final RdcTokenWriter printerTokenWriter, final JavaPrettyPrinterC prettyPrinter,
                                   final Environment env) {
        super(printerTokenWriter, prettyPrinter, env);
        this.printer = printerTokenWriter;
        this.prettyPrinter = prettyPrinter;
        this.env = env;
    }

    /**
     * Makes sure a newline is written after the given element.
     * {@inheritDoc}
     * @param comment The comment to write.
     */
    @Override
    public void writeComment(final CtComment comment) {
        if (!env.isCommentsEnabled() || comment == null) {
            return;
        }
        prettyPrinter.scan(comment);
        printer.writelnAfterComment();
    }

    /**
     * @see spoon.reflect.visitor.ElementPrinterHelper#writeComment(java.util.List)
     * @param comments The comments to write.
     */
    private void writeComment(final List<CtComment> comments) {
        if (!env.isCommentsEnabled() || comments == null) {
            return;
        }
        for (CtComment comment : comments) {
            writeComment(comment);
        }
    }

    /**
     * {@inheritDoc}
     * @param element The element to write.
     */
    public void writeComment(final CtElement element) {
        if (element == null) {
            return;
        }
        writeComment(element.getComments());
    }

    /**
     * {@inheritDoc}
     * @param element The element to write.
     */
    public void writeComment(final CtElement element, final CommentOffset offset) {
        writeComment(getComments(element, offset));
    }


}
