package de.uni_passau.fim.se2.rdh.config;

import de.uni_passau.fim.se2.rdh.printer.CharacterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdcProbabilitiesTest {

    @Test
    void testSwap() {
        RdcProbabilities probabilities = new RdcProbabilities();
        probabilities.setSpaceInsteadOfNewline(1);
        probabilities.setNewLineInsteadOfSpace(0);

        probabilities.setIncTabInsteadOfDecTab(1);
        probabilities.setDecTabInsteadOfIncTab(0);

        assertAll(
                () -> assertFalse(probabilities.shouldSwap(CharacterType.SPACE)),
                () -> assertTrue(probabilities.shouldSwap(CharacterType.NEWLINE)),
                () -> assertFalse(probabilities.shouldSwap(CharacterType.INC_TAB)),
                () -> assertTrue(probabilities.shouldSwap(CharacterType.DEC_TAB))
        );
    }

}