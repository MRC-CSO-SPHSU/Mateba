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
        assertThrows(NullPointerException.class, () -> new MersenneTwister((Date) null));
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
        assertEquals(0x1.fffffffffffffp-2, doubleFromLongClosed(Long.MAX_VALUE));
        assertEquals(0x1.fffffffffff9fp-2, doubleFromLongClosed(Long.MAX_VALUE - 100_000L));
        assertEquals(0x1.fffffffffffffp-2, doubleFromLongClosed(Long.MAX_VALUE - 1));
        assertEquals(0x1.ffffffffffffdp-3, doubleFromLongClosed(Long.MAX_VALUE / 2));

        assertEquals(0x1.0000000000001p-1, doubleFromLongClosed(Long.MIN_VALUE));
        assertEquals(0x1.fffffffffff9fp-2, doubleFromLongClosed(Long.MIN_VALUE - 100_000L));
        assertEquals(0x1.0000000000001p-1, doubleFromLongClosed(Long.MIN_VALUE + 1));
        assertEquals(0x1.8000000000001p-1, doubleFromLongClosed(Long.MIN_VALUE / 2));

        assertEquals(0.d, doubleFromLongClosed(0L));
        assertEquals(0.d, doubleFromLongClosed(1L));
        assertEquals(0.d, doubleFromLongClosed(2L));
        assertEquals(1.d, doubleFromLongClosed(-1L));
        assertEquals(1.d, doubleFromLongClosed(-2L));

        assertEquals(0x1.8000000000001p-48, doubleFromLongClosed(2L + 100_000L));
        assertEquals(0x1.fffffffffffdp-1, doubleFromLongClosed(-2L - 100000L));
    }

    @Test
    void testDoubleFromLongOpenRight() {
        assertNotEquals(doubleFromLongClosed(Long.MAX_VALUE), doubleFromLongOpenRight(Long.MAX_VALUE));
        assertNotEquals(doubleFromLongClosed(Long.MAX_VALUE - 100_000L), doubleFromLongOpenRight(Long.MAX_VALUE - 100_000L));
        assertNotEquals(doubleFromLongClosed(Long.MAX_VALUE - 1), doubleFromLongOpenRight(Long.MAX_VALUE - 1));
        assertNotEquals(doubleFromLongClosed(Long.MAX_VALUE / 2), doubleFromLongOpenRight(Long.MAX_VALUE / 2));

        assertNotEquals(doubleFromLongClosed(Long.MIN_VALUE), doubleFromLongOpenRight(Long.MIN_VALUE));
        assertNotEquals(doubleFromLongClosed(Long.MIN_VALUE - 100_000L), doubleFromLongOpenRight(Long.MIN_VALUE - 100_000L));
        assertNotEquals(doubleFromLongClosed(Long.MIN_VALUE + 1), doubleFromLongOpenRight(Long.MIN_VALUE + 1));
        assertNotEquals(doubleFromLongClosed(Long.MIN_VALUE / 2), doubleFromLongOpenRight(Long.MIN_VALUE / 2));

        assertEquals(doubleFromLongClosed(0L), doubleFromLongOpenRight(0L));
        assertEquals(doubleFromLongClosed(1L), doubleFromLongOpenRight(1L));
        assertEquals(doubleFromLongClosed(2L), doubleFromLongOpenRight(2L));
        assertNotEquals(doubleFromLongClosed(-1L), doubleFromLongOpenRight(-1L));
        assertNotEquals(doubleFromLongClosed(-2L), doubleFromLongOpenRight(-2L));

        assertNotEquals(doubleFromLongClosed(2L + 100_000L), doubleFromLongOpenRight(2L + 100_000L));
        assertNotEquals(doubleFromLongClosed(-2L - 100000L), doubleFromLongOpenRight(-2L - 100000L));
    }

    @Test
    void testDoubleFromLongOpen() {
        assertNotEquals(0, doubleFromLongOpen(0L));
        assertNotEquals(1., doubleFromLongOpen(-1L));
        assertEquals(0x1.0p-53, doubleFromLongOpen(0L));
        assertEquals(0x1.fffffffffffffp-1, doubleFromLongOpen(-1L));
    }

    @Test
    void testDoubleFromLongOpenLeft() {
        assertNotEquals(0, doubleFromLongOpenLeft(0L));
        assertEquals(0x1.0p-53, doubleFromLongOpenLeft(0L));
        assertEquals(0x1.0p0, doubleFromLongOpenLeft(-1L));
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
