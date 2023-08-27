package de.uni_passau.fim.se2.rdh.printer;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spoon.reflect.code.CtComment;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.TokenWriter;

/**
 * This class writes certain tokens to the output.
 * <p>
 * With a certain probability (defined in {@link RdcProbabilities}) it will modify the output by adding or removing
 * tabs, spaces, newlines, etc.
 * </p><p>
 * This class is a modified version of {@link spoon.reflect.visitor.DefaultTokenWriter}. It is used by the
 * {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}.
 * </p>
 */
public class RdcTokenWriter implements TokenWriter {

    private final PrinterHelper printerHelper;
    private final RdcProbabilities probabilities;

    /**
     * Creates a new {@link RdcTokenWriter} with the given {@link PrinterHelper} and {@link RdcProbabilities}.
     *
     * @param printerHelper The {@link PrinterHelper} to use.
     * @param probabilities The {@link RdcProbabilities} to use.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The probabilities can be changed by the user at runtime
    public RdcTokenWriter(final PrinterHelper printerHelper, final RdcProbabilities probabilities) {
        this.printerHelper = printerHelper;
        this.probabilities = probabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeOperator(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeSeparator(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeLiteral(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeKeyword(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeIdentifier(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeCodeSnippet(final String token) {
        printerHelper.write(token);
        return this;
    }

    /**
     * With a certain probability, this method will remove the given comment.
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeComment(final CtComment comment) {
        if (!probabilities.shouldRemoveComment()) {
            RdcCommentHelper.printComment(printerHelper, comment);
        }

        return this;
    }

    /**
     * With a certain probability, this method will write none or multiple newlines instead of one.
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter writeln() {

        // TODO: This leads to problems with comments
        // Write spaces instead of a newline
        if (probabilities.shouldSwap(CharacterType.NEWLINE)) {
            printerHelper.writeSpace();
            return this;
        }

        // Write newlines
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.NEWLINE);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.writeln();
        }

        return this;
    }

    /**
     * With a certain probability, this method will increase none or multiple tabs instead of one.
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter incTab() {

        // Dec tab instead of inc tab
        if (probabilities.shouldSwap(CharacterType.INC_TAB)) {
            printerHelper.decTab();
            return this;
        }

        // Inc tabs
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.INC_TAB);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.incTab();
        }

        return this;
    }

    /**
     * With a certain probability, this method will decrease none or multiple tabs instead of one.
     * {@inheritDoc}
     */
    @Override
    public RdcTokenWriter decTab() {

        // Inc tab instead of dec tab
        if (probabilities.shouldSwap(CharacterType.DEC_TAB)) {
            printerHelper.incTab();
            return this;
        }

        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.DEC_TAB);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.decTab();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        printerHelper.reset();
    }

    /**
     * With a certain probability, this method will write none or multiple spaces instead of one.
     * {@inheritDoc}
     */
    @Override
    public TokenWriter writeSpace() {

        // Write newlines instead of space
        if (probabilities.shouldSwap(CharacterType.SPACE)) {
            printerHelper.writeln();
            return this;
        }

        // Write spaces
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.SPACE);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.writeSpace();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrinterHelper getPrinterHelper() {
        return printerHelper;
    }
}
