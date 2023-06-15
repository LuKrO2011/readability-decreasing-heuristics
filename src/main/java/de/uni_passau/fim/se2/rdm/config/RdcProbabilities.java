package de.uni_passau.fim.se2.rdm.config;

import lombok.Data;
import spoon.reflect.visitor.CharacterType;

import java.util.List;
import java.util.Random;

@Data
public class RdcProbabilities {

    private final Random random;

    private List<Double> newline;

    // TODO: Distinguish inctab and dectab
    private List<Double> tab;

    private List<Double> space;

    private double newLineInsteadOfSpace = 0;
    private double spaceInsteadOfNewline = 0;

    public RdcProbabilities() {
        this(new Random());
    }

    public RdcProbabilities(Random random) {
        this.random = random;
    }

    public int getRandomNumberOf(CharacterType characterType) {
        List<Double> probabilities = getProbabilitiesFor(characterType);

        Random random = new Random();
        double randomValue = random.nextDouble();

        double accumulatedProbability = 0.0;
        for (int i = 0; i < probabilities.size(); i++) {
            double probability = probabilities.get(i);
            accumulatedProbability += probability;

            if (randomValue <= accumulatedProbability) {
                return i; // Return the index as the number of characters
            }
        }

        return 1; // Default to 1 character
    }

    // TODO: How can this be done more elegant using a enum constructor?
    private List<Double> getProbabilitiesFor(CharacterType characterType) {
        List<Double> probabilities;
        switch (characterType) {
            case NEWLINE:
                probabilities = newline;
                break;
            case TAB:
                probabilities = tab;
                break;
            case SPACE:
                probabilities = space;
                break;
            default:
                throw new RuntimeException("Unknown character type");
        }
        return probabilities;
    }
}
