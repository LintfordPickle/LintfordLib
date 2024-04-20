package net.lintfordlib.core.maths;

import junit.framework.TestCase;
import org.junit.jupiter.api.DisplayName;

public class Vector2fTest extends TestCase {

    @DisplayName("Test Vector2f length function when components are 0")
    public void WhenComponentsOfVector2fAreBothZeroThenResultShouldBeZero() {
        Vector2f v0 = new Vector2f(0,0);

        final var result = v0.len();

        assertEquals(0.0f, result);
    }
}