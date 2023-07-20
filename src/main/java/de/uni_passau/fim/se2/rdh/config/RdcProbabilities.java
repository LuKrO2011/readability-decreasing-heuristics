package de.uni_passau.fim.se2.rdh.config;

import de.uni_passau.fim.se2.rdh.util.Randomness;
import de.uni_passau.fim.se2.rdh.validators.Probability;
import de.uni_passau.fim.se2.rdh.printer.CharacterType;
import de.uni_passau.fim.se2.rdh.validators.ProbabilityList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public final class RdcProbabilities {

    // Probabilities of changing the number of characters
    @ProbabilityList
    private List<Double> newline = List.of(0.0, 1.0);
    @ProbabilityList
    private List<Double> incTab = List.of(0.0, 1.0);
    @ProbabilityList
    private List<Double> decTab = List.of(0.0, 1.0);
    @ProbabilityList
    private List<Double> space = List.of(0.0, 1.0);

    // Probabilities of swapping characters with others
    @Probability
    private double newLineInsteadOfSpace;
    @Probability
    private double spaceInsteadOfNewline;
    @Probability
    private double incTabInsteadOfDecTab;
    @Probability
    private double decTabInsteadOfIncTab;


    // Probabilities of refactorings
    @Probability
    private double renameVariable;
    @Probability
    private double renameField;
    @Probability
    private double renameMethod;
    @Probability
    private double inlineMethod;
    @Probability
    private double removeComment;
    @Probability
    private double add0;
    @Probability
    private double insertBraces;
    @Probability
    private double starImport;
    @Probability
    private double inlineField;
    @Probability
    private double partiallyEvaluate;

    // TODO: writeFullyQualifiedName
    // TODO: writeStarImport

    /**
     * Creates a new {@link RdcProbabilities} object with the default probabilities (= no modification).
     */
    public RdcProbabilities() {
    }

    /**
     * A copy constructor that creates a new {@link RdcProbabilities} object with the given probabilities.
     *
     * @param probabilities The probabilities to use/copy.
     */
    public RdcProbabilities(final RdcProbabilities probabilities) {
        this.newline = new ArrayList<>(probabilities.newline);
        this.incTab = new ArrayList<>(probabilities.incTab);
        this.decTab = new ArrayList<>(probabilities.decTab);
        this.space = new ArrayList<>(probabilities.space);
        this.renameVariable = probabilities.renameVariable;
        this.renameField = probabilities.renameField;
        this.renameMethod = probabilities.renameMethod;
        this.inlineMethod = probabilities.inlineMethod;
        this.removeComment = probabilities.removeComment;
        this.add0 = probabilities.add0;
        this.insertBraces = probabilities.insertBraces;
        this.starImport = probabilities.starImport;
        this.inlineField = probabilities.inlineField;
        this.partiallyEvaluate = probabilities.partiallyEvaluate;
        this.newLineInsteadOfSpace = probabilities.newLineInsteadOfSpace;
        this.spaceInsteadOfNewline = probabilities.spaceInsteadOfNewline;
        this.incTabInsteadOfDecTab = probabilities.incTabInsteadOfDecTab;
        this.decTabInsteadOfIncTab = probabilities.decTabInsteadOfIncTab;
    }

    /**
     * Returns a random number of characters for the given {@link CharacterType}.
     *
     * @param characterType The {@link CharacterType} to get the number of characters for.
     * @return A random number of characters.
     */
    public int getRandomNumberOf(final CharacterType characterType) {
        List<Double> probabilities = getProbabilitiesFor(characterType);

        double randomValue = Randomness.nextDouble();

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
    private List<Double> getProbabilitiesFor(final CharacterType characterType) {
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
    public boolean shouldSwap(final CharacterType characterType) {
        double probability = getSwapProbabilityFor(characterType);
        return Randomness.nextDouble() <= probability;
    }

    /**
     * Returns the probability for swapping the given {@link CharacterType} with another character.
     *
     * @param characterType The {@link CharacterType} to get the probability for.
     * @return The probability for swapping the given {@link CharacterType} with another character.
     */
    // TODO: How can this be done more elegant using a enum constructor?
    private double getSwapProbabilityFor(final CharacterType characterType) {
        return switch (characterType) {
            case NEWLINE -> newLineInsteadOfSpace;
            case SPACE -> spaceInsteadOfNewline;
            case INC_TAB -> incTabInsteadOfDecTab;
            case DEC_TAB -> decTabInsteadOfIncTab;
        };
    }

    /**
     * Returns whether a variable should be renamed.
     *
     * @return Whether a variable should be renamed.
     */
    public boolean shouldRenameVariable() {
        return Randomness.nextDouble() <= renameVariable;
    }

    /**
     * Returns whether a field (global variable) should be renamed.
     *
     * @return Whether a field (global variable) should be renamed.
     */
    public boolean shouldRenameField() {
        return Randomness.nextDouble() <= renameField;
    }

    /**
     * Returns whether a method should be renamed.
     *
     * @return Whether a method should be renamed.
     */
    public boolean shouldRenameMethod() {
        return Randomness.nextDouble() <= renameMethod;
    }

    /**
     * Returns whether a method should be inlined.
     *
     * @return Whether a method should be inlined.
     */
    public boolean shouldInlineMethod() {
        return Randomness.nextDouble() <= inlineMethod;
    }

    /**
     * Returns whether a comment should be removed.
     *
     * @return Whether a comment should be removed.
     */
    public boolean shouldRemoveComment() {
        return Randomness.nextDouble() <= removeComment;
    }

    /**
     * Returns whether a new operation should be inserted. The new operation should not change the semantics of the
     * program.
     *
     * @return Whether a new operation should be inserted.
     */
    public boolean shouldInsertOperation() {
        return Randomness.nextDouble() <= add0;
    }

    /**
     * Returns whether braces should be inserted. The braces might be inserted anyway if needed for the semantics of the
     * program. However, if this method returns true, braces should be inserted even if they are not needed.
     *
     * @return Whether braces should be inserted.
     */
    public boolean shouldInsertBraces() {
        return Randomness.nextDouble() <= insertBraces;
    }

    /**
     * Returns whether a star import should be replaced with a normal import.
     *
     * @return Whether a star import should be replaced with a normal import.
     */
    public boolean shouldReplaceWithStarImport() {
        return Randomness.nextDouble() <= starImport;
    }

    /**
     * Returns whether a field should be inlined.
     *
     * @return Whether a field should be inlined.
     */
    public boolean shouldInlineField() {
        return Randomness.nextDouble() <= inlineField;
    }

    /**
     * Returns whether an expression should be partially evaluated.
     *
     * @return Whether an expression should be partially evaluated.
     */
    public boolean shouldPartiallyEvaluate() {
        return Randomness.nextDouble() <= partiallyEvaluate;
    }

    public List<Double> getNewline() {
        return Collections.unmodifiableList(newline);
    }

    public void setNewline(final List<Double> newline) {
        this.newline = new ArrayList<>(newline);
    }

    public List<Double> getIncTab() {
        return Collections.unmodifiableList(incTab);
    }

    public void setIncTab(final List<Double> incTab) {
        this.incTab = new ArrayList<>(incTab);
    }

    public List<Double> getDecTab() {
        return Collections.unmodifiableList(decTab);
    }

    public void setDecTab(final List<Double> decTab) {
        this.decTab = new ArrayList<>(decTab);
    }

    public List<Double> getSpace() {
        return Collections.unmodifiableList(space);
    }

    public void setSpace(final List<Double> space) {
        this.space = new ArrayList<>(space);
    }

    public double getNewLineInsteadOfSpace() {
        return newLineInsteadOfSpace;
    }

    public void setNewLineInsteadOfSpace(final double newLineInsteadOfSpace) {
        this.newLineInsteadOfSpace = newLineInsteadOfSpace;
    }

    public double getSpaceInsteadOfNewline() {
        return spaceInsteadOfNewline;
    }

    public void setSpaceInsteadOfNewline(final double spaceInsteadOfNewline) {
        this.spaceInsteadOfNewline = spaceInsteadOfNewline;
    }

    public double getIncTabInsteadOfDecTab() {
        return incTabInsteadOfDecTab;
    }

    public void setIncTabInsteadOfDecTab(final double incTabInsteadOfDecTab) {
        this.incTabInsteadOfDecTab = incTabInsteadOfDecTab;
    }

    public double getDecTabInsteadOfIncTab() {
        return decTabInsteadOfIncTab;
    }

    public void setDecTabInsteadOfIncTab(final double decTabInsteadOfIncTab) {
        this.decTabInsteadOfIncTab = decTabInsteadOfIncTab;
    }

    public double getRenameVariable() {
        return renameVariable;
    }

    public void setRenameVariable(final double renameVariable) {
        this.renameVariable = renameVariable;
    }

    public double getRenameField() {
        return renameField;
    }

    public void setRenameField(final double renameField) {
        this.renameField = renameField;
    }

    public double getRenameMethod() {
        return renameMethod;
    }

    public void setRenameMethod(final double renameMethod) {
        this.renameMethod = renameMethod;
    }

    public double getInlineMethod() {
        return inlineMethod;
    }

    public void setInlineMethod(final double inlineMethod) {
        this.inlineMethod = inlineMethod;
    }

    public double getRemoveComment() {
        return removeComment;
    }

    public void setRemoveComment(final double removeComment) {
        this.removeComment = removeComment;
    }

    public double getAdd0() {
        return add0;
    }

    public void setAdd0(final double add0) {
        this.add0 = add0;
    }

    public double getInsertBraces() {
        return insertBraces;
    }

    public void setInsertBraces(final double insertBraces) {
        this.insertBraces = insertBraces;
    }

    public double getStarImport() {
        return starImport;
    }

    public void setStarImport(final double starImport) {
        this.starImport = starImport;
    }

    public void setInlineField(final double inlineField) {
        this.inlineField = inlineField;
    }

    public double getInlineField() {
        return inlineField;
    }

    public double getPartiallyEvaluate() {
        return partiallyEvaluate;
    }

    public void setPartiallyEvaluate(final double partiallyEvaluate) {
        this.partiallyEvaluate = partiallyEvaluate;
    }
}
