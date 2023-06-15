package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdm.config.RdcProbabilities;
import spoon.reflect.code.CtComment;

import java.util.Random;

public class RdcTokenWriter implements TokenWriter {

    private final Random random = new Random();

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

        // TODO: Adjust this
        if (random.nextDouble() < probabilities.getSpaceInsteadOfNewline()) {
            printerHelper.writeSpace();
            return this;
        }

        int numberOfNewLines = probabilities.getRandomNumberOf(CharacterType.NEWLINE);
        for (int i = 0; i < numberOfNewLines; i++) {
            printerHelper.writeln();
        }

        return this;
    }

    @Override
    public RdcTokenWriter incTab() {
        if (random.nextDouble() < probabilities.getDoubleTab()) {
            printerHelper.incTab().incTab();
            return this;
        }

        if (random.nextDouble() < probabilities.getNoTab()) {
            return this;
        }

        printerHelper.incTab();
        return this;
    }

    @Override
    public RdcTokenWriter decTab() {
        if (random.nextDouble() < probabilities.getDoubleTab()) {
            printerHelper.decTab().decTab();
            return this;
        }

        if (random.nextDouble() < probabilities.getNoTab()) {
            return this;
        }

        printerHelper.decTab();
        return this;
    }

    @Override
    public void reset() {
        printerHelper.reset();
    }

    @Override
    public TokenWriter writeSpace() {
        if (random.nextDouble() < probabilities.getDoubleSpace()) {
            printerHelper.writeSpace();
            printerHelper.writeSpace();
            return this;
        }

        if (random.nextDouble() < probabilities.getNoSpace()) {
            return this;
        }

        if (random.nextDouble() < probabilities.getNewLineInsteadOfSpace()) {
            printerHelper.writeln();
            return this;
        }

        printerHelper.writeSpace();
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
