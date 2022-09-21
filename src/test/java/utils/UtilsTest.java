package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTest {

    @Test
    void testAbsInt() {
        assertThrows(IllegalArgumentException.class, () -> Utils.absInt(Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, Utils.absInt(Integer.MIN_VALUE + 1));
        assertEquals(Integer.MAX_VALUE, Utils.absInt(Integer.MAX_VALUE));
        assertEquals(0, Utils.absInt(0));
        assertEquals(1, Utils.absInt(1));
        assertEquals(1, Utils.absInt(-1));
    }

    @Test
    void testAbsLong() {
        assertThrows(IllegalArgumentException.class, () -> Utils.absLong(Long.MIN_VALUE));
        assertEquals(Long.MAX_VALUE, Utils.absLong(Long.MIN_VALUE + 1));
        assertEquals(Long.MAX_VALUE, Utils.absLong(Long.MAX_VALUE));
        assertEquals(0L, Utils.absLong(0L));
        assertEquals(1L, Utils.absLong(1L));
        assertEquals(1L, Utils.absLong(-1L));
    }
}

