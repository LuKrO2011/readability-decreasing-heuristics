package de.uni_passau.fim.se2.rdm.printer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RdcProbabilities {
    private double doubleNewLine = 0.1;
    private double noNewLine = 0.1;
    private List<Double> newLine = List.of(0.1, 0.8, 0.1);

    private double doubleTab = 0.1;
    private double noTab = 0.1;

    private double doubleSpace = 0.1;
    private double noSpace = 0.1;
}
