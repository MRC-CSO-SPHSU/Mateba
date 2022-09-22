/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.random.engine;

import cern.mateba.function.tdouble.DoubleFunction;
import cern.mateba.function.tint.IntFunction;
import cern.mateba.function.tlong.LongFunction;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Random;

/**
 * Abstract base class for uniform pseudo-random number generating engines.
 * <p>
 * Most probability distributions are obtained by using a <b>uniform</b> pseudo-random number generation engine followed
 * by a transformation to the desired distribution. Thus, subclasses of this class are at the core of computational
 * statistics, simulations, Monte Carlo methods, etc.
 * <p>
 * Subclasses produce uniformly distributed {@code int}'s and {@code long}'s in the closed intervals
 * {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} and {@code [Long.MIN_VALUE,Long.MAX_VALUE]}, respectively, as well as
 * {@code double}'s in all possible unit intervals from 0.0 to 1.0.
 * <p>
 * Subclasses need to override {@code nextLong()}. All other methods generating different data types or ranges are
 * usually layered upon it.
 * <p>
 * Note that this implementation is <b>not synchronized</b>.
 *
 * @see MersenneTwister
 * @see java.util.Random
 */

@SuppressWarnings("unused")
public abstract class RandomEngine extends Random implements DoubleFunction, IntFunction, LongFunction, Cloneable {
    // fixme now that we have a different default range, check other distributions as they depend on the uniform one.
    @Serial
    private static final long serialVersionUID = -3722884246173327714L;

    /**
     * Constructs and returns a new uniform random number engine seeded with the current time. Currently, this is
     * {@link MersenneTwister}.
     */
    @Contract(" -> new")
    public static @NotNull RandomEngine makeDefault() {
        return new MersenneTwister(System.currentTimeMillis());
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
    static double doubleFromLongClosed(final long l) {
        return (l >>> 11) / unitIntervalTypes.CLOSED.getLongFactor();
    }

    /**
     * Range {@code [0, 1)}.
     *
     * @see #doubleFromLongClosed(long)
     */
    static double doubleFromLongOpenRight(final long l) {
        return (l >>> 11) / unitIntervalTypes.OPEN_RIGHT.getLongFactor();
    }

    /**
     * Range {@code (0, 1]}.
     *
     * @see #doubleFromLongClosed(long)
     */
    static double doubleFromLongOpenLeft(final long l) {
        return ((l >>> 11) + 1) / unitIntervalTypes.OPEN_LEFT.getLongFactor();
    }

    /**
     * Range {@code (0, 1)}.
     *
     * @see #doubleFromLongClosed(long)
     */
    static double doubleFromLongOpen(final long l) {
        return ((l >>> 12) + 0.5) / unitIntervalTypes.OPEN.getLongFactor();
    }

    /**
     * Maps one-to-one any negative {@code integer} to the non-negative range.
     * Works by flipping the sign bit {@code x & ~(1 << 31))}.
     *
     * @param i An {@code integer} number.
     * @return The non-negative complement.
     */
    private static int map(final int i) {
        return i & 2147483647;
    }

    /**
     * Maps one-to-one any negative {@code long} to the non-negative range.
     * Works by flipping the sign bit {@code x & ~(1L << 63))}.
     *
     * @param l A {@code long} number.
     * @return The non-negative complement.
     */
    private static long map(final long l) {
        return l & 9223372036854775807L;
    }

    /**
     * Checks if a given {@code long} number is a power of two.
     *
     * @param l A {@code long} number.
     * @return {@code true} if {@code l} is a power of two, {@code false} otherwise.
     * @implSpec 0 is considered a valid power of two to keep the code trivial. If {@code l < 0} you're on your own.
     * @see <a href="https://graphics.stanford.edu/~seander/bithacks.html#DetermineIfPowerOf2">Bit Twiddling Hacks </a>
     */
    private static boolean powerOfTwo(final long l) {
        return (l & (l - 1)) == 0;
    }

    /**
     * Evaluate the result of integer division of two numbers.
     *
     * @param numerator The number to be divided.
     * @param divisor   The number to divide by, must be a power of two.
     * @return the result of division.
     */
    private static long moduloPowerOfTwo(final long numerator, final long divisor) {
        return numerator & divisor - 1;
    }

    /**
     * Equivalent to {@code raw()}. This has the effect that random engines can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    final public double apply(final double dummy) {
        return raw();
    }

    /**
     * Equivalent to {@code nextInt()}. This has the effect that random engines can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    final public int apply(final int dummy) {
        return nextInt();
    }

    /**
     * Equivalent to {@code nextLong()}. This has the effect that random engines can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    final public long apply(final long dummy) {
        return nextLong();
    }

    /**
     * Returns a 64 bit uniformly distributed random number in the semi-open unit interval {@code [0.0,1.0)} (including
     * 0.0 and excluding 1.0).
     *
     * @implSpec This implementation follows JDK convention in terms of included endpoints.
     * @see #nextDouble(unitIntervalTypes)
     */
    @Override
    final public double nextDouble() {
        return nextDouble(unitIntervalTypes.OPEN_RIGHT);
    }

    /**
     * Generates a random {@code double} between 0 and 1. Inclusion of endpoints depends on {@code type}.
     *
     * @param type The interval type, one of the following values:
     *             {@code OPEN} for {@code (0, 1)}, {@code CLOSED} for {@code  [0, 1]}, {@code OPEN_LEFT} for
     *             {@code (0, 1]}, and {@code OPEN_RIGHT} for {@code  [0, 1)}.
     * @return a random number from the provided interval.
     * @throws IllegalArgumentException when the provided type is not supported.
     */
    final public double nextDouble(final @NotNull unitIntervalTypes type) {
        return switch (type) {
            case CLOSED -> doubleFromLongClosed(nextLong());
            case OPEN -> doubleFromLongOpen(nextLong());
            case OPEN_LEFT -> doubleFromLongOpenLeft(nextLong());
            case OPEN_RIGHT -> doubleFromLongOpenRight(nextLong());
        };
    }

    /**
     * Generates random values between 0 and {@code bound}.
     *
     * @param type  The range type.
     * @param bound The range limit, can be positive/negative.
     * @return a random {@code double } from the range.
     * @throws IllegalArgumentException when {bound == 0 || Double.isNaN(bound) || Double.isInfinite(bound)}.
     * @see unitIntervalTypes
     */
    final public double nextDouble(final @NotNull unitIntervalTypes type, final double bound) {
        if (bound == 0 || Double.isNaN(bound) || Double.isInfinite(bound))
            throw new IllegalArgumentException("Bound can't be NaN/0.");

        return nextDouble(type) * bound; // todo check the original implementation for different round-off errors, also check corner cases
    }

    final public double nextDouble(final @NotNull unitIntervalTypes type, double origin, double bound) {
        if (Double.isNaN(bound) || Double.isNaN(origin)) throw new IllegalArgumentException("Invalid boundaries.");
        if (origin == bound) throw new IllegalArgumentException("Zero length interval.");
        if (origin > bound) throw new IllegalArgumentException("Input parameters form an unordered set.");
        if (Double.isInfinite(bound) || Double.isInfinite(origin))
            throw new IllegalArgumentException("Infinite limits.");
        if (Double.isInfinite(bound - origin))
            throw new IllegalArgumentException("The range is too large."); // fixme is it possible to avoid this?

        return (nextDouble(type) * (bound - origin)) + origin;// todo check the original implementation for different round-off errors
    }

    @Override
    final public double nextDouble(final double bound) {
        return nextDouble(unitIntervalTypes.OPEN_RIGHT, bound);
    }

    @Override
    final public double nextDouble(final double origin, final double bound) {
        return nextDouble(unitIntervalTypes.OPEN_RIGHT, origin, bound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public double nextGaussian(final double mean, final double stddev) {
        return super.nextGaussian(mean, stddev); // todo replace
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextExponential() {
        return super.nextExponential(); // todo replace?
    }

    /**
     * Generates a 32-bit {@code int} by calling {@link #nextLong()} and trimming the result.
     *
     * @return a 32 bit uniformly distributed random number in the closed interval
     * {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} (including {@code Integer.MIN_VALUE} and
     * {@code Integer.MAX_VALUE});
     */
    final public int nextInt() {
        return (int) (nextLong() >> 32);
    }


    /**
     * Generates a random {@code int} in the range {@code [0, bound]} or {@code [bound, -1]} when {@code bound} is
     * positive/negative, respectively.
     *
     * @param bound The limit of the range.
     * @return a random {@code int}.
     * @throws IllegalArgumentException when {@code bound == 0 || bound == -1}.
     * @implSpec the {@code bound} is <b>always</b> included.
     * @see <a href="https://stackoverflow.com/a/46991999">StackOverflow</a>
     * @see <a href="https://dl.acm.org/doi/10.1145/3230636">Daniel Lemire. 2019. Fast Random Integer Generation in an
     * Interval. ACM Trans. Model. Comput. Simul. 29, 1, Article 3 </a>
     */
    @Override
    final public int nextInt(final int bound) {
        if (bound == 0 || bound == -1) throw new IllegalArgumentException("Incorrect boundary value provided.");
        return bound > 0 ? nonNegativeNextInt(bound) : ~nonNegativeNextInt(~bound);
    }

    /**
     * A special case of an {@code int} generator.
     *
     * @param bound a strictly positive {@code int} number in range {@code (0, Integer.MAX_VALUE]}.
     * @return a {@code int} number in the {@code [0, Integer.MAX_VALUE]} range only.
     * @implSpec No checks whatsoever for performance reasons. If {@code bound + 1} is a power of two, the bit trick
     * that is {@code (x % 2}<sup>k</sup>{@code ) == (x & 2}<sup>k</sup>{@code  - 1)} is used to avoid extra modulo
     * operations.
     * @implNote Gets an {@code long} number in the  {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]} and maps to the
     * non-negative range by clearing the sign bit via {@code x & ~(1 << 31))}.
     * If the desired range is {@code [0, Integer.MAX_VALUE]}, simply returns the value, else samples (and rejects to
     * avoid modulo bias) till the value is in range.
     * @see <a href="https://dl.acm.org/doi/pdf/10.1145/3230636">Fast Random Integer Generation in an Interval</a>
     */
    private int nonNegativeNextInt(final int bound) {
        var nonnegativeValue = map(nextInt());
        if (bound == Integer.MAX_VALUE) return nonnegativeValue;

        val boundPrime = (long) (bound + 1);
        val powerOfTwo = powerOfTwo(boundPrime);
        if (powerOfTwo) return (int) moduloPowerOfTwo(map(nextInt()), boundPrime);

        var value = map(nextInt());
        var upscale = value * boundPrime;
        var bin = moduloPowerOfTwo(upscale, 2147483648L);
        if (bin <= bound) {
            var t = (2147483648L - boundPrime) % boundPrime;
            while (bin < t) {
                value = map(nextInt());
                upscale = value * boundPrime;
                bin = moduloPowerOfTwo(upscale, 2147483648L);
            }
        }
        return (int) (bin >> 32);
    }

    /**
     * Generates a random {@code int} number that belongs to the range {@code [origin, bound]}, where both points are
     * included. The actual signs of {@code origin} and {@code bound} don't matter as long as {@code {origin, bound}} is
     * an ordered set, i.e., {@code origin < bound}.
     *
     * @param origin An {@code int} number, can't be {@code Integer.MAX_VALUE}.
     * @param bound  An {@code int} number, can't be {@code Integer.MIN_VALUE}.
     * @return a random {@code int} from the range {@code [origin, bound]}.
     * @implNote Three possible ranges for the variable {@code rangePower} are considered. {@code rangePower} is the
     * total number of all possible outcomes defined as {@code bound - origin + 1}. If {@code rangePower > 0} it is
     * limited by {@code Integer.MAX_VALUE}, here an {@code int} from the discrete uniform distribution
     * {@code [0, rangePower]} and shifted by the value of {@code origin}; the direction of the shift is decided
     * according to the sign of {@code origin}. {@code rangePower == 0} only in the case of an overflow, when
     * {@code bound == Integer.MAX_VALUE}, {@code origin == Integer.MIN_VALUE}, the whole {@code long} range is used
     * then for sampling via {@link #nextInt()}. Finally, when there is any other case of an overflow, the conventional
     * rejection technique is used at the moment.
     */
    @Override
    final public int nextInt(final int origin, final int bound) {
        if (!(origin < bound)) throw new IllegalArgumentException("The set is not ordered.");
        val rangePower = bound - origin + 1;
        if (rangePower > 0) return nextInt(rangePower) + origin;
        if (rangePower < 0) {
            var value = nextInt();
            while (!(value >= origin && value <= bound)) value = nextInt();
            return value;
        }
        return nextInt();
    }

    /**
     * Returns a 64 bit uniformly distributed random number in the closed interval
     * {@code [Long.MIN_VALUE,Long.MAX_VALUE]} (including {@code Long.MIN_VALUE} and {@code Long.MAX_VALUE}).
     */
    public abstract long nextLong();

    /**
     * Generates a random {@code long} in the range {@code [0, bound]} or {@code [bound, -1]} when {@code bound} is
     * positive/negative, respectively.
     *
     * @param bound The limit of the range.
     * @return a random {@code long}.
     * @throws IllegalArgumentException when {@code bound == 0 || bound == -1}.
     * @implSpec the {@code bound} is <b>always</b> included.
     * @see <a href="https://stackoverflow.com/a/46991999">stackoverflow</a>
     */
    @Override
    final public long nextLong(final long bound) {
        if (bound == 0 || bound == -1) throw new IllegalArgumentException("Incorrect boundary value provided.");
        return bound > 0 ? nonNegativeNextLong(bound) : ~nonNegativeNextLong(~bound);
    }

    /**
     * A special case of a {@code long} generator.
     *
     * @param bound a strictly positive {@code long} number in range {@code (0, Long.MAX_VALUE]}.
     * @return a {@code long} number in the {@code [0, Long.MAX_VALUE]} range only.
     * @implSpec No checks whatsoever for performance reasons. If {@code bound + 1} is a power of two, the bit trick
     * that is {@code (x % 2}<sup>k</sup>{@code ) == (x & 2}<sup>k</sup>{@code  - 1)} is used to avoid extra modulo
     * operations.
     * @implNote Gets a {@code long} number in the  {@code [Long.MIN_VALUE, Long.MAX_VALUE]} and maps to the
     * non-negative range by clearing the sign bit via {@code x & ~(1L << 63))}.
     * If the desired range is {@code [0, Long.MAX_VALUE]}, simply returns the value, else samples (and rejects to avoid
     * modulo bias) till the value is in range.
     * @see <a href="https://stackoverflow.com/a/46991999">StackOverflow</a>
     * @see <a href="https://dl.acm.org/doi/10.1145/3230636">Daniel Lemire. 2019. Fast Random Integer Generation in an
     * Interval. ACM Trans. Model. Comput. Simul. 29, 1, Article 3 </a>
     */
    private long nonNegativeNextLong(final long bound) {
        var nonnegativeValue = map(nextLong());
        if (bound == Long.MAX_VALUE) return nonnegativeValue;

        val boundPrime = bound + 1;
        val powerOfTwo = powerOfTwo(boundPrime);

        if (powerOfTwo) {
            return moduloPowerOfTwo(map(nextLong()), boundPrime);
        } else {
            val cutOffLimit = Long.MAX_VALUE - (Long.MAX_VALUE % boundPrime + 1) % boundPrime;
            while (nonnegativeValue > cutOffLimit) nonnegativeValue = map(nextLong());
            return nonnegativeValue % boundPrime;
        }
    }

    /**
     * Generates a random {@code long} number that belongs to the range {@code [origin, bound]}, where both points are
     * included. The actual signs of {@code origin} and {@code bound} don't matter as long as {@code {origin, bound}} is
     * an ordered set, i.e., {@code origin < bound}.
     *
     * @param origin A {@code long} number, can't be {@code Long.MAX_VALUE}.
     * @param bound  A {@code long} number, can't be {@code Long.MIN_VALUE}.
     * @return a random {@code long} from the range {@code [origin, bound]}.
     * @throws IllegalArgumentException when {@code origin >= bound}.
     * @implNote Three possible ranges for the variable {@code rangePower} are considered. {@code rangePower} is the
     * total number of all possible outcomes defined as {@code bound - origin + 1}. If {@code rangePower > 0} it is
     * limited by {@code Long.MAX_VALUE}, here a {@code long} from the discrete uniform distribution
     * {@code [0, rangePower]} and shifted by the value of {@code origin}; the direction of the shift is decided
     * according to the sign of {@code origin}. {@code rangePower == 0} only in the case of an overflow, when
     * {@code bound == Long.MAX_VALUE}, {@code origin == Long.MIN_VALUE}, the whole {@code long} range is used then for
     * sampling via {@link #nextLong()}. Finally, when there is any other case of an overflow, the conventional
     * rejection technique is used at the moment.
     */
    @Override
    final public long nextLong(final long origin, final long bound) {
        if (!(origin < bound)) throw new IllegalArgumentException("The set is not ordered.");
        val rangePower = bound - origin + 1;
        if (rangePower > 0) return nextLong(rangePower) + origin;
        if (rangePower < 0) {
            var value = nextLong();
            while (!(value >= origin && value <= bound)) value = nextLong();
            return value;
        }
        return nextLong();
    }

    /**
     * @see #nextDouble()
     */
    final public double raw() {
        return nextDouble(unitIntervalTypes.OPEN_RIGHT);
    }

    /**
     * Returns a 64 bit uniformly distributed random number between 0.0 and 1.0. Endpoints are included when a suitable
     * interval type is provided.
     *
     * @see unitIntervalTypes
     */
    final public double raw(final unitIntervalTypes type) {
        return nextDouble(type);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchMethodException Always as the method is not implemented.
     */
    @Override
    final public float nextFloat(final float origin, final float bound) {
        try {
            throw new NoSuchMethodException("Don't do floats");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchMethodException Always as the method is not implemented.
     */
    @Override
    final public float nextFloat(final float bound) {
        try {
            throw new NoSuchMethodException("Don't do floats");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An implementation of the {@link Cloneable} interface.
     */
    @Override
    public RandomEngine clone() {
        try {
            return (RandomEngine) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum unitIntervalTypes {
        OPEN (1L << 52),
        CLOSED ((1L << 53) - 1),
        OPEN_LEFT (1L << 53),
        OPEN_RIGHT (1L << 53);

        @Getter
        private final double longFactor;
        unitIntervalTypes(final double longFactor) {
            this.longFactor = longFactor;
        }
    }
}
