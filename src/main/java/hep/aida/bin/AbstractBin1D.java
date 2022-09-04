package hep.aida.bin;

import cern.mateba.buffer.tdouble.DoubleBuffer1D;
import cern.mateba.buffer.tdouble.DoubleBuffer1DConsumer;
import cern.mateba.list.tdouble.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * Abstract base class for all 1-dimensional bins consumes {@code double} elements.
 * <p>
 * This class is fully thread safe (all public methods are synchronized). Thus, you can have one or more threads adding
 * to the bin as well as one or more threads reading and viewing the statistics of the bin <i>while it is filled</i>.
 * For high performance, add data in large chunks (buffers) via method {@link #addAllOf} rather than piecewise via
 * method {@link #add}.
 *
 * @see <a href="package-summary.html">package summary</a>
 * @see <a href="package-tree.html">tree view</a>
 */
@SuppressWarnings("unused")
public abstract class AbstractBin1D extends AbstractBin implements DoubleBuffer1DConsumer {

    @Serial
    private static final long serialVersionUID = 3476788492463209410L;

    /**
     * Makes this class non instantiable, but still lets others inherit from it.
     */
    protected AbstractBin1D() {
    }

    /**
     * Adds the specified element to the receiver.
     *
     * @param element element to be appended.
     */
    public abstract void add(double element);

    /**
     * Adds all values of the specified list to the receiver.
     *
     * @param list the list of which all values shall be added.
     */
    public final synchronized void addAllOf(final @NotNull DoubleArrayList list) {
        addAllOfFromTo(list, 0, list.size() - 1);
    }

    /**
     * Adds the part of the specified list between indexes {@code from} (inclusive) and {@code to} (inclusive) to the
     * receiver. You may want to override this method for performance reasons.
     *
     * @param list the list of which elements shall be added.
     * @param from the index of the first element to be added (inclusive).
     * @param to   the index of the last element to be added (inclusive).
     * @throws IndexOutOfBoundsException if {@code list.size() > 0 && (from < 0 || from > to || to >= list.size())}.
     */
    public synchronized void addAllOfFromTo(final @NotNull DoubleArrayList list, final int from, final int to) {
        for (int i = from; i <= to; i++) add(list.getQuick(i));
    }

    /**
     * Constructs and returns a streaming buffer connected to the receiver. Whenever the buffer is full its contents are
     * automatically flushed to {@code this}. (Adding elements via a buffer to a bin is significantly faster than adding
     * them directly.)
     *
     * @param capacity the number of elements the buffer shall be capable of holding before overflowing and flushing to
     *                 the receiver.
     * @return a streaming buffer having the receiver as target.
     */
    public synchronized DoubleBuffer1D buffered(final int capacity) {
        return new DoubleBuffer1D(this, capacity);
    }

    /**
     * Computes the deviations from the receiver's measures to another bin's measures.
     *
     * @param other the other bin to compare with
     * @return a summary of the deviations.
     */
    public String compareWith(final @NotNull AbstractBin1D other) {
        return "\nDifferences [percent]" +
            "\nSize: " + relError(size(), other.size()) + " %" +
            "\nSum: " + relError(sum(), other.sum()) + " %" +
            "\nSumOfSquares: " + relError(sumOfSquares(), other.sumOfSquares()) + " %" +
            "\nMin: " + relError(min(), other.min()) + " %" +
            "\nMax: " + relError(max(), other.max()) + " %" +
            "\nMean: " + relError(mean(), other.mean()) + " %" +
            "\nRMS: " + relError(rms(), other.rms()) + " %" +
            "\nVariance: " + relError(variance(), other.variance()) + " %" +
            "\nStandard deviation: " + relError(standardDeviation(), other.standardDeviation()) + " %" +
            "\nStandard error: " + relError(standardError(), other.standardError()) + " %" +
            "\n";
    }

    /**
     * Checks if two bins are equal. They are equal if the other object is of the same class or a subclass of this class
     * and both have the same size, minimum, maximum, sum and sumOfSquares.
     *
     * @return true when two bins are equal, false otherwise.
     */

    public boolean equals(final Object object) {
        if (!(object instanceof AbstractBin1D other)) return false;
        return size() == other.size() && min() == other.min() && max() == other.max() && sum() == other.sum()
            && sumOfSquares() == other.sumOfSquares();
    }

    /**
     * @return the maximum.
     */
    public abstract double max();

    /**
     * @return the arithmetic mean, which is {@code Sum(x[i]) / size()}.
     */
    public synchronized double mean() {
        return sum() / size();
    }

    /**
     * @return the minimum.
     */
    public abstract double min();

    /**
     * Computes the relative error (in percent) from one measure to another.
     */
    protected double relError(final double measure1, final double measure2) {
        return 100 * (1 - measure1 / measure2);
    }

    /**
     * @return the rms (Root Mean Square), which is {@code Math.sqrt(Sum(x[i] * x[i]) / size())}.
     */
    public synchronized double rms() {
        return Descriptive.rms(size(), sumOfSquares());
    }

    /**
     * @return the sample standard deviation, which is {@code Math.sqrt(variance())}.
     */
    public synchronized double standardDeviation() {
        return Math.sqrt(variance());
    }

    /**
     * @return the sample standard error, which is {@code Math.sqrt(variance() / size())}
     */
    public synchronized double standardError() {
        return Descriptive.standardError(size(), variance());
    }

    /**
     * @return the sum of all elements, which is {@code Sum(x[i])}.
     */
    public abstract double sum();

    /**
     * @return the sum of squares, which is {@code Sum(x[i] * x[i])}.
     */
    public abstract double sumOfSquares();

    /**
     * @return a String representation of the receiver.
     */
    public synchronized String toString() {
        return getClass().getName() +
            "\n-------------" +
            "\nSize: " + size() +
            "\nSum: " + sum() +
            "\nSumOfSquares: " + sumOfSquares() +
            "\nMin: " + min() +
            "\nMax: " + max() +
            "\nMean: " + mean() +
            "\nRMS: " + rms() +
            "\nVariance: " + variance() +
            "\nStandard deviation: " + standardDeviation() +
            "\nStandard error: " + standardError() +
            "\n";
    }

    /**
     * Trims the capacity of the receiver to be the receiver's current size. Releases any superfluous internal memory.
     * An application can use this operation to minimize the storage of the receiver.
     *
     * @implNote This default implementation does nothing.
     */

    public synchronized void trimToSize() {
    }

    /**
     * @return the sample variance, which is {@code Sum((x[i] - mean())}<sup>2</sup>{@code )  /  (size() - 1)}.
     */
    public synchronized double variance() {
        return Descriptive.sampleVariance(size(), sum(), sumOfSquares());
    }
}
