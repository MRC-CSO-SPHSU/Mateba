package hep.aida.ref;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;

import java.io.Serial;
import java.util.concurrent.Future;

/**
 * A reference implementation of {@link IHistogram1D}. The goal is to provide a clear implementation rather than the
 * most efficient implementation.
 */
@SuppressWarnings("unused")
public class Histogram1D extends AbstractHistogram1D implements IHistogram1D {
    @Serial
    private static final long serialVersionUID = 2824448963399293909L;
    private double[] errors;

    private double[] heights;

    private int[] entries;

    private int nEntry;

    private double sumWeight;

    private double sumWeightSquared;

    private double mean, rms;

    /**
     * Creates a variable-width histogram. Example:
     * {@code edges = (0.2, 1.0, 5.0)} yields an axis with 2 in-range bins
     * {@code [0.2,1.0), [1.0,5.0)} and 2 extra bins
     * {@code [-inf,0.2), [5.0,inf]}.
     *
     * @param title The histogram title.
     * @param edges the bin boundaries the axis shall have; must be sorted ascending and must not contain multiple
     *              identical elements.
     * @throws IllegalArgumentException if {@code edges.length < 1}.
     */
    public Histogram1D(String title, double[] edges) {
        this(title, new VariableAxis(edges));
    }

    /**
     * Creates a histogram with the given axis binning.
     *
     * @param title The histogram title.
     * @param axis  The axis description to be used for binning.
     */
    public Histogram1D(String title, IAxis axis) {
        super(title);
        xAxis = axis;
        int bins = axis.bins();
        entries = new int[bins + 2];
        heights = new double[bins + 2];
        errors = new double[bins + 2];
    }

    /**
     * Creates a fixed-width histogram.
     *
     * @param title The histogram title.
     * @param bins  The number of bins.
     * @param min   The minimum value on the X axis.
     * @param max   The maximum value on the X axis.
     */
    public Histogram1D(String title, int bins, double min, double max) {
        this(title, new FixedAxis(bins, min, max));
    }

    public int allEntries() {
        return nEntry;
    }

    public int binEntries(int index) {
        return entries[map(index)];
    }

    public double binError(int index) {
        return Math.sqrt(errors[map(index)]);
    }

    public double binHeight(int index) {
        return heights[map(index)];
    }

    public double equivalentBinEntries() {
        return sumWeight * sumWeight / sumWeightSquared;
    }

    public void fill(double x) {
        int bin = map(xAxis.coordToIndex(x));
        entries[bin]++;
        heights[bin]++;
        errors[bin]++;
        nEntry++;
        sumWeight++;
        sumWeightSquared++;
        mean += x;
        rms += x * x;
    }

    public void fill(double x, double weight) {
        int bin = map(xAxis.coordToIndex(x));
        entries[bin]++;
        heights[bin] += weight;
        errors[bin] += weight * weight;
        nEntry++;
        sumWeight += weight;
        sumWeightSquared += weight * weight;
        mean += x * weight;
        rms += x * weight * weight;
    }

    public void fill_2D(final double[] data, final int rows, final int columns, final int zero, final int rowStride,
                        final int columnStride) {
        int nthreads = ConcurrencyUtils.getNumberOfThreads();
        if ((nthreads > 1) && (rows * columns >= ConcurrencyUtils.getThreadsBeginN_1D())) {
            nthreads = Math.min(nthreads, rows);
            Future<?>[] futures = new Future[nthreads];
            int k = rows / nthreads;
            for (int j = 0; j < nthreads; j++) {
                final int firstRow = j * k;
                final int lastRow = (j == nthreads - 1) ? rows : firstRow + k;
                futures[j] = ConcurrencyUtils.submit(new Runnable() {

                    public void run() {
                        double[] errors_loc = new double[errors.length];
                        double[] heights_loc = new double[heights.length];
                        int[] entries_loc = new int[entries.length];
                        int nEntry_loc = 0;
                        double sumWeight_loc = 0;
                        double sumWeightSquared_loc = 0;
                        double mean_loc = 0, rms_loc = 0;
                        int idx = zero + firstRow * rowStride;
                        for (int r = firstRow; r < lastRow; r++) {
                            for (int i = idx, c = 0; c < columns; c++) {
                                int bin = map(xAxis.coordToIndex(data[i]));
                                entries_loc[bin]++;
                                heights_loc[bin]++;
                                errors_loc[bin]++;
                                nEntry_loc++;
                                sumWeight_loc++;
                                sumWeightSquared_loc++;
                                mean_loc += data[i];
                                rms_loc += data[i] * data[i];
                                i += columnStride;
                            }
                            idx += rowStride;
                        }
                        synchronized (this) {
                            for (int i = 0; i < entries.length; i++) {
                                errors[i] += errors_loc[i];
                                heights[i] += heights_loc[i];
                                entries[i] += entries_loc[i];
                            }
                            nEntry += nEntry_loc;
                            sumWeight += sumWeight_loc;
                            sumWeightSquared += sumWeightSquared_loc;
                            mean += mean_loc;
                            rms += rms_loc;
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);
        } else {
            int idx = zero;
            for (int r = 0; r < rows; r++) {
                for (int i = idx, c = 0; c < columns; c++) {
                    int bin = map(xAxis.coordToIndex(data[i]));
                    entries[bin]++;
                    heights[bin]++;
                    errors[bin]++;
                    nEntry++;
                    sumWeight++;
                    sumWeightSquared++;
                    mean += data[i];
                    rms += data[i] * data[i];
                    i += columnStride;
                }
                idx += rowStride;
            }
        }
    }

    public void fill_2D(final double[] data, final double[] weights, final int rows, final int columns, final int zero,
                        final int rowStride, final int columnStride) {
        int nthreads = ConcurrencyUtils.getNumberOfThreads();
        if ((nthreads > 1) && (rows * columns >= ConcurrencyUtils.getThreadsBeginN_1D())) {
            nthreads = Math.min(nthreads, rows);
            Future<?>[] futures = new Future[nthreads];
            int k = rows / nthreads;
            for (int j = 0; j < nthreads; j++) {
                final int firstRow = j * k;
                final int lastRow = (j == nthreads - 1) ? rows : firstRow + k;
                futures[j] = ConcurrencyUtils.submit(new Runnable() {

                    public void run() {
                        int idx = zero + firstRow * rowStride;
                        double[] errors_loc = new double[errors.length];
                        double[] heights_loc = new double[heights.length];
                        int[] entries_loc = new int[entries.length];
                        int nEntry_loc = 0;
                        double sumWeight_loc = 0;
                        double sumWeightSquared_loc = 0;
                        double mean_loc = 0, rms_loc = 0;
                        for (int r = firstRow; r < lastRow; r++) {
                            for (int i = idx, c = 0; c < columns; c++) {
                                int bin = map(xAxis.coordToIndex(data[i]));
                                int widx = r * columns + c;
                                double w2 = weights[widx] * weights[widx];
                                entries_loc[bin]++;
                                heights_loc[bin] += weights[widx];
                                errors_loc[bin] += w2;
                                nEntry_loc++;
                                sumWeight_loc += weights[widx];
                                sumWeightSquared_loc += w2;
                                mean_loc += data[i] * weights[widx];
                                rms_loc += data[i] * w2;
                                i += columnStride;
                            }
                            idx += rowStride;
                        }
                        synchronized (this) {
                            for (int i = 0; i < entries.length; i++) {
                                errors[i] += errors_loc[i];
                                heights[i] += heights_loc[i];
                                entries[i] += entries_loc[i];
                            }
                            nEntry += nEntry_loc;
                            sumWeight += sumWeight_loc;
                            sumWeightSquared += sumWeightSquared_loc;
                            mean += mean_loc;
                            rms += rms_loc;
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);
        } else {
            int idx = zero;
            for (int r = 0; r < rows; r++) {
                for (int i = idx, c = 0; c < columns; c++) {
                    int bin = map(xAxis.coordToIndex(data[i]));
                    int widx = r * columns + c;
                    double w2 = weights[widx] * weights[widx];
                    entries[bin]++;
                    heights[bin] += weights[widx];
                    errors[bin] += w2;
                    nEntry++;
                    sumWeight += weights[widx];
                    sumWeightSquared += w2;
                    mean += data[i] * weights[widx];
                    rms += data[i] * w2;
                    i += columnStride;
                }
                idx += rowStride;
            }
        }
    }

    /**
     * Returns the contents of this histogram.
     *
     * @return the contents of this histogram
     */
    public Histogram1DContents getContents() {
        return new Histogram1DContents(entries, heights, errors, nEntry, sumWeight, sumWeightSquared, mean, rms);
    }

    /**
     * Sets the contents of this histogram.
     */
    public void setContents(Histogram1DContents contents) {
        this.entries = contents.getEntries();
        this.heights = contents.getHeights();
        this.errors = contents.getErrors();
        this.nEntry = contents.getNEntry();
        this.sumWeight = contents.getSumWeight();
        this.sumWeightSquared = contents.getSumWeightSquared();
        this.mean = contents.getMean();
        this.rms = contents.getRms();
    }

    public double mean() {
        return mean / sumWeight;
    }

    public void reset() {
        for (int i = 0; i < entries.length; i++) {
            entries[i] = 0;
            heights[i] = 0;
            errors[i] = 0;
        }
        nEntry = 0;
        sumWeight = 0;
        sumWeightSquared = 0;
        mean = 0;
        rms = 0;
    }

    public double rms() {
        return Math.sqrt(rms / sumWeight - mean * mean / sumWeight / sumWeight);
    }

    /**
     * Used internally for creating slices and projections
     */
    void setContents(int[] entries, double[] heights, double[] errors) {
        this.entries = entries;
        this.heights = heights;
        this.errors = errors;

        for (int i = 0; i < entries.length; i++) {
            nEntry += entries[i];
            sumWeight += heights[i];
        }
        // TODO: Can we do anything sensible/useful with the other statistics?
        sumWeightSquared = Double.NaN;
        mean = Double.NaN;
        rms = Double.NaN;
    }
}
