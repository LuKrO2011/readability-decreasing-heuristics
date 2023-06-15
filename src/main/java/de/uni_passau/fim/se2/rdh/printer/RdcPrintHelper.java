package de.uni_passau.fim.se2.rdh.printer;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.PrinterHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RdcPrintHelper extends PrinterHelper {

    private final Random random = new Random();
    private final RdcProbabilities probabilities;

    private String lineSeparator = System.getProperty("line.separator");

    /**
     * Mapping for line numbers.
     */
    private Map<Integer, Integer> lineNumberMapping = new HashMap<>();

    public RdcPrintHelper(Environment env, RdcProbabilities probabilities) {
        super(env);
        this.probabilities = probabilities;
    }

    public RdcPrintHelper(Environment env) {
        this(env, new RdcProbabilities());
    }

    @Override
    public PrinterHelper write(String s) {
        return super.write(s);
    }

    @Override
    public PrinterHelper write(char c) {
        return super.write(c);
    }

    @Override
    public PrinterHelper writeln() {
        /*if (random.nextDouble() < probabilities.getDoubleNewLine()) {
            return super.writeln().writeln();
        }

        if (random.nextDouble() < probabilities.getNoNewLine()) {
            return this;
        }*/

        return super.writeln();
    }

    @Override
    public PrinterHelper incTab() {
        /*if (random.nextDouble() < probabilities.getDoubleTab()) {
            return super.incTab().incTab();
        }

        if (random.nextDouble() < probabilities.getNoTab()) {
            return this;
        }*/

        return super.incTab();
    }

    @Override
    public PrinterHelper decTab() {
        /*if (super.getTabCount() >= 2 && random.nextDouble() < probabilities.getDoubleTab()) {
            return super.decTab().decTab();
        }

        if (random.nextDouble() < probabilities.getNoTab()) {
            return this;
        }*/

        return super.decTab();
    }

    // TODO?
    @Override
    public PrinterHelper setTabCount(int tabCount) {
        /*if (random.nextDouble() < probabilities.getDoubleTab()) {
            return super.setTabCount(tabCount + 1);
        }

        if (random.nextDouble() < probabilities.getNoTab()) {
            return super.setTabCount(tabCount - 1);
        }*/

        return super.setTabCount(tabCount);
    }

    // TODO?
    @Override
    public boolean removeLine() {
        return super.removeLine();
    }

    @Override
    public PrinterHelper adjustStartPosition(CtElement e) {
        /*if (random.nextDouble() < probabilities.getLessStartPosition()) {
            return super.adjustStartPosition(e.getParent());
        }

        if (!e.getDirectChildren().isEmpty() && random.nextDouble() < probabilities.getMoreStartPosition()) {
            return super.adjustStartPosition(e.getDirectChildren().get(0));
        }*/

        return super.adjustStartPosition(e);
    }

    // TODO?
    @Override
    public PrinterHelper adjustEndPosition(CtElement e) {
        return super.adjustEndPosition(e);
    }

    // TODO?
    @Override
    public void mapLine(CtElement e, CtCompilationUnit unitExpected) {
        super.mapLine(e, unitExpected);
    }

    // TODO?
    @Override
    public void putLineNumberMapping(int valueLine) {
        super.putLineNumberMapping(valueLine);
    }

    @Override
    public void writeSpace() {
        /*if(random.nextDouble() < probabilities.getDoubleSpace()) {
            super.writeSpace();
            super.writeSpace();
        }

        if(random.nextDouble() < probabilities.getNoSpace()) {
            return;
        }*/

        super.writeSpace();
    }

    // TODO?
    @Override
    public void setShouldWriteTabs(boolean b) {
        super.setShouldWriteTabs(b);
    }
}
