package cern.jet.random.engine;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.Date;

/**
 * MersenneTwister (MT19937) is one of the strongest uniform pseudo-random number generators known so far; at the same
 * time it is quick. It produces uniformly distributed {@code int}'s and {@code long}'s in the closed intervals
 * {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} and {@code [Long.MIN_VALUE,Long.MAX_VALUE]}, respectively, as well as
 * {@code double}'s in all possible unit intervals from 0 to 1. The seed can be any 32-bit {@code integer} or 64-bit
 * {@code long} except {@code 0}; the seed should preferably be odd.
 *
 * @implNote This implementation is <b>not synchronized</b>.
 * @see <a href="https://dl.acm.org/doi/10.1145/272991.272995">Makoto Matsumoto and Takuji Nishimura. 1998. Mersenne
 * twister: a 623-dimensionally equidistributed uniform pseudo-random number generator. ACM Trans. Model. Comput. Simul.
 * 8, 1 (Jan. 1998), 3–30.</a>
 * @see <a href="http://www.math.sci.hiroshima-u.ac.jp/m-mat/MT/VERSIONS/C-LANG/mt19937-64.c">mt19937-64.c</a>
 */
// todo https://dl.acm.org/doi/pdf/10.1145/369534.369540 check this out for better version https://dl.acm.org/doi/10.1145/1132973.1132974
public final class MersenneTwister extends RandomEngine { // fixme check that nextint/nextlong limits follow the convention to be passed to shuffle
    @Serial
    private static final long serialVersionUID = 8546229500601388477L;

    private static final long DEFAULT_SEED = 5_489L;
    private static final int RECURRENCE_DEGREE = 312;
    private static final int MIDDLE_WORD = 156;
    private static final long UPPER_MASK = 0xFFFF_FFFF_8000_0000L;
    private static final long LOWER_MASK = 0x7FFF_FFFFL;
    private static final long[] TWIST_MATRIX = {0L, 0xB502_6F5A_A966_19E9L};

    private static final int RMD = RECURRENCE_DEGREE - MIDDLE_WORD;
    private static final int RDM1 = RECURRENCE_DEGREE - 1;
    private static final int RDP1 = RECURRENCE_DEGREE + 1;
    private static final int MWM1 = MIDDLE_WORD - 1;
    private int stateVectorIndex = RECURRENCE_DEGREE + 1;
    private long[] stateVector = new long[RECURRENCE_DEGREE];

    /**
     * Constructs and returns a random number generator with a default seed, which is a <b>constant</b>. This
     * constructor always yields generators that produce exactly the same sequence.
     */
    public MersenneTwister() {
        setSeed(0L);
    }

    /**
     * Constructs and returns a random number generator with the given seed.
     *
     * @param seed should not be 0, in such a case 5489 is silently substituted.
     */
    public MersenneTwister(final int seed) {
        setSeed((long) seed);
    }

    /**
     * Constructs and returns a random number generator with the given seed.
     *
     * @param seed should not be 0, in such a case 5489L is silently substituted.
     */
    public MersenneTwister(final long seed) {
        System.out.println(stateVector.length);
        setSeed(seed);
    }

    /**
     * Constructs and returns a random number generator seeded with the given date.
     *
     * @param d typically {@code new java.util.Date()}
     */
    public MersenneTwister(final @NotNull Date d) {
        setSeed(d.getTime());
    }

    /**
     * Returns a copy of the receiver; the copy will produce identical sequences. After this call has returned, the
     * copy and the receiver have equal but separate state.
     *
     * @return a copy of the receiver.
     */
    @Override
    public @NonNull MersenneTwister clone() {
        val clone = (MersenneTwister) super.clone();
        clone.stateVector = this.stateVector.clone();
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong() {// todo check the idea with caching of mt i.e. cache == mt and mt = cache
        int i;
        long nextValue;

        if (stateVectorIndex >= RECURRENCE_DEGREE) {
            if (stateVectorIndex == RDP1) setSeed(5_489L);

            for (i = 0; i < RMD; i++) {
                nextValue = stateVector[i] & UPPER_MASK | stateVector[i + 1] & LOWER_MASK;
                stateVector[i] = stateVector[i + MIDDLE_WORD] ^ nextValue >>> 1 ^ TWIST_MATRIX[(int) (nextValue & 1L)];
            }

            while (i < RDM1) {
                nextValue = stateVector[i] & UPPER_MASK | stateVector[i + 1] & LOWER_MASK;
                stateVector[i] = stateVector[i - RMD] ^ nextValue >>> 1 ^ TWIST_MATRIX[(int) (nextValue & 1L)];
                i++;
            }

            nextValue = stateVector[RDM1] & UPPER_MASK | stateVector[0] & LOWER_MASK;
            stateVector[RDM1] = stateVector[MWM1] ^ nextValue >>> 1 ^ TWIST_MATRIX[(int) (nextValue & 1L)];

            stateVectorIndex = 0;
        }

        nextValue = stateVector[stateVectorIndex++];

        nextValue ^= nextValue >>> 29 & 0x5555_5555_5555_5555L;
        nextValue ^= nextValue << 17 & 0x71D6_7FFF_EDA6_0000L;
        nextValue ^= nextValue << 37 & 0xFFF7_EEE0_0000_0000L;
        nextValue ^= nextValue >>> 43;

        return nextValue;
    }

    /**
     * Seeds the state array with another array of {@code long}.
     *
     * @param keys Additional values for seeding.
     * @implSpec Does nothing when the input is empty
     */
    @Override
    public void setSeed(final int @Nullable [] keys) {
        if (keys == null || keys.length == 0) return;

        setSeed(19_650_218L);

        int i = 1;
        int j = 0;
        int k = Math.max(RECURRENCE_DEGREE, keys.length);

        while (k != 0) {
            stateVector[i] = (stateVector[i] ^ (stateVector[i - 1] ^ stateVector[i - 1] >>> 62) *
                3_935_559_000_370_003_845L) + keys[j] + j;
            i++;
            j++;
            if (i >= RECURRENCE_DEGREE) {
                stateVector[0] = stateVector[RDM1];
                i = 1;
            }
            if (j >= keys.length) j = 0;
            k--;
        }

        for (k = RDM1; k != 0; k--) {
            stateVector[i] = (stateVector[i] ^ (stateVector[i - 1] ^ stateVector[i - 1] >>> 62) *
                2_862_933_555_777_941_757L) - i;
            i++;
            if (i >= RECURRENCE_DEGREE) {
                stateVector[0] = stateVector[RDM1];
                i = 1;
            }
        }

        stateVector[0] = 1L << 63;
    }

    /**
     * @see #nextLong()
     */
    @Override
    public void setSeed(final int i) {
        setSeed((long) i);
    }

    /**
     * Sets the receiver's seed. This method resets the receiver's entire internal state.
     *
     * @param seed The seed.
     * @implSpec When{@code seed = 0} uses the default value {@code 5489L}.
     */
    @Override
    public void setSeed(final long seed) {
        stateVector = new long[RECURRENCE_DEGREE];
        stateVector[0] = seed == 0 ? DEFAULT_SEED : seed;

        for (stateVectorIndex = 1; stateVectorIndex < RECURRENCE_DEGREE; stateVectorIndex++)
            stateVector[stateVectorIndex] = 6_364_136_223_846_793_005L *
                (stateVector[stateVectorIndex - 1] ^ stateVector[stateVectorIndex - 1] >>> 62) + stateVectorIndex;
    }
}
