package hep.aida;

import java.io.Serializable;

/**
 * A common base interface for IHistogram1D, IHistogram2D and IHistogram3D.
 */
@SuppressWarnings("unused")
public interface IHistogram extends Serializable {
    /**
     * Constant specifying the overflow bin (can be passed to any method expecting a bin number).
     */
    int OVERFLOW = -1;

    /**
     * Constant specifying the underflow bin (can be passed to any method expecting a bin number).
     */
    int UNDERFLOW = -2;

    /**
     * Number of all entries in all (both in-range and under/overflow) bins in the histogram.
     */
    int allEntries();

    /**
     * Returns 1 for one-dimensional histograms, 2 for two-dimensional histograms, and so on.
     */
    int dimensions();

    /**
     * Number of in-range entries in the histogram.
     */
    int entries();

    /**
     * Number of equivalent entries.
     *
     * @return {@code SUM[ weight ] ^ 2 / SUM[ weight^2 ]}.
     */
    double equivalentBinEntries();

    /**
     * Number of under and overflow entries in the histogram.
     */
    int extraEntries();

    /**
     * Reset contents; as if just constructed.
     */
    void reset();

    /**
     * Sum of all (both in-range and under/overflow) bin heights in the
     * histogram.
     */
    double sumAllBinHeights();

    /**
     * Sum of in-range bin heights in the histogram.
     */
    double sumBinHeights();

    /**
     * Sum of under/overflow bin heights in the histogram.
     */
    double sumExtraBinHeights();

    /**
     * Title of the histogram (will be set only in the constructor).
     */
    String title();
}
