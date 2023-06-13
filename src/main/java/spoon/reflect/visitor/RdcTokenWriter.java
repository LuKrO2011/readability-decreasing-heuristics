package spoon.reflect.visitor;

import spoon.reflect.code.CtComment;

public class RdcTokenWriter implements TokenWriter {

    private final PrinterHelper printerHelper;

    public RdcTokenWriter() {
        this.printerHelper = new PrinterHelper();
    }

    public RdcTokenWriter(PrinterHelper printerHelper) {
        this.printerHelper = printerHelper;
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
        printerHelper.writeln();
        printerHelper.writeln();
        printerHelper.writeln();
        printerHelper.writeln();
        return this;
    }

    @Override
    public RdcTokenWriter incTab() {
        printerHelper.incTab();
        return this;
    }

    @Override
    public RdcTokenWriter decTab() {
        printerHelper.decTab();
        return this;
    }

    @Override
    public void reset() {
        printerHelper.reset();
    }

    @Override
    public TokenWriter writeSpace() {
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
