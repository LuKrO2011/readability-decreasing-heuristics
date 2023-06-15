package de.uni_passau.fim.se2.rdm.config;

import lombok.Data;
import de.uni_passau.fim.se2.rdm.printer.CharacterType;

import java.util.List;
import java.util.Random;

@Data
public class RdcProbabilities {

    private final Random random;

    // Probabilities of changing the number of characters
    private List<Double> newline;
    private List<Double> incTab;
    private List<Double> decTab;
    private List<Double> space;

    // Probabilities of swapping characters with others
    private double newLineInsteadOfSpace;
    private double spaceInsteadOfNewline;
    private double incTabInsteadOfDecTab;
    private double decTabInsteadOfIncTab;

    // Probabilities of refactorings
    private double renameVariable;
    private double renameField;
    private double renameMethod;
    private double inlineMethod;
    private double removeComment;
    // TODO: writeFullyQualifiedName
    // TODO: writeStarImport

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
            case INC_TAB:
                probabilities = incTab;
                break;
            case DEC_TAB:
                probabilities = decTab;
                break;
            case SPACE:
                probabilities = space;
                break;
            default:
                throw new RuntimeException("Unknown character type");
        }
        return probabilities;
    }

    public boolean shouldSwap(CharacterType characterType) {
        double probability = getSwapProbabilityFor(characterType);
        return random.nextDouble() <= probability;
    }

    private double getSwapProbabilityFor(CharacterType characterType) {
        double probability;
        switch (characterType) {
            case NEWLINE:
                probability = newLineInsteadOfSpace;
                break;
            case SPACE:
                probability = spaceInsteadOfNewline;
                break;
            case INC_TAB:
                probability = incTabInsteadOfDecTab;
                break;
            case DEC_TAB:
                probability = decTabInsteadOfIncTab;
                break;
            default:
                throw new RuntimeException("Unknown character type");
        }
        return probability;
    }

    public boolean shouldRenameVariable() {
        return random.nextDouble() <= renameVariable;
    }

    public boolean shouldRenameField() {
        return random.nextDouble() <= renameField;
    }

    public boolean shouldRenameMethod() {
        return random.nextDouble() <= renameMethod;
    }

    public boolean shouldInlineMethod() {
        return random.nextDouble() <= inlineMethod;
    }

    public boolean shouldRemoveComment() {
        return random.nextDouble() <= removeComment;
    }
}
