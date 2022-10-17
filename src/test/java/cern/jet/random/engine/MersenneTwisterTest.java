package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static cern.jet.random.engine.RandomSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class MersenneTwisterTest {

    @Test
    void testConstructor() throws Exception {
        val atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        var mt = new MersenneTwister(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        var f = mt.getClass().getDeclaredField("stateVector");
        f.setAccessible(true);
        assertNotNull(f);
        var value = f.get(mt);
        assertEquals(312, Array.getLength(value));
        assertEquals(5489L, Array.get(value, 0));
        f = mt.getClass().getDeclaredField("stateVectorIndex");
        f.setAccessible(true);
        value = f.get(mt);
        assertEquals(312, value);

        mt = new MersenneTwister();
        f = mt.getClass().getDeclaredField("stateVector");
        f.setAccessible(true);
        assertNotNull(f);
        value = f.get(mt);
        assertEquals(312, Array.getLength(value));
        assertEquals(5489L, Array.get(value, 0));
        f = mt.getClass().getDeclaredField("stateVectorIndex");
        f.setAccessible(true);
        value = f.get(mt);
        assertEquals(312, value);

        mt = new MersenneTwister(100L);
        f = mt.getClass().getDeclaredField("stateVector");
        f.setAccessible(true);
        assertNotNull(f);
        value = f.get(mt);
        assertEquals(312, Array.getLength(value));
        assertEquals(100L, Array.get(value, 0));
        f = mt.getClass().getDeclaredField("stateVectorIndex");
        f.setAccessible(true);
        value = f.get(mt);
        assertEquals(312, value);

        mt = new MersenneTwister(100);
        f = mt.getClass().getDeclaredField("stateVector");
        f.setAccessible(true);
        assertNotNull(f);
        value = f.get(mt);
        assertEquals(312, Array.getLength(value));
        assertEquals(100L, Array.get(value, 0));
        f = mt.getClass().getDeclaredField("stateVectorIndex");
        f.setAccessible(true);
        value = f.get(mt);
        assertEquals(312, value);

    }

    @Test
    void testConstructor9() {
        assertThrows(NullPointerException.class, () -> new MersenneTwister(null));
    }

    @Test
    void actualValues() {
        val mt = new MersenneTwister();
        mt.setSeed(new int[]{0x12345, 0x23456, 0x34567, 0x45678});
        val size = 1000;

        val actualLongValues = IntStream.range(0, size).mapToLong(i -> mt.nextLong()).toArray();
        val actualDoubleValues = IntStream.range(0, size).mapToDouble(i -> mt.nextDouble()).toArray();

        try (val is = getClass().getClassLoader().getResourceAsStream("long.txt")) {
            assert is != null;
            val referenceLongValues = new BufferedReader(new InputStreamReader(is)).lines().parallel().flatMapToLong(num ->
                LongStream.of(Long.parseUnsignedLong(num))).toArray();
            assertArrayEquals(referenceLongValues, actualLongValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (val is = getClass().getClassLoader().getResourceAsStream("double.txt")) {
            assert is != null;
            val referenceDoubleValues = new BufferedReader(new InputStreamReader(is)).lines().parallel()
                .flatMapToDouble(num -> DoubleStream.of(Double.parseDouble(num))).toArray();
            for (int i = 0; i < 1000; i++)
                assertTrue(Math.abs(referenceDoubleValues[i] - actualDoubleValues[i]) < 1.0E-15);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testClone() {
        val a = new MersenneTwister();
        val b = a.clone();
        val nextA = a.nextLong();
        val nextB = b.nextLong();
        assertEquals(nextA, nextB);
    }

    @Test
    void testNextLong() {
        assertEquals(-3932459287431434586L, (new MersenneTwister()).nextLong());
    }

    @Test
    void nextDouble() {
        assertEquals(0x1.fffffffffffffp-2, doubleFromLongCC(Long.MAX_VALUE));
        assertEquals(0x1.fffffffffff9fp-2, doubleFromLongCC(Long.MAX_VALUE - 100_000L));
        assertEquals(0x1.fffffffffffffp-2, doubleFromLongCC(Long.MAX_VALUE - 1));
        assertEquals(0x1.ffffffffffffdp-3, doubleFromLongCC(Long.MAX_VALUE / 2));

        assertEquals(0x1.0000000000001p-1, doubleFromLongCC(Long.MIN_VALUE));
        assertEquals(0x1.fffffffffff9fp-2, doubleFromLongCC(Long.MIN_VALUE - 100_000L));
        assertEquals(0x1.0000000000001p-1, doubleFromLongCC(Long.MIN_VALUE + 1));
        assertEquals(0x1.8000000000001p-1, doubleFromLongCC(Long.MIN_VALUE / 2));

        assertEquals(0.d, doubleFromLongCC(0L));
        assertEquals(0.d, doubleFromLongCC(1L));
        assertEquals(0.d, doubleFromLongCC(2L));
        assertEquals(1.d, doubleFromLongCC(-1L));
        assertEquals(1.d, doubleFromLongCC(-2L));

        assertEquals(0x1.8000000000001p-48, doubleFromLongCC(2L + 100_000L));
        assertEquals(0x1.fffffffffffdp-1, doubleFromLongCC(-2L - 100000L));
    }

    @Test
    void testDoubleFromLongOpenRight() {
        assertNotEquals(doubleFromLongCC(Long.MAX_VALUE), doubleFromLongCO(Long.MAX_VALUE));
        assertNotEquals(doubleFromLongCC(Long.MAX_VALUE - 100_000L), doubleFromLongCO(Long.MAX_VALUE - 100_000L));
        assertNotEquals(doubleFromLongCC(Long.MAX_VALUE - 1), doubleFromLongCO(Long.MAX_VALUE - 1));
        assertNotEquals(doubleFromLongCC(Long.MAX_VALUE / 2), doubleFromLongCO(Long.MAX_VALUE / 2));

        assertNotEquals(doubleFromLongCC(Long.MIN_VALUE), doubleFromLongCO(Long.MIN_VALUE));
        assertNotEquals(doubleFromLongCC(Long.MIN_VALUE - 100_000L), doubleFromLongCO(Long.MIN_VALUE - 100_000L));
        assertNotEquals(doubleFromLongCC(Long.MIN_VALUE + 1), doubleFromLongCO(Long.MIN_VALUE + 1));
        assertNotEquals(doubleFromLongCC(Long.MIN_VALUE / 2), doubleFromLongCO(Long.MIN_VALUE / 2));

        assertEquals(doubleFromLongCC(0L), doubleFromLongCO(0L));
        assertEquals(doubleFromLongCC(1L), doubleFromLongCO(1L));
        assertEquals(doubleFromLongCC(2L), doubleFromLongCO(2L));
        assertNotEquals(doubleFromLongCC(-1L), doubleFromLongCO(-1L));
        assertNotEquals(doubleFromLongCC(-2L), doubleFromLongCO(-2L));

        assertNotEquals(doubleFromLongCC(2L + 100_000L), doubleFromLongCO(2L + 100_000L));
        assertNotEquals(doubleFromLongCC(-2L - 100000L), doubleFromLongCO(-2L - 100000L));
    }

    @Test
    void testDoubleFromLongOpen() {
        assertNotEquals(0, doubleFromLongOO(0L));
        assertNotEquals(1., doubleFromLongOO(-1L));
        assertEquals(0x1.0p-53, doubleFromLongOO(0L));
        assertEquals(0x1.fffffffffffffp-1, doubleFromLongOO(-1L));
    }

    @Test
    void testDoubleFromLongOpenLeft() {
        assertNotEquals(0, doubleFromLongOC(0L));
        assertEquals(0x1.0p-53, doubleFromLongOC(0L));
        assertEquals(0x1.0p0, doubleFromLongOC(-1L));
    }

    @Test
    void setSeed() {
    }

    @Test
    void testSetSeed() {
    }

    @Test
    void testSetSeed1() {
    }

    @Test
    void testBuffer() {
    }
}
