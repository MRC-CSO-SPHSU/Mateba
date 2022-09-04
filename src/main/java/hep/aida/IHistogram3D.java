package hep.aida;

/**
 * A Java interface corresponding to the AIDA 3D Histogram.
 * <p>
 * <b>Note</b> All methods that accept a bin number as an argument will also accept the constants OVERFLOW or UNDERFLOW
 * as the argument, and as a result give the contents of the resulting OVERFLOW or UNDERFLOW bin.
 */
@SuppressWarnings("unused")
public interface IHistogram3D extends IHistogram {
    /**
     * The number of entries (ie the number of times fill was called for this bin).
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ the z bin number (0...Nz-1) or OVERFLOW or UNDERFLOW.
     */
    int binEntries(int indexX, int indexY, int indexZ);

    /**
     * The error on this bin.
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ the z bin number (0...Nz-1) or OVERFLOW or UNDERFLOW.
     */
    double binError(int indexX, int indexY, int indexZ);

    /**
     * Total height of the corresponding bin (ie the sum of the weights in this bin).
     *
     * @param indexX the x bin number (0...Nx-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number (0...Ny-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ the z bin number (0...Nz-1) or OVERFLOW or UNDERFLOW.
     */
    double binHeight(int indexX, int indexY, int indexZ);

    /**
     * Fill the histogram with weight 1; equivalent to {@code fill(x,y,z,1)}..
     */
    void fill(double x, double y, double z);

    /**
     * Fill the histogram with specified weight.
     */
    void fill(double x, double y, double z, double weight);

    /**
     * Returns the mean of the histogram, as calculated on filling-time
     * projected on the X axis.
     */
    double meanX();

    /**
     * Returns the mean of the histogram, as calculated on filling-time
     * projected on the Y axis.
     */
    double meanY();

    /**
     * Returns the mean of the histogram, as calculated on filling-time
     * projected on the Z axis.
     */
    double meanZ();

    /**
     * Indexes of the in-range bins containing the smallest and largest
     * binHeight(), respectively.
     *
     * @return {@code {minBinX,minBinY,minBinZ, maxBinX,maxBinY,maxBinZ}}.
     */
    int[] minMaxBins();

    /**
     * Create a projection parallel to the XY plane. Equivalent to
     * {@code sliceXY(UNDERFLOW,OVERFLOW)}.
     */
    IHistogram2D projectionXY();

    /**
     * Create a projection parallel to the XZ plane. Equivalent to
     * {@code sliceXZ(UNDERFLOW,OVERFLOW)}.
     */
    IHistogram2D projectionXZ();

    /**
     * Create a projection parallel to the YZ plane. Equivalent to
     * {@code sliceYZ(UNDERFLOW,OVERFLOW)}.
     */
    IHistogram2D projectionYZ();

    /**
     * Returns the rms of the histogram as calculated on filling-time projected
     * on the X axis.
     */
    double rmsX();

    /**
     * Returns the rms of the histogram as calculated on filling-time projected
     * on the Y axis.
     */
    double rmsY();

    /**
     * Returns the rms of the histogram as calculated on filling-time projected
     * on the Z axis.
     */
    double rmsZ();

    /**
     * Create a slice parallel to the XY plane at bin indexZ and one bin wide.
     * Equivalent to {@code sliceXY(indexZ,indexZ)}.
     */
    IHistogram2D sliceXY(int indexZ);

    /**
     * Create a slice parallel to the XY plane, between "indexZ1" and "indexZ2"
     * (inclusive). The returned IHistogram2D represents an instantaneous
     * snapshot of the histogram at the time the slice was created.
     */
    IHistogram2D sliceXY(int indexZ1, int indexZ2);

    /**
     * Create a slice parallel to the XZ plane at bin indexY and one bin wide.
     * Equivalent to {@code sliceXZ(indexY,indexY)}.
     */
    IHistogram2D sliceXZ(int indexY);

    /**
     * Create a slice parallel to the XZ plane, between "indexY1" and "indexY2"
     * (inclusive). The returned IHistogram2D represents an instantaneous
     * snapshot of the histogram at the time the slice was created.
     */
    IHistogram2D sliceXZ(int indexY1, int indexY2);

    /**
     * Create a slice parallel to the YZ plane at bin indexX and one bin wide.
     * Equivalent to {@code sliceYZ(indexX,indexX)}.
     */
    IHistogram2D sliceYZ(int indexX);

    /**
     * Create a slice parallel to the YZ plane, between "indexX1" and "indexX2"
     * (inclusive). The returned IHistogram2D represents an instantaneous
     * snapshot of the histogram at the time the slice was created.
     */
    IHistogram2D sliceYZ(int indexX1, int indexX2);

    /**
     * Return the X axis.
     */
    IAxis xAxis();

    /**
     * Return the Y axis.
     */
    IAxis yAxis();

    /**
     * Return the Z axis.
     */
    IAxis zAxis();
}
