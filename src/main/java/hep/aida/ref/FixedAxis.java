package hep.aida.ref;

import hep.aida.IAxis;
import hep.aida.IHistogram;

import java.io.Serial;

/**
 * Fixed-width axis; A reference implementation of {@link IAxis}.
 */
@SuppressWarnings("unused")
public class FixedAxis implements IAxis {

    @Serial
    private static final long serialVersionUID = -5488773274632632273L;
    private final int bins;

    private final double min;

    private final double binWidth;

    private int xunder, xover;

    /**
     * Create an Axis
     *
     * @param bins Number of bins
     * @param min  Minimum for axis
     * @param max  Maximum for axis
     * @implNote For internal consistency we save only min and binWidth and always use these quantities for all
     * calculations. Due to rounding errors the return value from upperEdge is not necessarily exactly equal to max.
     * @implSpec Our internal definition of overflow/underflow differs from that of the outside world this.under = 0;
     * this.over = bins+1;
     */
    public FixedAxis(final int bins, final double min, final double max) {
        if (bins < 1) throw new IllegalArgumentException("bins=" + bins);
        if (max <= min) throw new IllegalArgumentException("max <= min");

        this.bins = bins;
        this.min = min;
        this.binWidth = (max - min) / bins;
    }

    public double binCentre(final int index) {
        return min + binWidth * index + binWidth / 2;
    }

    public double binLowerEdge(final int index) {
        if (index == IHistogram.UNDERFLOW) return Double.NEGATIVE_INFINITY;
        if (index == IHistogram.OVERFLOW) return upperEdge();
        return min + binWidth * index;
    }

    public int bins() {
        return bins;
    }

    public double binUpperEdge(final int index) {
        if (index == IHistogram.UNDERFLOW) return min;
        if (index == IHistogram.OVERFLOW) return Double.POSITIVE_INFINITY;
        return min + binWidth * (index + 1);
    }

    public double binWidth(final int index) {
        return binWidth;
    }

    public int coordToIndex(final double coord) {
        if (coord < min) return IHistogram.UNDERFLOW;
        int index = (int) Math.floor((coord - min) / binWidth);
        return index >= bins ? IHistogram.OVERFLOW : index;
    }

    public double lowerEdge() {
        return min;
    }

    public double upperEdge() {
        return min + binWidth * bins;
    }

    /**
     * This package private method is similar to coordToIndex except that it returns our internal definition for
     * overflow/underflow
     */
    int xgetBin(final double coord) {
        if (coord < min) return xunder;
        int index = (int) Math.floor((coord - min) / binWidth);
        return index > bins ? xover : index + 1;
    }

    /**
     * Package private method to map from the external representation of bin number to our internal representation of
     * bin number
     */
    int xmap(final int index) {
        if (index >= bins)
            throw new IllegalArgumentException("bin=" + index);
        if (index >= 0) return index + 1;
        return switch (index) {
            case IHistogram.UNDERFLOW -> xunder;
            case IHistogram.OVERFLOW -> xover;
            default -> throw new IllegalArgumentException("bin=" + index);
        };
    }
}
