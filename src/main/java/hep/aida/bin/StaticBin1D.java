package hep.aida.bin;

import cern.mateba.list.tdouble.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * 1-dimensional non-rebinnable bin consuming {@code double} elements; Efficiently computes basic statistics of data
 * sequences.
 * <p>
 * The data streamed into a {@link StaticBin1D} is not preserved! As a consequence infinitely many elements can be added
 * to this bin. As a further consequence this bin cannot compute more than basic statistics. It is also not rebinnable.
 * If these drawbacks matter, consider to use a {@link DynamicBin1D}, which overcomes them at the expense of increased
 * memory requirements.
 * <p>
 * This class is fully thread safe (all public methods are synchronized). Thus, you can have one or more threads adding
 * to the bin as well as one or more threads reading and viewing the statistics of the bin <i>while it is filled</i>.
 * For high performance, add data in large chunks (buffers) via method {@link #addAllOf} rather than piecewise via
 * method {@link #add}.
 *
 * @see <a href="package-summary.html">package summary</a>
 * @see <a href="package-tree.html">tree view</a>
 */
public class StaticBin1D extends AbstractBin1D implements Cloneable {

    /**
     * Function arguments used by method {@link #addAllOf(DoubleArrayList)} For memory tuning only. Avoids allocating a new
     * array of arguments each time {@link #addAllOf(DoubleArrayList)} is called.
     * <p>
     * Each bin does not need its own set of argument vars since they are declared as {@code static}.
     * {@link #addAllOf(DoubleArrayList)} of this class uses only 4 entries. Subclasses computing additional incremental
     * statistics may need more arguments. So, to be on the safe side we allocate space for 20 args. Be sure you access
     * these arguments only in synchronized blocks like synchronized (arguments) { do it }
     * <p>
     * By the way, the whole fuss would be unnecessary if Java would know INOUT parameters (call by reference).
     */
    final static protected double[] arguments = new double[20];
    @Serial
    private static final long serialVersionUID = 5631495510077600908L;
    /**
     * The number of elements consumed by incremental parameter maintenance.
     */
    protected int size = 0;

    protected double min = 0.0;
    protected double max = 0.0;
    protected double sum = 0.0;
    protected double sum_xx = 0.0;

    /**
     * Constructs and returns an empty bin.
     */
    public StaticBin1D() {
        clear();
    }

    /**
     * Adds the specified element to the receiver.
     *
     * @param element element to be appended.
     */

    public synchronized void add(final double element) {
        this.addAllOf(new DoubleArrayList(new double[]{element}));
    }

    /**
     * Adds the part of the specified list between indexes {@code from} (inclusive) and {@code to} (inclusive) to the
     * receiver.
     *
     * @param list the list of which elements shall be added.
     * @param from the index of the first element to be added (inclusive).
     * @param to   the index of the last element to be added (inclusive).
     * @throws IndexOutOfBoundsException if {@code list.size() > 0 && (from < 0 || from > to || to >= list.size())}.
     */
    public synchronized void addAllOfFromTo(final @NotNull DoubleArrayList list, final int from, final int to) {
        synchronized (arguments) {

            arguments[0] = this.min;
            arguments[1] = this.max;
            arguments[2] = this.sum;
            arguments[3] = this.sum_xx;

            Descriptive.incrementalUpdate(list, from, to, arguments);

            this.min = arguments[0];
            this.max = arguments[1];
            this.sum = arguments[2];
            this.sum_xx = arguments[3];

            this.size += to - from + 1;
        }
    }

    /**
     * Removes all elements from the receiver. The receiver will be empty after this call returns.
     */
    public synchronized void clear() {
        clearAllMeasures();
        this.size = 0;
    }

    /**
     * Resets the values of all measures.
     */
    protected void clearAllMeasures() {
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        this.sum = 0.0;
        this.sum_xx = 0.0;
    }

    /**
     * Returns whether a client can obtain all elements added to the receiver. In other words, tells whether the
     * receiver internally preserves all added elements. If the receiver is rebinnable, the elements can be obtained via
     * {@code elements()} methods.
     *
     * @return {@code false}.
     */
    public synchronized boolean isRebinnable() {
        return false;
    }

    /**
     * @return the maximum.
     */
    public synchronized double max() {
        return this.max;
    }

    /**
     * @return the minimum.
     */
    public synchronized double min() {
        return this.min;
    }

    /**
     * Returns the number of elements contained in the receiver.
     *
     * @return the number of elements contained in the receiver.
     */
    public synchronized int size() {
        return this.size;
    }

    /**
     * @return the sum of all elements, which is {@code Sum(x[i])}.
     */
    public synchronized double sum() {
        return this.sum;
    }

    /**
     * @return the sum of squares, which is {@code Sum(x[i] * x[i])}.
     */
    public synchronized double sumOfSquares() {
        return this.sum_xx;
    }

    @Override
    public StaticBin1D clone() {
        try {
            return (StaticBin1D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
