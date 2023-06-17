package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomnessTest {

    @RepeatedTest(10)
    void randomStringOnlyLowerCase() {
        assertThat(Randomness.randomString(20)).isAlphabetic().isLowerCase();
    }

    @Test
    void randomIntSeeded() {
        Randomness.setSeed(1);

        assertThat(Randomness.nextInt(10_000)).isEqualTo(8_985);
        assertThat(Randomness.nextInt(10_000)).isEqualTo(4_588);
        assertThat(Randomness.nextInt(10_000)).isEqualTo(1_847);
        assertThat(Randomness.nextInt(10_000)).isEqualTo(313);
        assertThat(Randomness.nextInt(10_000)).isEqualTo(4_254);
    }
}
