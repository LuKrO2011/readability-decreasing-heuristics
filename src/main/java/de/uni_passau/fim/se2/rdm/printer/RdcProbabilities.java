package de.uni_passau.fim.se2.rdm.printer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// TODO: Use toml? https://toml.io/en/

@Data
public class RdcProbabilities {
    private double doubleNewLine = 0.2;
    private double noNewLine = 0.2;

    // TODO: Distinguish inctab and dectab
    private double doubleTab = 0.1;
    private double noTab = 0.1;

    private double doubleSpace = 0.2;
    private double noSpace = 0.2;

    private double newLineInsteadOfSpace = 0.2;
    private double spaceInsteadOfNewline = 0.2;
}
