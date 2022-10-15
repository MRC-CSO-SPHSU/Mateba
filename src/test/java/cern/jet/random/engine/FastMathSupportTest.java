package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

class FastMathSupportTest {
    @Test
    void fastLog2() {
        val log22 = Math.log(2);
        IntStream.rangeClosed(1, Integer.MAX_VALUE).forEach(i ->
            assertEquals((int) (Math.log(i) / log22), FastMathSupport.fastLog2(i))
        );
    }

    @Test
    void testFastLog2() {
        val log22 = Math.log(2);
        LongStream.rangeClosed(1, Integer.MAX_VALUE).forEach(i ->
            assertEquals((long) (Math.log(i) / log22), FastMathSupport.fastLog2(i))
        );

        assertNotEquals((long) (Math.log(Long.MAX_VALUE >> 2) / log22), FastMathSupport.fastLog2(Long.MAX_VALUE / 4));
        assertNotEquals((long) (Math.log(Long.MAX_VALUE >> 1) / log22), FastMathSupport.fastLog2(Long.MAX_VALUE / 2));
        assertNotEquals((long) (Math.log(Long.MAX_VALUE) / log22), FastMathSupport.fastLog2(Long.MAX_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9, 1 << 10,
        1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19, 1 << 20,
        1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29, 1 << 30,
        1 << 31})
    void fastDivisionByPowerOfTwo(final int divisor) {
        IntStream.rangeClosed(0, Integer.MAX_VALUE).forEach(i ->
            assertEquals((int) ((double) i / divisor), FastMathSupport.fastDivisionByPowerOfTwo(i, divisor))
        );
    }


    @ParameterizedTest
    @ValueSource(longs = {1L << 1, 1L << 2, 1L << 3, 1L << 4, 1L << 5, 1L << 6, 1L << 7, 1L << 8, 1L << 9, 1L << 10,
        1L << 11, 1L << 12, 1L << 13, 1L << 14, 1L << 15, 1L << 16, 1L << 17, 1L << 18, 1L << 19, 1L << 20,
        1L << 21, 1L << 22, 1L << 23, 1L << 24, 1L << 25, 1L << 26, 1L << 27, 1L << 28, 1L << 29, 1L << 30,
        1L << 31, 1L << 32, 1L << 33, 1L << 34, 1L << 35, 1L << 36, 1L << 37, 1L << 38, 1L << 39, 1L << 40,
        1L << 41, 1L << 42, 1L << 43, 1L << 44, 1L << 45, 1L << 46, 1L << 47, 1L << 48, 1L << 49, 1L << 50,
        1L << 51, 1L << 52, 1L << 53, 1L << 54, 1L << 55, 1L << 56, 1L << 57, 1L << 58, 1L << 59, 1L << 60,
        1L << 61, 1L << 62})
    void testFastDivisionByPowerOfTwo2(final long divisor) {
        assertEquals((long) ((double) (Long.MAX_VALUE >> 16) / divisor),
            FastMathSupport.fastDivisionByPowerOfTwo((Long.MAX_VALUE >> 16), divisor), "%d power".formatted(divisor));

        assertNotEquals((long) ((double) Long.MAX_VALUE / (1L << 60)),
            FastMathSupport.fastDivisionByPowerOfTwo((Long.MAX_VALUE >> 2), 1L << 60));

        LongStream.rangeClosed(1, Integer.MAX_VALUE).forEach(i ->
            assertEquals((long) ((double) i / divisor), FastMathSupport.fastDivisionByPowerOfTwo(i, divisor))
        );
    }

}
