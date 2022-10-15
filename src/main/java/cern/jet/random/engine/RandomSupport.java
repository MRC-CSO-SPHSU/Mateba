package cern.jet.random.engine;

import lombok.Getter;
import lombok.val;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static cern.jet.random.engine.FastMathSupport.*;
import static java.lang.StrictMath.fma;
import static java.lang.StrictMath.multiplyHigh;

public class RandomSupport {

    final private static long WORD_31_BIT = 1L << 31;
    final private static long WORD_63_BIT = 1L << 63;

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
     * Generates an integer in the range {@code [0, bound)} where {@code bound} is not included.
     *
     * @param rng   A random generator object.
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
     *
     * @param rng   A random generator object.
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
                    m_hi = multiplyHigh(nonnegativeValue, bound);
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
     * Converts a {@code long} number from the range {@code [Long.MIN_VALUE, Long.MAX_VALUE]} to {@code double} in the
     * {@code [0, 1]} range. According to the convention {@code []} denote that both endpoints are included.
     *
     * @param l A {@code long} number.
     * @return a {@code double} in the {@code [0, 1]} range.
     * @implSpec Works for both negative and non-negative numbers due to {@code >>>} used. Employs {@code fma} instead
     * of division.
     * @implNote This could be done with a LUT of some sort which "my RAM sticks are too small to contain". An extra
     * shift {@code >>>} is used to match the reference implementation.
     * @see <a href="https://ieeexplore.ieee.org/document/1306999">N. Brisebarre, J. . -M. Muller and Saurabh Kumar
     * Raina, "Accelerating correctly rounded floating-point division when the divisor is known in advance," in IEEE
     * Transactions on Computers, vol. 53, no. 8, pp. 1069-1072, Aug. 2004</a>
     */
    static double doubleFromLongCC(final long l) {
        val x = (l >>> 11);
        val q = x * doubleUnitIntervalTypes.DOUBLE_CC.getFactor();
        val r = -fma(q, (1L << 53) - 1, -x);
        return fma(r, doubleUnitIntervalTypes.DOUBLE_CC.getFactor(), q);
    }

    /**
     * Range {@code [0, 1)}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongCO(final long l) {
        return (l >>> 11) * doubleUnitIntervalTypes.DOUBLE_CO.getFactor();
    }

    /**
     * Range {@code (0, 1]}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongOC(final long l) {
        return ((l >>> 11) + 1) * doubleUnitIntervalTypes.DOUBLE_OC.getFactor();
    }

    /**
     * Range {@code (0, 1)}.
     *
     * @see #doubleFromLongCC(long)
     */
    static double doubleFromLongOO(final long l) {
        return ((l >>> 12) + 0.5) * doubleUnitIntervalTypes.DOUBLE_OO.getFactor();
    }

    public enum doubleUnitIntervalTypes {
        DOUBLE_OO(0x1.0p-52),
        DOUBLE_CC(0x1.0000000000001p-53),
        DOUBLE_OC(0x1.0p-53),
        DOUBLE_CO(0x1.0p-53);

        @Getter
        private final double Factor;

        doubleUnitIntervalTypes(final double longFactor) {
            this.Factor = longFactor;
        }
    }
}
