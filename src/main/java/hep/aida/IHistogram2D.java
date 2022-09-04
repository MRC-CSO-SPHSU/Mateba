package hep.aida;

/**
 * A Java interface corresponding to the AIDA 2D Histogram.
 * <p>
 * <b>Note</b> All methods that accept a bin number as an argument will also accept the constants OVERFLOW or UNDERFLOW
 * as the argument, and as a result give the contents of the resulting OVERFLOW or UNDERFLOW bin.
 */
@SuppressWarnings("unused")
public interface IHistogram2D extends IHistogram {
    /**
     * The number of entries (ie the number of times fill was called for this bin).
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     */
    int binEntries(int indexX, int indexY);

    /**
     * Equivalent to {@code projectionX().binEntries(indexX)}.
     */
    int binEntriesX(int indexX);

    /**
     * Equivalent to {@code projectionY().binEntries(indexY)}.
     */
    int binEntriesY(int indexY);

    /**
     * The error on this bin.
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     */
    double binError(int indexX, int indexY);

    /**
     * Total height of the corresponding bin (ie the sum of the weights in this bin).
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     */
    double binHeight(int indexX, int indexY);

    /**
     * Equivalent to {@code projectionX().binHeight(indexX)}.
     */
    double binHeightX(int indexX);

    /**
     * Equivalent to {@code projectionY().binHeight(indexY)}.
     */
    double binHeightY(int indexY);

    /**
     * Fill the histogram with weight 1.
     */
    void fill(double x, double y);

    /**
     * Fill the histogram with specified weight.
     */
    void fill(double x, double y, double weight);

    /**
     * Returns the mean of the histogram, as calculated on filling-time projected on the X axis.
     */
    double meanX();

    /**
     * Returns the mean of the histogram, as calculated on filling-time projected on the Y axis.
     */
    double meanY();

    /**
     * Indexes of the in-range bins containing the smallest and largest binHeight(), respectively.
     *
     * @return {@code {minBinX,minBinY, maxBinX,maxBinY}}.
     */
    int[] minMaxBins();

    /**
     * Create a projection parallel to the X axis. Equivalent to {@code sliceX(UNDERFLOW,OVERFLOW)}.
     */
    IHistogram1D projectionX();

    /**
     * Create a projection parallel to the Y axis. Equivalent to {@code sliceY(UNDERFLOW,OVERFLOW)}.
     */
    IHistogram1D projectionY();

    /**
     * Returns the rms of the histogram as calculated on filling-time projected on the X axis.
     */
    double rmsX();

    /**
     * Returns the rms of the histogram as calculated on filling-time projected on the Y axis.
     */
    double rmsY();

    /**
     * Slice parallel to the Y axis at bin indexY and one bin wide. Equivalent to {@code sliceX(indexY,indexY)}.
     */
    IHistogram1D sliceX(int indexY);

    /**
     * Create a slice parallel to the axis X axis, between "indexY1" and "indexY2" (inclusive). The returned
     * IHistogram1D represents an instantaneous snapshot of the histogram at the time the slice was created.
     */
    IHistogram1D sliceX(int indexY1, int indexY2);

    /**
     * Slice parallel to the X axis at bin indexX and one bin wide. Equivalent to {@code sliceY(indexX,indexX)}.
     */
    IHistogram1D sliceY(int indexX);

    /**
     * Create a slice parallel to the axis Y axis, between "indexX1" and "indexX2" (inclusive) The returned IHistogram1D
     * represents an instantaneous snapshot of the histogram at the time the slice was created.
     */
    IHistogram1D sliceY(int indexX1, int indexX2);

    /**
     * Return the X axis.
     */
    IAxis xAxis();

    /**
     * Return the Y axis.
     */
    IAxis yAxis();
}
