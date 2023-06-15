package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdm.config.RdcProbabilities;
import spoon.reflect.code.CtComment;

public class RdcTokenWriter implements TokenWriter {

    private final PrinterHelper printerHelper;
    private final RdcProbabilities probabilities;

    public RdcTokenWriter() {
        this(new PrinterHelper());
    }

    public RdcTokenWriter(PrinterHelper printerHelper, RdcProbabilities probabilities) {
        this.printerHelper = printerHelper;
        this.probabilities = probabilities;
    }

    public RdcTokenWriter(PrinterHelper printerHelper) {
        this(printerHelper, new RdcProbabilities());
    }

    @Override
    public RdcTokenWriter writeOperator(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeSeparator(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeLiteral(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeKeyword(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeIdentifier(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeCodeSnippet(String token) {
        printerHelper.write(token);
        return this;
    }

    @Override
    public RdcTokenWriter writeComment(CtComment comment) {
        CommentHelper.printComment(printerHelper, comment);
        return this;
    }

    @Override
    public RdcTokenWriter writeln() {

        // Write spaces instead of a newline
        if (probabilities.shouldSwap(CharacterType.NEWLINE)) {
            return (RdcTokenWriter) writeSpace();
        }

        // Write newlines
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.NEWLINE);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.writeln();
        }

        return this;
    }

    @Override
    public RdcTokenWriter incTab() {

        // Dec tab instead of inc tab
        if (probabilities.shouldSwap(CharacterType.INC_TAB)) {
            return decTab();
        }

        // Inc tabs
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.INC_TAB);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.incTab();
        }

        return this;
    }

    @Override
    public RdcTokenWriter decTab() {

        // Inc tab instead of dec tab
        if (probabilities.shouldSwap(CharacterType.DEC_TAB)) {
            return incTab();
        }

        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.DEC_TAB);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.decTab();
        }

        return this;
    }

    @Override
    public void reset() {
        printerHelper.reset();
    }

    @Override
    public TokenWriter writeSpace() {

        // Write newlines instead of space
        if (probabilities.shouldSwap(CharacterType.SPACE)) {
            return (TokenWriter) printerHelper.writeln();
        }

        // Write spaces
        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.SPACE);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.writeSpace();
        }

        return this;
    }

    @Override
    public PrinterHelper getPrinterHelper() {
        return printerHelper;
    }

    @Override
    public String toString() {
        return printerHelper.toString();
    }
}
