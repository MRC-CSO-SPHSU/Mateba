package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class RandomSupportTest {
    @Test
    void fastLog2() {
        val log22 = Math.log(2);
        IntStream.iterate(Integer.MAX_VALUE, i -> i >= 1, i -> i - 1).forEach(i ->
            assertEquals((int) (Math.log(i) / log22), RandomSupport.fastLog2(i)));
    }

    @Test
    void testFastLog2() {
        val log22 = Math.log(2);
        LongStream.iterate(Integer.MAX_VALUE, i -> i >= 1, i -> i - 1).forEach(i ->
            assertEquals((long) (Math.log(i) / log22), RandomSupport.fastLog2(i)));

        assertNotEquals((long) (Math.log(Long.MAX_VALUE >> 2) / log22), RandomSupport.fastLog2(Long.MAX_VALUE / 4));
        assertNotEquals((long) (Math.log(Long.MAX_VALUE >> 1) / log22), RandomSupport.fastLog2(Long.MAX_VALUE / 2));
        assertNotEquals((long) (Math.log(Long.MAX_VALUE) / log22), RandomSupport.fastLog2(Long.MAX_VALUE));
    }

    @Test
    void fastDivisionByPowerOfTwo() {
        for (int j = 0; j <= 30; j++) {
            val divisor = 1 << j;
            IntStream.iterate(Integer.MAX_VALUE, i -> i >= 0, i -> i - 1).forEach(i ->
                assertEquals((int) ((double) i / divisor), RandomSupport.fastDivisionByPowerOfTwo(i, divisor))
            );
        }
    }

    @Test
    void testFastDivisionByPowerOfTwo2() {
        long divisor;

        for (int j = 0; j <= 62; j++) {
            divisor = 1L << j;
            val finalDivisor = divisor;
            assertEquals((long) ((double) (Long.MAX_VALUE >> 16) / finalDivisor),
                RandomSupport.fastDivisionByPowerOfTwo((Long.MAX_VALUE >> 16), finalDivisor), "%d power".formatted(j));
        }

        assertNotEquals((long) ((double) Long.MAX_VALUE / (1L << 60)),
            RandomSupport.fastDivisionByPowerOfTwo((Long.MAX_VALUE >> 2), 1L << 60));


        for (int j = 0; j <= 62; j++) {
            divisor = 1L << j;
            val finalDivisor = divisor;
            LongStream.iterate(Integer.MAX_VALUE, i -> i >= 1, i -> i - 1).forEach(i ->
                assertEquals((long) ((double) i / finalDivisor), RandomSupport.fastDivisionByPowerOfTwo(i,
                    finalDivisor))
            );
        }
    }

    @Test
    void generateNonNegativeIntInRangeNotIncludeBound() {
        val rngMock = Mockito.mock(MersenneTwister.class, Mockito.CALLS_REAL_METHODS);
        doReturn(0).when(rngMock).nextInt();


        int j;
        val rng = new MersenneTwister(0L);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            j = RandomSupport.generateNonNegativeIntInRangeNotIncludeBound(rng, 2);
            assertTrue(j < 2);
            assertTrue(j >= 0);
        }

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            j = RandomSupport.generateNonNegativeIntInRangeNotIncludeBound(rng, 13);
            assertTrue(j < 13);
            assertTrue(j >= 0);
        }
    }
}
