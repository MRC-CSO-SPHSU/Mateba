package cern.jet.random.engine;

import lombok.val;

public class Validators {

    final private static String STREAM_SIZE = "Stream size is negative.";
    final private static String BOUND_ORDER = "Bad order of range limits.";
    final private static String RANGE_LENGTH = "Interval is of infinite length, " +
        "currently, no implementation supports that.";
    final private static String BOUND_NAN_INFINITY = "Bound value is NaN/Infinity.";

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
        if (Double.isInfinite(bound) || Double.isNaN(bound)) throw new IllegalArgumentException(BOUND_NAN_INFINITY);
    }

    static void validateFloatBound(final float bound) {
        if (Float.isInfinite(bound) || Float.isNaN(bound)) throw new IllegalArgumentException(BOUND_NAN_INFINITY);
    }
}
