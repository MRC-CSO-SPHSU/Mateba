package hep.aida.bin;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract base class for all arbitrary-dimensional bins consumes {@code double} elements
 * <p>
 * This class is fully thread safe (all public methods are synchronized). Thus, you can have one or more threads adding
 * to the bin as well as one or more threads reading and viewing the statistics of the bin <i>while it is filled</i>.
 *
 * @see <a href="package-summary.html">package summary</a>
 * @see <a href="package-tree.html">tree view</a>
 */
@SuppressWarnings("unused")
public abstract class AbstractBin implements Serializable {

    @Serial
    private static final long serialVersionUID = 7060878326431681969L;

    /**
     * Makes this class non instantiable, but still lets others inherit from it.
     */
    protected AbstractBin() {
    }

    /**
     * Returns {@code center(0)}.
     */
    public final double center() {
        return center(0);
    }

    /**
     * Returns a custom definable "center" measure; override this method if necessary. Returns the absolute or relative
     * center of this bin. For example, the center of gravity.
     * <p>
     * The <i>real</i> absolute center can be obtained as follows:
     * {@code partition(i).min(j) * bin(j).offset() + bin(j).center(i)}, where {@code i} is the dimension. and {@code j}
     * is the index of this bin.
     *
     * @param dimension the dimension to be considered (zero based).
     * @return always returns 0.5.
     */
    public synchronized double center(int dimension) {
        return 0.5;
    }

    /**
     * Removes all elements from the receiver. The receiver will be empty after this call returns.
     */
    public abstract void clear();

    /**
     * Compares {@code this} to another object.
     *
     * @return true when they are equal, false otherwise.
     * @implNote This default implementation returns true if the other object is a bin and has the same size, value,
     * error and center.
     */

    public boolean equals(final Object otherObj) {
        if (!(otherObj instanceof AbstractBin other)) return false;
        return size() == other.size() && value() == other.value() && error() == other.error()
            && center() == other.center();
    }

    /**
     * @return {@code error(0)}.
     * @see #error(int)
     */
    public final double error() {
        return error(0);
    }

    /**
     * Returns a custom definable error measure; override this method if necessary.
     *
     * @param dimension the dimension to be considered.
     * @return a custom definable error measure.
     * @implNote This default implementation always returns {@code 0}.
     */
    public synchronized double error(int dimension) {
        return 0;
    }

    /**
     * Returns whether a client can obtain all elements added to the receiver. In other words, tells whether the
     * receiver internally preserves all added elements. If the receiver is rebinnable, the elements can be obtained via
     * {@code elements()} methods.
     */
    public abstract boolean isRebinnable();

    /**
     * @return {@code offset(0)}.
     * @see #offset(int)
     */
    public final double offset() {
        return offset(0);
    }

    /**
     * Returns the relative or absolute position for the center of the bin; override this method if necessary.
     * Returns 1.0 if a relative center is stored in the bin. Returns 0.0 if an absolute center is stored in the bin.
     *
     * @param dimension the index of the considered dimension (zero based);
     * @return the relative or absolute position for the center of the bin.
     * @implNote This default implementation always returns 1.0 (relative).
     */
    public double offset(int dimension) {
        return 1.0;
    }

    /**
     * Returns the number of elements contained.
     *
     * @return the number of elements contained.
     */
    public abstract int size();

    /**
     * Returns a String representation of the receiver.
     *
     * @return a string containing the class name.
     */
    public synchronized String toString() {
        return getClass().getName() + "\n-------------\n";
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
     * @return {@code value(0)}.
     * @see #value(int)
     */
    public final double value() {
        return value(0);
    }

    /**
     * Returns a custom definable "value" measure; override this method if necessary.
     *
     * @param dimension the dimension to be considered.
     * @return a custom measure.
     * @implNote This default implementation always returns 0.0.
     */
    public double value(int dimension) {
        return 0;
    }
}
