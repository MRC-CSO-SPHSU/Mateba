package hep.aida;

import java.io.Serializable;

/**
 * An IAxis represents a binned histogram axis. A 1D Histogram would have one Axis representing the X axis, while a 2D
 * Histogram would have two axes representing the X and Y Axis.
 */
@SuppressWarnings("unused")
public interface IAxis extends Serializable {

    /**
     * Centre of the bin specified.
     *
     * @param index Bin number (0...bins()-1) or OVERFLOW or UNDERFLOW.
     */
    double binCentre(int index);

    /**
     * Lower edge of the specified bin.
     *
     * @param index Bin number (0...bins()-1) or OVERFLOW or UNDERFLOW.
     * @return the lower edge of the bin; for the underflow bin this is {@code Double.NEGATIVE_INFINITY}.
     */
    double binLowerEdge(int index);

    /**
     * The number of bins (excluding underflow and overflow) on the axis.
     */
    int bins();

    /**
     * Upper edge of the specified bin.
     *
     * @param index Bin number (0...bins()-1) or OVERFLOW or UNDERFLOW.
     * @return the upper edge of the bin; for the overflow bin this is
     * {@code Double.POSITIVE_INFINITY}.
     */
    double binUpperEdge(int index);

    /**
     * Width of the bin specified.
     *
     * @param index Bin number (0...bins()-1) or OVERFLOW or UNDERFLOW.
     */
    double binWidth(int index);

    /**
     * Converts a coordinate on the axis to a bin number. If the coordinate is <
     * lowerEdge returns UNDERFLOW, and if the coordinate is >= upperEdge
     * returns OVERFLOW.
     */
    int coordToIndex(double coord);

    /**
     * Lower axis edge.
     */
    double lowerEdge();

    /**
     * Upper axis edge.
     */
    double upperEdge();
}
