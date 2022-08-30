package hep.aida.ref;

import hep.aida.IAxis;
import hep.aida.IHistogram;
import hep.aida.IHistogram1D;

import java.io.Serial;

/**
 * Abstract base class extracting and implementing most of the redundancy of the interface.
 */
@SuppressWarnings("unused")
abstract class AbstractHistogram1D extends Histogram implements IHistogram1D {

    @Serial
    private static final long serialVersionUID = 8090024737598169832L;
    protected IAxis xAxis;

    AbstractHistogram1D(String title) {
        super(title);
    }

    public int allEntries() {
        return entries() + extraEntries();
    }

    public int dimensions() {
        return 1;
    }

    public int entries() {
        int entries = 0;
        for (int i = xAxis.bins(); --i >= 0; ) entries += binEntries(i);
        return entries;
    }

    public int extraEntries() {
        return binEntries(UNDERFLOW) + binEntries(OVERFLOW);
    }

    /**
     * Package private method to map from the external representation of bin number to our internal representation of
     * bin number
     */
    int map(int index) {
        int bins = xAxis.bins() + 2;
        if (index >= bins)
            throw new IllegalArgumentException("bin=" + index);
        if (index >= 0)
            return index + 1;
        if (index == IHistogram.UNDERFLOW)
            return 0;
        if (index == IHistogram.OVERFLOW)
            return bins - 1;
        throw new IllegalArgumentException("bin=" + index);
    }

    public int[] minMaxBins() {
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        int minBinX = -1;
        int maxBinX = -1;
        for (int i = xAxis.bins(); --i >= 0; ) {
            double value = binHeight(i);
            if (value < minValue) {
                minValue = value;
                minBinX = i;
            }
            if (value > maxValue) {
                maxValue = value;
                maxBinX = i;
            }
        }
        return new int[]{minBinX, maxBinX};
    }

    public double sumAllBinHeights() {
        return sumBinHeights() + sumExtraBinHeights();
    }

    public double sumBinHeights() {
        double sum = 0;
        for (int i = xAxis.bins(); --i >= 0; ) sum += binHeight(i);
        return sum;
    }

    public double sumExtraBinHeights() {
        return binHeight(UNDERFLOW) + binHeight(OVERFLOW);
    }

    public IAxis xAxis() {
        return xAxis;
    }
}
