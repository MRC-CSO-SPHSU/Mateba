package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MersenneTwisterTest {

    @Test
    void actualValues() {
        val mt = new MersenneTwister();
        mt.setSeed(new int[]{0x12345, 0x23456, 0x34567, 0x45678});
        val size = 1000;

        final long[] referenceLongValues;
        val actualLongValues = IntStream.range(0, size).mapToLong(i -> mt.nextLong()).toArray();

        final double[] referenceDoubleValues;
        val actualDoubleValues = IntStream.range(0, size).mapToDouble(i -> mt.nextDouble()).toArray();

        try (val is = getClass().getClassLoader().getResourceAsStream("long.txt")) {
            assert is != null;
            referenceLongValues = new BufferedReader(new InputStreamReader(is)).lines().parallel().flatMapToLong(num ->
                LongStream.of(Long.parseUnsignedLong(num))).toArray();
            assertArrayEquals(referenceLongValues, actualLongValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (val is = getClass().getClassLoader().getResourceAsStream("double.txt")) {
            assert is != null;
            referenceDoubleValues = new BufferedReader(new InputStreamReader(is)).lines().parallel()
                .flatMapToDouble(num -> DoubleStream.of(Double.parseDouble(num))).toArray();
            assertArrayEquals(referenceDoubleValues, actualDoubleValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testClone() {
        val a = new MersenneTwister();

        System.out.println(a.nextLong());
        //a.nextLong();
    }

    @Test
    void nextLong() {
        class A implements Cloneable{
            final long[] stateVector = new long[2];

            @Override
            public A clone() {
                try {
                    return (A) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError();
                }
            }
        }

        val q = new A();
        val q1 = q.clone();
        q.stateVector[0] = 3;
        assertEquals(3, q.stateVector[0] );
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
}
