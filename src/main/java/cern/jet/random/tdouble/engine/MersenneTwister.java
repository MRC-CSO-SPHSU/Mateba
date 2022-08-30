/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.random.tdouble.engine;

import java.io.Serial;
import java.util.Date;

/**
 * MersenneTwister (MT19937) is one of the strongest uniform pseudo-random number generators known so far; at the same
 * time it is quick. It produces uniformly distributed {@code int}'s and {@code long}'s in the closed intervals
 * {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} and {@code [Long.MIN_VALUE,Long.MAX_VALUE]}, respectively, as well as
 * {@code float}'s and {@code double}'s in all possible unit intervals from 0 to 1. The seed can be
 * any 32-bit {@code integer} or 64-bit {@code long} except {@code 0}; the seed should preferably be odd.
 * <p>
 * MersenneTwister generates random numbers in batches of 624 numbers at a time, so the caching and pipelining of modern
 * systems is exploited. The generator is implemented to generate the output by using the fastest arithmetic operations
 * only: 32-bit additions and bit operations (no division, no multiplication, no mod). These operations generate
 * sequences of 32 random bits ({@code int}'s). {@code long}'s are formed by concatenating two 32 bit {@code int}'s.
 * {@code float}'s are formed by dividing the interval {@code [0.0,1.0]} into 2<sup>32</sup> sub intervals, then
 * randomly choosing one subinterval. {@code double}'s are formed by dividing the interval {@code [0.0,1.0]} into
 * 2<sup>64</sup> sub intervals, then randomly choosing one subinterval.
 *
 * @implSpec After M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-Dimensionally Equidistributed Uniform
 * Pseudo-Random Number Generator", ACM Transactions on Modeling and Computer Simulation, Vol. 8, No. 1, January 1998,
 * pp 3--30.
 * <dt>More info on <A HREF="http://www.math.keio.ac.jp/~matumoto/eindex.html">Masumoto's homepage</A>.
 * <dt>More info on <A HREF="http://www.ncsa.uiuc.edu/Apps/CMP/RNG/www-rng.html"> Pseudo-random number generators is on
 * the Web</A>.
 * <dt>Yet <A HREF="http://nhse.npac.syr.edu/random"> some more info</A>.
 * <p>
 * The correctness of this implementation has been verified against the published output sequence
 * <a href="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.out">mt19937-2.out</a>
 * of the C-implementation <ahref="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.c">mt19937-2.c</a>.
 * (Call {@code test(1000)} to print the sequence).
 * @implNote This implementation is <b>not synchronized</b>.
 * <p>
 */
public class MersenneTwister extends DoubleRandomEngine implements Cloneable {
    private static final int NN = 312;
    private static final int MM = 156;
    private static final long MATRIX_A_U = 0xB502_6F5A_A966_19E9L;

    /**
     * Most significant 33 bits, upper mask.
     */
    private static final long UM_U = 0xFFFF_FFFF_8000_0000L;

    /**
     * Least significant 31 bits, lower mask.
     */
    private static final long LM_U = 0x7FFF_FFFFL;
    private static final long[] mag01_U = {0L, MATRIX_A_U};
    @Serial
    private static final long serialVersionUID = 8546229500601388477L;

    /**
     * mti==NN+1 means mt[NN] is not initialized
     */
    private static int mti = NN + 1;

    /**
     * The array for the state vector
     */
    private long[] mt_U = new long[NN];

    /**
     * Constructs and returns a random number generator with a default seed, which is a <b>constant</b>. Thus using this
     * constructor will yield generators that always produce exactly the same sequence. This method is mainly intended
     * to ease testing and debugging.
     */
    public MersenneTwister() {
        this(5489);
    }

    /**
     * Constructs and returns a random number generator with the given seed.
     *
     * @param seed should not be 0, in such a case 5489 is silently substituted.
     */
    public MersenneTwister(int seed) {
        setSeed(seed);
    }

    /**
     * Constructs and returns a random number generator seeded with the given date.
     *
     * @param d typically {@code new java.util.Date()}
     */
    public MersenneTwister(Date d) {
        this((int) d.getTime());
    }

    /**
     * Returns a copy of the receiver; the copy will produce identical sequences. After this call has returned, the
     * copy and the receiver have equal but separate state.
     *
     * @return a copy of the receiver.
     */
    @Override
    public Object clone() {
        MersenneTwister clone;
        try {
            clone = (MersenneTwister) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.mt_U = mt_U.clone();
        return clone;
    }

    /**
     * Generates a random number on [-2^63, 2^63-1]-interval.
     */
    public long nextLong() {
        int i;
        long x_U;

        if (mti >= NN) {// todo check the idea with caching of mt i.e. cache == mt and mt = cache
            if (mti == NN + 1) setSeed(5_489L);

            for (i = 0; i < NN - MM; i++) {
                x_U = mt_U[i] & UM_U | mt_U[i + 1] & LM_U;
                mt_U[i] = mt_U[i + MM] ^ x_U >>> 1 ^ mag01_U[(int) (x_U & 1L)];
            }
            for (; i < NN - 1; i++) {
                x_U = mt_U[i] & UM_U | mt_U[i + 1] & LM_U;
                mt_U[i] = mt_U[i + (MM - NN)] ^ x_U >>> 1 ^ mag01_U[(int) (x_U & 1L)];
            }
            x_U = mt_U[NN - 1] & UM_U | mt_U[0] & LM_U;
            mt_U[NN - 1] = mt_U[MM - 1] ^ x_U >>> 1 ^ mag01_U[(int) (x_U & 1L)];

            mti = 0;
        }

        x_U = mt_U[mti++];

        x_U ^= x_U >>> 29 & 0x5555555555555555L;
        x_U ^= x_U << 17 & 0x71D67FFFEDA60000L;
        x_U ^= x_U << 37 & 0xFFF7EEE000000000L;
        x_U ^= x_U >>> 43;

        return x_U;
    }

    /* initialize by an array with array-length */
    /* init_key is the array for initializing keys */
    @Override
    public void setSeed(int[] initKey_U) {

        int i = 1;
        int j = 0;
        int k = Math.max(NN, initKey_U.length);

        setSeed(19_650_218L);

        for (; k != 0; k--) {
            mt_U[i] = (mt_U[i] ^ (mt_U[i - 1] ^ mt_U[i - 1] >>> 62) * 3_935_559_000_370_003_845L) + initKey_U[j] + j; /* non linear */
            i++;
            j++;
            if (i >= NN) {
                mt_U[0] = mt_U[NN - 1];
                i = 1;
            }
            if (j >= initKey_U.length) {
                j = 0;
            }
        }

        for (k = NN - 1; k != 0; k--) {
            mt_U[i] = (mt_U[i] ^ (mt_U[i - 1] ^ mt_U[i - 1] >>> 62) * 2_862_933_555_777_941_757L) - i; /* non linear */
            i++;
            if (i >= NN) {
                mt_U[0] = mt_U[NN - 1];
                i = 1;
            }
        }

        mt_U[0] = 1L << 63; /* MSB is 1; assuring non-zero initial array */
    }

    /**
     * @see #nextLong()
     */
    @Override
    public void setSeed(int i) {
        setSeed((long) i);
    }

    /**
     * Sets the receiver's seed. This method resets the receiver's entire internal state.
     *
     * @param seed_U The seed.
     */
    public void setSeed(long seed_U) {
        mt_U[0] = seed_U == 0 ? 5489L : seed_U;
        for (mti = 1; mti < NN; mti++) {
            mt_U[mti] = 6_364_136_223_846_793_005L * (mt_U[mti - 1] ^ mt_U[mti - 1] >>> 62) + mti;
        }
    }
}
