package cern.jet.random.engine;

import lombok.val;

import static java.lang.StrictMath.fma;

public class FastMathSupport {
    /**
     * A helper method that calculates the integer part of a {@code log2} of any {@code int} number.
     *
     * @param value An integer number.
     * @return {@code (int) log2(value)}.
     * @implSpec Provides valid results when the input is a positive {@code int}.
     * The exact equivalent of {@code (int) (Math.log(value) / Math.log(2))}, but much faster.
     */
    static int fastLog2(int value) {
        return 31 - Integer.numberOfLeadingZeros(value);
    }

    /**
     * A helper method that calculates the integer part of a {@code log2} of any {@code long} number.
     *
     * @param value An integer number.
     * @return {@code (int) log2(value)}.
     * @implSpec Provides valid results when the input is a positive {@code long}.
     * The exact equivalent of {@code (long) (Math.log(value) / Math.log(2))}, but much faster, for the range
     * up to {@code Integer.MAX_VALUE} and above. At some point the {@link Math#log(double)} starts to fail to provide
     * the accurate answer due to round-off errors and limited precision.
     */
    static long fastLog2(long value) {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    /**
     * Fast division of two integer numbers when the dividend is a non-negative integer number and the divisor is a
     * positive one and a power of {@code 2} as well.
     *
     * @param a The dividend.
     * @param b The divisor.
     * @return The quotient.
     */
    static int fastDivisionByPowerOfTwo(final int a, final int b) {
        return a >> fastLog2(b);
    }

    /**
     * @implNote Due to some limitations of floating point math starts to diverge from {@code (int) ((double) a / b)}
     * at some point.
     * @see #fastDivisionByPowerOfTwo(int, int)
     */
    static long fastDivisionByPowerOfTwo(final long a, final long b) {
        return a >> fastLog2(b);
    }

    /**
     * Maps one-to-one any negative {@code integer} to the non-negative range.
     * Works by flipping the sign bit {@code x & ~(1 << 31))}.
     *
     * @param i An {@code integer} number.
     * @return The non-negative complement.
     */
    static int map(final int i) {
        return i & 2_147_483_647;
    }

    /**
     * Maps one-to-one any negative {@code long} to the non-negative range.
     * Works by flipping the sign bit {@code x & ~(1L << 63))}.
     *
     * @param l A {@code long} number.
     * @return The non-negative complement.
     */
    static long map(final long l) {
        return l & 9_223_372_036_854_775_807L;
    }

    /**
     * Checks if a given {@code long} number is a power of two.
     *
     * @param l A {@code long} number.
     * @return {@code true} if {@code l} is a power of two, {@code false} otherwise.
     * @implSpec 0 is considered a valid power of two to keep the code trivial. If {@code l < 0} you're on your own.
     * @see <a href="https://graphics.stanford.edu/~seander/bithacks.html#DetermineIfPowerOf2">Bit Twiddling Hacks</a>
     */
    static boolean powerOfTwo(final long l) {
        return (l & (l - 1)) == 0;
    }

    /**
     * Evaluate the result of integer division of two numbers.
     *
     * @param numerator The number to be divided.
     * @param divisor   The number to divide by, must be a power of two.
     * @return the result of division.
     */
    static long moduloPowerOfTwo(final long numerator, final long divisor) {
        return numerator & divisor - 1;
    }

    /**
     * A fast and mathematically correct division method for cases when divisor {@code y} is known in advance.
     *
     * @param x           The dividend.
     * @param y           The divisor.
     * @param reciprocalY The inverse of {@code y}.
     * @return the {@code x / y} ratio.
     * @see <a href="https://ieeexplore.ieee.org/document/1306999">N. Brisebarre, J. . -M. Muller and Saurabh Kumar
     * Raina, "Accelerating correctly rounded floating-point division when the divisor is known in advance," in IEEE
     * Transactions on Computers, vol. 53, no. 8, pp. 1069-1072, Aug. 2004</a>
     */
    static double fastDivision(final double x, final double y, final double reciprocalY) {
        val q = x * reciprocalY;
        val r = -fma(q, y, -x);
        return fma(r, reciprocalY, q);
    }
}
