package utils;

public class Utils {
    /**
     * A speedy {@code abs} function implementation.
     *
     * @param i An {@code integer} number
     * @return the absolute value of any {@code integer} number
     * @throws IllegalArgumentException when {@code Integer.MIN_VALUE} is passed to the method.
     * @see <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerAbs">Bit Twiddling Hacks</a>
     */
    public static int absInt(final int i){
        if (i == Integer.MIN_VALUE) throw new IllegalArgumentException("Integer.MIN_VALUE results in an overflow.");
        final int mask = i >> 31;
        return (i + mask) ^ mask;
    }

    /**
     * A speedy {@code abs} function implementation.
     *
     * @param l A {@code long} number
     * @return the absolute value of any {@code long} number
     * @throws IllegalArgumentException when {@code Long.MIN_VALUE} is passed to the method.
     * @see <a href="https://graphics.stanford.edu/~seander/bithacks.html#IntegerAbs">Bit Twiddling Hacks</a>
     */
    public static long absLong(final long l){
        if (l == Long.MIN_VALUE) throw new IllegalArgumentException("Long.MIN_VALUE results in an overflow.");
        final long mask = l >> 63;
        return (l + mask) ^ mask;
    }
}
