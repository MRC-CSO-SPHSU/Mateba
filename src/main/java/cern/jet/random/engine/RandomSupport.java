package cern.jet.random.engine;

import lombok.Getter;
import lombok.val;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.lang.StrictMath.multiplyHigh;

public class RandomSupport {

    final private static long WORD_31_BIT = 1L << 31;
    final private static long WORD_63_BIT = 1L << 63;

    final private static String STREAM_SIZE = "Stream size is negative.";
    final private static String BOUND_ORDER = "Bad order of range limits.";
    final private static String RANGE_LENGTH = "Interval is of infinite length, " +
                                                "currently, no implementation supports that.";
    final private static String BOUND_NAN = "Bound value is NaN.";

    static DoubleStream generateDoubleStream(final RandomEngine rng) {
        return LongStream.generate(rng::nextLong).mapToDouble(RandomSupport::doubleFromLongCO);
    }

    static DoubleStream generateDoubleStream(final RandomEngine rng, final doubleUnitIntervalTypes type) {
        return switch (type) {
            case DOUBLE_CC -> LongStream.generate(rng::nextLong).mapToDouble(RandomSupport::doubleFromLongCC);
            case DOUBLE_OO -> LongStream.generate(rng::nextLong).mapToDouble(RandomSupport::doubleFromLongOO);
            case DOUBLE_OC -> LongStream.generate(rng::nextLong).mapToDouble(RandomSupport::doubleFromLongOC);
            case DOUBLE_CO -> LongStream.generate(rng::nextLong).mapToDouble(RandomSupport::doubleFromLongCO);
        };
    }

    static IntStream generateIntegerStream(final RandomEngine rng) {
        return IntStream.generate(rng::nextInt).sequential();
    }

    static LongStream generateLongStream(final RandomEngine rng) {
        return LongStream.generate(rng::nextLong);
    }

    static long generateNextLong(final RandomEngine rng, final long origin, final long bound) {
        val rangePower = bound - origin + 1;
        if (rangePower > 0) return rng.nextLong(rangePower) + origin; //fixme this nextlong introduces extra checks, change this
        if (rangePower < 0) {
            var value = rng.nextLong();
            while (!(value >= origin && value <= bound)) value = rng.nextLong();
            return value;
        }
        return rng.nextLong();
    }

    /**
     * A helper method that calculates the integer part of a {@code log2} of any {@code int} number.
     *
     * @implSpec Provides valid results when the input is a positive {@code int}.
     * The exact equivalent of {@code (int) (Math.log(value) / Math.log(2))}, but much faster.
     * @param value An integer number.
     * @return {@code (int) log2(value)}.
     */
    static int fastLog2(int value)
    {
        return 31 - Integer.numberOfLeadingZeros(value);
    }

    /**
     * A helper method that calculates the integer part of a {@code log2} of any {@code long} number.
     *
     * @implSpec Provides valid results when the input is a positive {@code long}.
     * The exact equivalent of {@code (long) (Math.log(value) / Math.log(2))}, but much faster, for the range
     * up to {@code Integer.MAX_VALUE} and above. At some point the {@link Math#log(double)} starts to fail to provide
     * the accurate answer due to round-off errors and limited precision.
     * @param value An integer number.
     * @return {@code (int) log2(value)}.
     */
    static long fastLog2 (long value)
    {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    /**
     * Fast division of two integer numbers when the dividend is a non-negative integer number and the divisor is a
     * positive one and a power of {@code 2} as well.
     * @param a The dividend.
     * @param b The divisor.
     * @return The quotient.
     */
    static int fastDivisionByPowerOfTwo(final int a, final int b) {
        return a >> fastLog2(b);
    }

    /**
     * @see #fastDivisionByPowerOfTwo(int, int)
     * @implNote Due to some limitations of floating point math starts to diverge from {@code (int) ((double) a / b)}
     * at some point.
     */
    static long fastDivisionByPowerOfTwo(final long a, final long b) {
        return a >> fastLog2(b);
    }

    /**
     * Generates an integer in the range {@code [0, bound)} where {@code bound} is not included.
     * @param rng A random generator object.
     * @param bound The bound, a strictly positive integer, must be {@code > 1} to have at least two values in the
     *              range, i.e. at least a pair of {@code {0, 1}}.
     * @return an integer in {@code [0, bound)} range.
     * @see <a href="https://dl.acm.org/doi/pdf/10.1145/3230636">Daniel Lemire. 2019. Fast Random Integer Generation in
     * an Interval. ACM Trans. Model. Comput. Simul. 29, 1, Article 3 (January 2019), 12 pages.</a>
     */
    static int generateNonNegativeIntInRangeNotIncludeBound(final RandomEngine rng, final int bound) {
        var nonnegativeValue = map(rng.nextInt());
        if (powerOfTwo(bound)) return (int) moduloPowerOfTwo(nonnegativeValue, bound);
        else {
            long m = (long) nonnegativeValue * bound;
            long l = moduloPowerOfTwo(m, WORD_31_BIT);
            if (l < bound) {
                val t = (WORD_31_BIT - bound) % bound;
                while (l < t) {
                    nonnegativeValue = map(rng.nextInt());
                    m = (long) nonnegativeValue * bound;
                    l = moduloPowerOfTwo(m, WORD_31_BIT);
                }
            }
            return (int) (m >>> 31);
        }
    }

    /**
     * Generates an integer in the range {@code [0, bound]} where {@code bound} is included.
     * @param rng A random generator object.
     * @param bound The bound, a strictly positive integer, must be {@code > 0} to have at least two values in the
     *              range, i.e. at least a pair of {@code {0, 1}}.
     * @return an integer in {@code [0, bound]} range.
     * @implSpec No checks whatsoever for performance reasons. If {@code bound + 1} is a power of two, the bit trick
     * that is {@code (x % 2}<sup>k</sup>{@code ) == (x & 2}<sup>k</sup>{@code  - 1)} is used to avoid extra modulo
     * operations. Gets an {@code int} number in the  {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]} and maps to the
     * non-negative range by clearing the sign bit via {@code x & ~(1 << 31))}.
     * If the desired range is {@code [0, Integer.MAX_VALUE]}, simply returns the value, else samples (and rejects to
     * avoid modulo bias) till the value is in range.
     * @see <a href="https://dl.acm.org/doi/pdf/10.1145/3230636">Daniel Lemire. 2019. Fast Random Integer Generation in
     * an Interval. ACM Trans. Model. Comput. Simul. 29, 1, Article 3 (January 2019), 12 pages.</a>
     */
    static int generateNonNegativeIntInRangeIncludeBound(final RandomEngine rng, final int bound) {
        if (bound == Integer.MAX_VALUE) return map(rng.nextInt());
        else return generateNonNegativeIntInRangeNotIncludeBound(rng, bound + 1);
    }

    /**
     * @see #generateNonNegativeIntInRangeNotIncludeBound(RandomEngine, int)
     */
    static long generateNonNegativeLongInRangeNotIncludeBound(final RandomEngine rng, final long bound) {
        var nonnegativeValue = map(rng.nextLong());
        if (powerOfTwo(bound)) return (int) moduloPowerOfTwo(nonnegativeValue, bound);
        else {
            long m_hi = multiplyHigh(nonnegativeValue, bound);
            long m_lo = nonnegativeValue * bound;
            long l = moduloPowerOfTwo(m_lo, WORD_63_BIT);
            if (l < bound) {
                val t = (WORD_63_BIT - bound) % bound;
                while (l < t) {
                    nonnegativeValue = map(rng.nextLong());
                    m_hi =  multiplyHigh(nonnegativeValue, bound);
                    m_lo = nonnegativeValue * bound;
                    l = moduloPowerOfTwo(m_lo, WORD_63_BIT);
                }
            }
            return (m_hi << 1) + (m_lo >>> 63);
        }
    }

    /**
     * @see #generateNonNegativeIntInRangeIncludeBound(RandomEngine, int)
     */
    static long generateNonNegativeLongInRangeIncludeBound(final RandomEngine rng, final long bound) {
        if (bound == Long.MAX_VALUE) return map(rng.nextLong());
        else return generateNonNegativeLongInRangeNotIncludeBound(rng, bound + 1);
    }

    /**
     * Converts a {@code long} (64 bit) number from the range {@code [Long.MIN_VALUE, Long.MAX_VALUE]} to a (64 bit)
     * {@code double} in the {@code [0, 1]} range. According to the convention {@code []} denote that both endpoints are
     * included.
     *
     * @param l A {@code long} number.
     * @return a double in the {@code [0, 1]} range.
     * @implSpec {@code Long.MIN_VALUE} is mapped to {@code 0} to make sure all numbers from {@code [0, Long.MAX_VALUE]}
     * have the same frequency of appearance (no zero bias whatsoever).
     * @implNote This could be done with a LUT of some sort which "my RAM sticks are too small to contain". An extra
     * shift {@code >>>} is used to match the reference implementation.
     */
    static double doubleFromLongCC(final long l) {
        return (l >>> 11) / doubleUnitIntervalTypes.DOUBLE_CC.getLongFactor();
    }

    /**
     * Range {@code [0, 1)}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongCO(final long l) {
        return (l >>> 11) / doubleUnitIntervalTypes.DOUBLE_CO.getLongFactor(); // todo check factors and replace with reciprocals where suitable
    }

    /**
     * Range {@code (0, 1]}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongOC(final long l) {
        return ((l >>> 11) + 1) / doubleUnitIntervalTypes.DOUBLE_OC.getLongFactor();
    }

    /**
     * Range {@code (0, 1)}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongOO(final long l) {
        return ((l >>> 12) + 0.5) / doubleUnitIntervalTypes.DOUBLE_OO.getLongFactor();
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
    }// fixme rename to more clear
// todo we don't really care how to do this, should we simply do a bitshift instead of complex mapping??
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

    public enum doubleUnitIntervalTypes {
        DOUBLE_OO(1L << 52),
        DOUBLE_CC((1L << 53) - 1),
        DOUBLE_OC(1L << 53),
        DOUBLE_CO(1L << 53);

        @Getter
        private final double longFactor;

        doubleUnitIntervalTypes(final double longFactor) {
            this.longFactor = longFactor;
        }
    }

    static void validateStreamSize(final long streamSize) {
        if (streamSize < 0L) throw new IllegalArgumentException(STREAM_SIZE);
    }

    static double validateDoubleRange(final double randomNumberOrigin, final double randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) throw new IllegalArgumentException(BOUND_ORDER);
        val intervalLength = randomNumberBound - randomNumberOrigin;
        if (!((intervalLength) < Double.POSITIVE_INFINITY))
            throw new IllegalArgumentException(RANGE_LENGTH);
        return intervalLength;
    }

    static float validateFloatRange(final float randomNumberOrigin, final float randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) throw new IllegalArgumentException(BOUND_ORDER);
        val intervalLength = randomNumberBound - randomNumberOrigin;
        if (!((intervalLength) < Float.POSITIVE_INFINITY))
            throw new IllegalArgumentException(RANGE_LENGTH);
        return intervalLength;
    }

    static void validateIntRange(final int start, final int end) {
        if (!(start < end)) throw new IllegalArgumentException("The set is not ordered.");
    }

    static void validateLongRange(final long start, final long end) {
        if (!(start < end)) throw new IllegalArgumentException("The set is not ordered.");
    }

    static void validateIntBound(final int bound) {
        if (bound == 0 || bound == -1) throw new IllegalArgumentException("Incorrect boundary value provided.");
    }

    static void validateLongBound(final long bound) {
        if (bound == 0 || bound == -1) throw new IllegalArgumentException("Incorrect boundary value provided.");
    }

    static void validateDoubleBound(final double bound) {
        if (!Double.isNaN(bound)) throw new IllegalArgumentException(BOUND_NAN);
    }

    static void validateFloatBound(final float bound) {
        if (!Float.isNaN(bound)) throw new IllegalArgumentException(BOUND_NAN);
    }
}
