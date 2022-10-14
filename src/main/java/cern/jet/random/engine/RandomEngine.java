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
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static cern.jet.random.engine.RandomSupport.*;
import static java.lang.StrictMath.fma;

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

    private long bufferInt;
    private boolean intFlag;

    /**
     * Constructs and returns a new uniform random number engine seeded with the current time. Currently, this is
     * {@link MersenneTwister}.
     */
    @Contract(" -> new")
    public static @NotNull RandomEngine makeDefault() {
        return new MersenneTwister(System.currentTimeMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public @NonNull DoubleStream doubles() {
        return generateDoubleStream(this);
    }

    /**
     * Generates a stream of {@code double} between {@code 0} and {@code 1} for different types of the unit interval
     * {@link doubleUnitIntervalTypes}.
     * @param type Unit interval type.
     * @return a stream of {@code double}.
     */
    final public @NonNull DoubleStream doubles(final @NonNull RandomSupport.doubleUnitIntervalTypes type) {
        return generateDoubleStream(this, type);
    }

    @Override
    final public @NonNull DoubleStream doubles(final double randomNumberOrigin, final double randomNumberBound) {
        val intervalLength = validateDoubleRange(randomNumberOrigin, randomNumberBound);
        // todo add a check th the case we might get infinite values?? is it even possible technically?
        return generateDoubleStream(this).map(i -> fma(i, intervalLength, randomNumberOrigin));//todo add trim
    }

    final public @NonNull DoubleStream doubles(final double randomNumberOrigin, final double randomNumberBound,
                                               final @NonNull RandomSupport.doubleUnitIntervalTypes type) {
        val intervalLength = validateDoubleRange(randomNumberOrigin, randomNumberBound);
        // todo add a check th the case we might get infinite values?? is it even possible technically?
        return generateDoubleStream(this, type).map(i -> fma(i, intervalLength, randomNumberOrigin));//todo add trim
    }

    /**
     * Generates a stream of {@code double} of a limited length.
     * @param streamSize The stream length.
     * @return a stream of {@code double}.
     */
    @Override
    final public @NonNull DoubleStream doubles(final long streamSize) {
        validateStreamSize(streamSize);
        return generateDoubleStream(this).limit(streamSize);
    }

    /**
     * @see #doubles(long)
     * @see #doubles(doubleUnitIntervalTypes)
     */
    final public @NonNull DoubleStream doubles(final long streamSize, final @NonNull RandomSupport.doubleUnitIntervalTypes type) {
        validateStreamSize(streamSize);
        return generateDoubleStream(this, type).limit(streamSize);
    }

    @Override
    final public @NonNull DoubleStream doubles(final long streamSize, final double randomNumberOrigin,
                                               final double randomNumberBound) {
        validateStreamSize(streamSize);
        val intervalLength = validateDoubleRange(randomNumberOrigin, randomNumberBound);
        // todo add a check th the case we might get infinite values?? is it even possible technically?
        return generateDoubleStream(this).map(i -> fma(i, intervalLength, randomNumberOrigin)).limit(streamSize);//todo add trim
    }

    final public @NonNull DoubleStream doubles(final long streamSize, final double randomNumberOrigin,
                                               final double randomNumberBound, final @NonNull RandomSupport.doubleUnitIntervalTypes type) {
        validateStreamSize(streamSize);
        val intervalLength = validateDoubleRange(randomNumberOrigin, randomNumberBound);
        // todo add a check th the case we might get infinite values?? is it even possible technically?
        return generateDoubleStream(this, type).map(i -> fma(i, intervalLength, randomNumberOrigin)).limit(streamSize);
        //todo add trim
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public @NonNull IntStream ints() {
        return generateIntegerStream(this);
    }

    @Override
    final public @NonNull IntStream ints(final int randomNumberOrigin, final int randomNumberBound) {
        validateIntRange(randomNumberOrigin, randomNumberBound);
        return generateIntegerStream(this); // fixme broken, just a stub
    }

    final public @NonNull IntStream ints(final int randomNumberOrigin, final int randomNumberBound,
                                         final boolean includeBound) {
        validateIntRange(randomNumberOrigin, randomNumberBound);
        return generateIntegerStream(this); // fixme broken, just a stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public @NonNull IntStream ints(final long streamSize) {
        validateStreamSize(streamSize);
        return generateIntegerStream(this).limit(streamSize);
    }

    @Override
    final public @NonNull IntStream ints(final long streamSize, final int randomNumberOrigin,
                                         final int randomNumberBound) {
        validateStreamSize(streamSize);
        validateIntRange(randomNumberOrigin, randomNumberBound);
        return generateIntegerStream(this); // fixme broken, just a stub
    }

    final public @NonNull IntStream ints(final long streamSize, final int randomNumberOrigin,
                                         final int randomNumberBound, final boolean includeBound) {
        validateStreamSize(streamSize);
        validateIntRange(randomNumberOrigin, randomNumberBound);
        return generateIntegerStream(this); // fixme broken, just a stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public @NonNull LongStream longs() {
        return generateLongStream(this);
    }

    @Override
    final public @NonNull LongStream longs(final long randomNumberOrigin, final long randomNumberBound) {
        validateLongRange(randomNumberOrigin, randomNumberBound);
        return generateLongStream(this); // fixme broken, just a stub
    }

    final public @NonNull LongStream longs(final long randomNumberOrigin, final long randomNumberBound,
                                           final boolean includeBound) {
        validateLongRange(randomNumberOrigin, randomNumberBound);
        return generateLongStream(this); // fixme broken, just a stub
    }

    /**
     * Generates a stream of {@code long} of a limited length.
     * @param streamSize The stream length.
     * @return a stream of {@code long}.
     */
    @Override
    final public @NonNull LongStream longs(final long streamSize) {
        validateStreamSize(streamSize);
        return generateLongStream(this).limit(streamSize);
    }

    @Override
    final public @NonNull LongStream longs(final long streamSize, final long randomNumberOrigin,
                                           final long randomNumberBound) {
        validateStreamSize(streamSize);
        validateLongRange(randomNumberOrigin, randomNumberBound);
        return generateLongStream(this).limit(streamSize); // fixme broken, just a stub
    }

    final public @NonNull LongStream longs(final long streamSize, final long randomNumberOrigin,
                                           final long randomNumberBound, final boolean includeBound) {
        validateStreamSize(streamSize);
        validateLongRange(randomNumberOrigin, randomNumberBound);
        return generateLongStream(this).limit(streamSize); // fixme broken, just a stub
    }

    @Override
    public boolean nextBoolean() {
        val v = nextInt();
        return (v >> 31) != 0; // fixme check and try to improve via buffer
    }

    @Override
    final public void nextBytes(final byte @NonNull [] bytes) {} // fixme finish this

    /**
     * {@inheritDoc}
     */
    @Override
    final public float nextFloat() {
        return (float) nextDouble(); // fixme improve
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public float nextFloat(final float bound) {
        validateFloatBound(bound);
        return (float) nextDouble(bound); // fixme improve
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public float nextFloat(final float randomNumberOrigin, final float randomNumberBound) {
        val intervalLength = validateFloatRange(randomNumberOrigin, randomNumberBound);
        return (float) doubleFromLongCO(nextLong()); // fixme broken, just a stub
    }

    /**
     * Returns a 64 bit uniformly distributed random number in the semi-open unit interval {@code [0.0,1.0)} (including
     * 0.0 and excluding 1.0).
     *
     * @implSpec This implementation follows JDK convention in terms of included endpoints.
     * @see #nextDouble(doubleUnitIntervalTypes)
     */
    @Override
    final public double nextDouble() {
        return doubleFromLongCO(nextLong());
    }

    @Override
    final public double nextDouble(final double bound) {
        validateDoubleBound(bound);
        return doubleFromLongCO(nextLong()); // fixme broken, just a stub
    }

    @Override
    final public double nextDouble(final double randomNumberOrigin, final double randomNumberBound) {
        val rangeLength = validateDoubleRange(randomNumberOrigin, randomNumberBound);
        return doubleFromLongCO(nextLong()); // fixme broken, just a stub
    }

    /**
     * Generates an {@code int} by calling {@link #nextLong()} and trimming the result.
     *
     * @return a 32 bit uniformly distributed random number in the closed interval
     * {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} (including {@code Integer.MIN_VALUE} and
     * {@code Integer.MAX_VALUE});
     */
    @Override
    public int nextInt() {
        intFlag = !intFlag;
        if (intFlag) {
            bufferInt = nextLong();
            return (int) (bufferInt >> 32);
        } else return (int) (bufferInt << 32 >>> 32);
    }

    /**
     * Generates a random {@code int} in the range {@code [0, bound)} or {@code (bound, -1]} when {@code bound} is
     * positive/negative, respectively.
     *
     * @param bound The limit of the range.
     * @return a random {@code int}.
     * @implSpec the {@code bound} is not included by default.
     */
    @Override
    public int nextInt(final int bound) {
        validateIntBound(bound);
        if (bound > 0) return generateNonNegativeIntInRangeNotIncludeBound(this, bound);
        return ~generateNonNegativeIntInRangeNotIncludeBound(this, ~bound);
    }
// fixme add versions with a bool flag

    /**
     * Allows to switch between closed and semi-open intervals for random number generation.
     * @param bound The conventional bound.
     * @param includeBound A boolean flag, when {@code true} the bound is included, else is identical to
     * {@link #nextInt(int)}.
     * @return a random number in range.
     *
     */
    public int nextInt(final int bound, boolean includeBound) {
        validateIntBound(bound);
        if (includeBound) {
            if (bound > 0) return generateNonNegativeIntInRangeIncludeBound(this, bound);
            else return ~generateNonNegativeIntInRangeIncludeBound(this, ~bound);
        }
        else {
            if (bound > 0) return generateNonNegativeIntInRangeNotIncludeBound(this, bound);
            else return ~generateNonNegativeIntInRangeNotIncludeBound(this, ~bound);
        }
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
        validateIntRange(origin, bound);
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
     * Generates a random {@code long} in the range {@code [0, bound)} or {@code (bound, -1]} when {@code bound} is
     * positive/negative, respectively.
     *
     * @param bound The limit of the range.
     * @return a random {@code long}.
     * @implSpec the {@code bound} is <b>always not</b> included.
     * @see <a href="https://stackoverflow.com/a/46991999">stackoverflow</a>
     */
    @Override
    final public long nextLong(final long bound) {
        validateLongBound(bound);
        if (bound > 0) return generateNonNegativeLongInRangeNotIncludeBound(this, bound);
        else return ~generateNonNegativeLongInRangeNotIncludeBound(this, ~bound);
    }

    /**
     * @see #nextInt(int, boolean)
     */
    final public long nextLong(final long bound, final boolean includeBound) {
        validateLongBound(bound);
        if (includeBound)
            if (bound > 0) return generateNonNegativeLongInRangeIncludeBound(this, bound);
            else return ~generateNonNegativeLongInRangeIncludeBound(this, ~bound);
        else
            if (bound > 0) return generateNonNegativeLongInRangeNotIncludeBound(this, bound);
            else return ~generateNonNegativeLongInRangeNotIncludeBound(this, ~bound);
    }

    /**
     * Generates a random {@code long} number that belongs to the range {@code [origin, bound]}, where both points are
     * included. The actual signs of {@code origin} and {@code bound} don't matter as long as {@code {origin, bound}} is
     * an ordered set, i.e., {@code origin < bound}.
     *
     * @param origin A {@code long} number, can't be {@code Long.MAX_VALUE}.
     * @param bound  A {@code long} number, can't be {@code Long.MIN_VALUE}.
     * @return a random {@code long} from the range {@code [origin, bound]}.
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
        validateLongRange(origin, bound);
        val rangePower = bound - origin + 1;
        if (rangePower > 0) return nextLong(rangePower) + origin;
        if (rangePower < 0) {
            var value = nextLong();
            while (!(value >= origin && value <= bound)) value = nextLong();
            return value;
        }
        return nextLong();
    }

    final public long nextLong(final long origin, final long bound, final boolean includeBound) {
        validateLongRange(origin, bound);
        return nextLong(); // fixme broken just a stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public double nextGaussian() { // fixme do we need this?
        return super.nextGaussian();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public double nextGaussian(final double mean, final double stddev) {
        return super.nextGaussian(mean, stddev);// fixme fma?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public double nextExponential() {
        return super.nextExponential();
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
     * Generates a random {@code double} between 0 and 1. Inclusion of endpoints depends on {@code type}.
     *
     * @param type The interval type, one of the following values:
     *             {@code DOUBLE_OO} for {@code (0, 1)}, {@code DOUBLE_CC} for {@code  [0, 1]}, {@code DOUBLE_OC} for
     *             {@code (0, 1]}, and {@code DOUBLE_CO} for {@code  [0, 1)}.
     * @return a random number from the provided interval.
     * @throws IllegalArgumentException when the provided type is not supported.
     */
    final public double nextDouble(final @NotNull RandomSupport.doubleUnitIntervalTypes type) {
        return switch (type) {
            case DOUBLE_CC -> doubleFromLongCC(nextLong());
            case DOUBLE_OO -> doubleFromLongOO(nextLong());
            case DOUBLE_OC -> doubleFromLongOC(nextLong());
            case DOUBLE_CO -> doubleFromLongCO(nextLong());
        };
    }

    /**
     * Generates random values between 0 and {@code bound}.
     *
     * @param type  The range type.
     * @param bound The range limit, can be positive/negative.
     * @return a random {@code double } from the range.
     * @throws IllegalArgumentException when {bound == 0 || Double.isNaN(bound) || Double.isInfinite(bound)}.
     * @see doubleUnitIntervalTypes
     */
    final public double nextDouble(final @NotNull RandomSupport.doubleUnitIntervalTypes type, final double bound) {
        if (bound == 0 || Double.isNaN(bound) || Double.isInfinite(bound))
            throw new IllegalArgumentException("Bound can't be NaN/0.");

        return nextDouble(type) * bound; // todo check the original implementation for different round-off errors, also check corner cases
    }

    final public double nextDouble(final @NotNull RandomSupport.doubleUnitIntervalTypes type, double origin, double bound) {
        if (Double.isNaN(bound) || Double.isNaN(origin)) throw new IllegalArgumentException("Invalid boundaries.");
        if (origin == bound) throw new IllegalArgumentException("Zero length interval.");
        if (origin > bound) throw new IllegalArgumentException("Input parameters form an unordered set.");
        if (Double.isInfinite(bound) || Double.isInfinite(origin))
            throw new IllegalArgumentException("Infinite limits.");
        if (Double.isInfinite(bound - origin))
            throw new IllegalArgumentException("The range is too large."); // fixme is it possible to avoid this?

        return (nextDouble(type) * (bound - origin)) + origin;// todo check the original implementation for different round-off errors
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
    private int nonNegativeNextInt2(final int bound) {
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
    private long nonNegativeNextLong2(final long bound) {
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
     * @see #nextDouble()
     */
    final public double raw() {
        return nextDouble(doubleUnitIntervalTypes.DOUBLE_CO);
    }

    /**
     * Returns a 64 bit uniformly distributed random number between 0.0 and 1.0. Endpoints are included when a suitable
     * interval type is provided.
     *
     * @see doubleUnitIntervalTypes
     */
    final public double raw(final @NonNull RandomSupport.doubleUnitIntervalTypes type) {
        return nextDouble(type);// fixme check that this needs override
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
}
