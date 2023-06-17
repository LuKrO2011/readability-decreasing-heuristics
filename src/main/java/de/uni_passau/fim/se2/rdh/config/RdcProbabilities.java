package de.uni_passau.fim.se2.rdh.config;

import lombok.Data;
import de.uni_passau.fim.se2.rdh.printer.CharacterType;

import java.util.List;
import java.util.Random;

/**
 * This class contains the probabilities for the refactorings and the probabilities for changing the number of
 * characters.
 * <p>
 * The probabilities are read from a YAML file. The file is read by the {@link YamlLoaderSaver}.
 * </p>
 * <p>
 * The probabilities for changing the number of a certain character are stored in lists. The index of the list
 * represents the number of characters. The value at the index is the probability for the number of characters - 1.
 * <br>
 * Example: The probability for no newline is 0.5, the probability for 1 newline (original behaviour) is 0.3 and the
 * probability for 2 newlines is 0.2. Then the list would look like this: [0.5, 0.3, 0.2].
 * </p>
 */
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

    /**
     * Creates a new {@link RdcProbabilities} with a new {@link Random} object.
     */
    public RdcProbabilities() {
        this(new Random());
    }

    /**
     * Creates a new {@link RdcProbabilities} with the given {@link Random} object.
     *
     * @param random The {@link Random} object to use.
     */
    public RdcProbabilities(Random random) {
        this.random = random;
    }

    /**
     * Returns a random number of characters for the given {@link CharacterType}.
     *
     * @param characterType The {@link CharacterType} to get the number of characters for.
     * @return A random number of characters.
     */
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

    /**
     * Returns the probabilities for the given {@link CharacterType}.
     *
     * @param characterType The {@link CharacterType} to get the probabilities for.
     * @return The probabilities for the given {@link CharacterType}.
     */
    // TODO: How can this be done more elegant using a enum constructor?
    private List<Double> getProbabilitiesFor(CharacterType characterType) {
        return switch (characterType) {
            case NEWLINE -> newline;
            case INC_TAB -> incTab;
            case DEC_TAB -> decTab;
            case SPACE -> space;
        };
    }

    /**
     * Returns whether the given {@link CharacterType} should be swapped with another character.
     *
     * @param characterType The {@link CharacterType} to check.
     * @return Whether the given {@link CharacterType} should be swapped with another character.
     */
    public boolean shouldSwap(CharacterType characterType) {
        double probability = getSwapProbabilityFor(characterType);
        return random.nextDouble() <= probability;
    }

    /**
     * Returns the probability for swapping the given {@link CharacterType} with another character.
     *
     * @param characterType The {@link CharacterType} to get the probability for.
     * @return The probability for swapping the given {@link CharacterType} with another character.
     */
    // TODO: How can this be done more elegant using a enum constructor?
    private double getSwapProbabilityFor(CharacterType characterType) {
        return switch (characterType) {
            case NEWLINE -> newLineInsteadOfSpace;
            case SPACE -> spaceInsteadOfNewline;
            case INC_TAB -> incTabInsteadOfDecTab;
            case DEC_TAB -> decTabInsteadOfIncTab;
            default -> throw new RuntimeException("Unknown character type");
        };
    }

    /**
     * Returns whether a variable should be renamed.
     *
     * @return Whether a variable should be renamed.
     */
    public boolean shouldRenameVariable() {
        return random.nextDouble() <= renameVariable;
    }

    /**
     * Returns whether a field (global variable) should be renamed.
     *
     * @return Whether a field (global variable) should be renamed.
     */
    public boolean shouldRenameField() {
        return random.nextDouble() <= renameField;
    }

    /**
     * Returns whether a method should be renamed.
     *
     * @return Whether a method should be renamed.
     */
    public boolean shouldRenameMethod() {
        return random.nextDouble() <= renameMethod;
    }

    /**
     * Returns whether a method should be inlined.
     *
     * @return Whether a method should be inlined.
     */
    public boolean shouldInlineMethod() {
        return random.nextDouble() <= inlineMethod;
    }

    /**
     * Returns whether a comment should be removed.
     *
     * @return Whether a comment should be removed.
     */
    public boolean shouldRemoveComment() {
        return random.nextDouble() <= removeComment;
    }
}
