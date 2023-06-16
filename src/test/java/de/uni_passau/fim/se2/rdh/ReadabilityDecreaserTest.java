package de.uni_passau.fim.se2.rdh;

import org.junit.jupiter.api.Test;

class ReadabilityDecreaserTest {

    @Test
    void testMain() {
        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser("src/test/resources/code");
        readabilityDecreaser.process("HelloWorld.java");
    }

}