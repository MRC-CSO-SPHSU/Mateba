package hep.aida;

/**
 * A Java interface corresponding to the AIDA 1D Histogram.
 * <p>
 * <b>Note</b> All methods that accept a bin number as an argument will also accept the constants OVERFLOW or UNDERFLOW
 * as the argument, and as a result give the contents of the resulting OVERFLOW or UNDERFLOW bin.
 */
@SuppressWarnings("unused")
public interface IHistogram1D extends IHistogram {
    /**
     * Number of entries in the corresponding bin (ie the number of times fill was called for this bin).
     *
     * @param index the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     */
    int binEntries(int index);

    /**
     * The error on this bin.
     *
     * @param index the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     */
    double binError(int index);

    /**
     * Total height of the corresponding bin (ie the sum of the weights in this bin).
     *
     * @param index the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     */
    double binHeight(int index);

    /**
     * Fill histogram with weight 1.
     */
    void fill(double x);

    /**
     * Fill histogram with specified weight.
     */
    void fill(double x, double weight);

    /**
     * Fill histogram with specified data and weight 1.
     */
    void fill_2D(final double[] data, final int rows, final int columns, final int zero, final int rowStride,
                        final int columnStride);

    /**
     * Fill histogram with specified data and weights.
     */
    void fill_2D(final double[] data, final double[] weights, final int rows, final int columns, final int zero,
                        final int rowStride, final int columnStride);

    /**
     * Returns the mean of the whole histogram as calculated on filling-time.
     */
    double mean();

    /**
     * Indexes of the in-range bins containing the smallest and largest
     * binHeight(), respectively.
     *
     * @return {@code {minBin,maxBin}}.
     */
    int[] minMaxBins();

    /**
     * Returns the rms of the whole histogram as calculated on filling-time.
     */
    double rms();

    /**
     * Returns the X Axis.
     */
    IAxis xAxis();
}
