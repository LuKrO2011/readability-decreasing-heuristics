package de.uni_passau.fim.se2.rdm.config;

import lombok.Data;

@Data
public class RdcProbabilities {
    private double doubleNewLine = 0;
    private double noNewLine = 0;

    // TODO: Distinguish inctab and dectab
    private double doubleTab = 0;
    private double noTab = 0;

    private double doubleSpace = 0;
    private double noSpace = 0;

    private double newLineInsteadOfSpace = 0;
    private double spaceInsteadOfNewline = 0;
}
