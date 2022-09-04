/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.list;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for resizable lists holding objects or primitive data types such as {@code int}, {@code long},
 * etc.
 *
 * @implNote This implementation is not synchronized.
 * @see <a href="package-summary.html">package summary</a>
 * @see <a href="package-tree.html">tree view</a>
 */
@SuppressWarnings("unused")
public abstract class AbstractList implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = -475297140870140110L;
    /**
     * The size of the list. This is a READ_ONLY variable for all methods but {@code setSizeRaw(int newSize)} !!! If you
     * violate this principle in subclasses, you should exactly know what you are doing.
     */
    protected int size;

    /**
     * Checks if the given index is in range.
     */
    protected static void checkRange(final int index, final int theSize) {
        if (index >= theSize || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + theSize);
    }

    /**
     * Checks if the given range is within the contained array's bounds.
     *
     * @throws IndexOutOfBoundsException if {@code to != from - 1 && from < 0 || from > to || to >= size()}.
     */
    protected static void checkRangeFromTo(final int from, final int to, final int theSize) {
        // from < 0 throw negative index
        // to < from throw unordered set
        //
        if (to >= theSize)
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);

        if (to != from - 1 && from < 0 || from > to || to >= theSize)
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);
    }
/*    protected static void checkRangeFromTo(final int from, final int to, final int theSize) {
        if (to != from - 1 && from < 0 || from > to || to >= theSize)
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);
    }*/

    /**
     * Checks is {@code index} is within the conventional limits defined by the pair {@code [0, size]}.
     *
     * @param index The index value.
     * @param size  The array size.
     * @throws IndexOutOfBoundsException when the index value is negative or above the actual size.
     */
    public static void validateSize(final int index, final int size) { // fixme compare to checkRange and merge if needed
        if (index > size || index < 0) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    /**
     * Appends all of the elements of the specified Collection to the receiver.
     */
    public void addAllOf(Collection<?> collection) {
        this.beforeInsertAllOf(size(), collection);
    }

    /**
     * Inserts all elements of the specified collection before the specified position into the receiver. Shifts the
     * element currently at that position (if any) and any subsequent elements to the right (increases their indicies).
     *
     * @param index      Index before which to insert first element from the specified collection.
     * @param collection The collection to be inserted.
     */
    public void beforeInsertAllOf(int index, Collection<?> collection) {
        this.beforeInsertDummies(index, collection.size());
        this.replaceFromWith(index, collection);
    }

    /**
     * Inserts the part of the specified list between {@code from} (inclusive) and {@code to} (inclusive) before the
     * specified position into the receiver. Shifts the element currently at that position (if any) and any subsequent
     * elements to the right.
     *
     * @param index index before which to insert first element from the specified list (must be in {@code [0,size]})..
     * @param other list of which a part is to be inserted into the receiver.
     * @param from  the index of the first element to be inserted (inclusive).
     * @param to    the index of the last element to be inserted (inclusive).
     */
    public void beforeInsertAllOfFromTo(final int index, final @NotNull AbstractList other, final int from,
                                        final int to) {
        val length = to - from + 1;
        this.beforeInsertDummies(index, length);
        this.replaceFromToWithFrom(index, index + length - 1, other, from);
    }

    /**
     * Inserts {@code length} dummy elements before the specified position into the receiver. Shifts the element
     * currently at that position (if any) and any subsequent elements to the right. <b>This method must set the new
     * size to be {@code size() + length}.
     *
     * @param index  Index before which to insert dummy elements (must be in {@code [0, size]}).
     * @param length Number of dummy elements to be inserted.
     */
    protected void beforeInsertDummies(final int index, final int length) {
        validateSize(index, size);
        if (length > 0) {
            ensureCapacity(size + length);
            setSizeRaw(size + length);
            replaceFromToWithFrom(index + length, size - 1, this, index);
        }
    }

    /**
     * Replaces the part between {@code from} (inclusive) and {@code to} (inclusive) with the other list's part between
     * {@code otherFrom} and {@code otherTo}. Powerful (and tricky) method! Both parts need not be of the same size
     * (part A can both be smaller or larger than part B). Parts may overlap. Receiver and other list may (but most not)
     * be identical. If {@code from > to}, then inserts other part before {@code from}.
     *
     * @param from      the first element of the receiver (inclusive)
     * @param to        the last element of the receiver (inclusive)
     * @param other     the other list (may be identical with receiver)
     * @param otherFrom the first element of the other list (inclusive)
     * @param otherTo   the last element of the other list (inclusive)
     */
    public void replaceFromToWithFromTo(final int from, final int to, final @NotNull AbstractList other,
                                        final int otherFrom, final int otherTo) {
        if (otherFrom > otherTo)
            throw new IndexOutOfBoundsException("otherFrom: " + otherFrom + ", otherTo: " + otherTo);

        if (this == other && to - from != otherTo - otherFrom) {
            replaceFromToWithFromTo(from, to, partFromTo(otherFrom, otherTo), 0, otherTo - otherFrom);
            return;
        }

        val length = otherTo - otherFrom + 1;
        int diff = length;
        int theLast = from - 1;

        if (to >= from) {
            diff -= (to - from + 1);
            theLast = to;
        }

        if (diff > 0) beforeInsertDummies(theLast + 1, diff);
        else if (diff < 0) removeFromTo(theLast + diff, theLast - 1);

        if (length > 0) replaceFromToWithFrom(from, from + length - 1, other, otherFrom);
    }

    /**
     * Appends the part of the specified list between {@code from} (inclusive) and {@code to} (inclusive) to the
     * receiver.
     *
     * @param other the list to be added to the receiver.
     * @param from  the index of the first element to be appended (inclusive).
     * @param to    the index of the last element to be appended (inclusive).
     */
    public void addAllOfFromTo(final @NotNull AbstractList other, final int from, final int to) {
        beforeInsertAllOfFromTo(size, other, from, to);
    }

    /**
     * Returns a new list of the part of the receiver between {@code from}, inclusive, and {@code to}, inclusive.
     *
     * @param from the index of the first element (inclusive).
     * @param to   the index of the last element (inclusive).
     * @return a new list
     */
    public abstract AbstractList partFromTo(final int from, final int to);

    /**
     * Removes all elements from the receiver. The receiver will be empty after this call returns.
     */
    public void clear() {
        removeFromTo(0, size() - 1);
    }

    /**
     * Limits default to the whole array.
     *
     * @see #mergeSortFromTo(int, int)
     */
    public final void mergeSort() {
        mergeSortFromTo(0, size() - 1);
    }

    /**
     * Sorts the receiver into ascending order. This sort is guaranteed to be <i>stable</i>: equal elements will not be
     * reordered as a result of the sort.
     * <p>
     * The sorting algorithm is a modified mergesort (in which the merge is omitted if the highest element in the low
     * sublist is less than the lowest element in the high sublist). This algorithm offers guaranteed {@code N * log(N)}
     * performance, and can approach linear performance on nearly sorted lists.
     * <p>
     * <b>You should never call this method unless you are sure that this particular sorting algorithm is the right one
     * for your data set.</b> It is generally better to call {@link #sort()} or {@code sortFromTo(...)} instead, because
     * those methods automatically choose the best sorting algorithm.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     */
    public abstract void mergeSortFromTo(final int from, final int to);

    /**
     * Limits default to the whole array.
     *
     * @see #quickSortFromTo(int, int)
     */
    public final void quickSort() {
        quickSortFromTo(0, size() - 1);
    }

    /**
     * Sorts the specified range of the receiver into ascending order. The sorting algorithm is a tuned quicksort,
     * adapted from Jon L. Bentley and M. Douglas McIlroy's "Engineering a Sort Function", Software-Practice and
     * Experience, Vol. 23(11) P. 1249-1265 (November 1993). This algorithm offers n*log(n) performance on many data
     * sets that cause other quicksorts to degrade to quadratic performance.
     * <p>
     * <b>You should never call this method unless you are sure that this particular sorting algorithm is the right one
     * for your data set.</b> It is generally better to call {@link #sort()} or {@code sortFromTo(...)} instead, because
     * those methods automatically choose the best sorting algorithm.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     */
    public abstract void quickSortFromTo(final int from, final int to);

    /**
     * Removes the element at the specified position from the receiver. Shifts any subsequent elements to the left.
     *
     * @param index the index of the element to removed.
     */
    public void remove(final int index) {
        removeFromTo(index, index);
    }

    /**
     * Removes from the receiver all elements whose index is between {@code from}, inclusive and {@code to}, inclusive.
     * Shifts any succeeding elements to the left (reduces their index). This call shortens the list by
     * {@code (to - from + 1)} elements.
     *
     * @param from index of first element to be removed.
     * @param to   index of last element to be removed.
     */
    public void removeFromTo(final int from, final int to) {
        if (size == 0) return; // fixme a temp fix to deal with arrays with zero elements
        checkRangeFromTo(from, to, size);
        val numMoved = size - to - 1;
        if (numMoved > 0) replaceFromToWithFrom(from, from - 1 + numMoved, this, to + 1);
        val width = to - from + 1;
        if (width > 0) setSizeRaw(size - width);
    }

    /**
     * Replaces the part of the receiver starting at {@code from} (inclusive) with all the elements of the specified
     * collection. Does not alter the size of the receiver. Replaces exactly
     * {@code Math.max(0,Math.min(size()-from, other.size()))} elements.
     *
     * @param from  the index at which to copy the first element from the
     *              specified collection.
     * @param other Collection to replace part of the receiver
     */
    public abstract void replaceFromWith(final int from, final @NotNull Collection<?> other);

    /**
     * Reverses the elements of the receiver. Last becomes first, second last becomes second first, and so on.
     */
    public abstract void reverse();

    /**
     * Sets the size of the receiver. If the new size is greater than the current size, new null or zero items are added
     * to the end of the receiver. If the new size is less than the current size, all components at index newSize and
     * greater are discarded. This method does not release any superfluous internal memory. Use method
     * {@link #trimToSize} to release superfluous internal memory.
     *
     * @param newSize the new size of the receiver.
     * @throws IndexOutOfBoundsException if {@code newSize < 0}.
     */
    public void setSize(final int newSize) {
        if (newSize < 0) throw new IndexOutOfBoundsException("newSize:" + newSize);

        int currentSize = size();
        if (newSize != currentSize)
            if (newSize > currentSize) beforeInsertDummies(currentSize, newSize - currentSize);
            else removeFromTo(newSize, currentSize - 1);
    }

    /**
     * Randomly permutes the whole receiver. After invocation, all elements will be at random positions.
     *
     * @see #shuffleFromTo(int, int)
     */
    public final void shuffle() {
        shuffleFromTo(0, size() - 1);
    }

    /**
     * Randomly permutes the receiver between {@code from} (inclusive) and {@code to} (inclusive).
     *
     * @param from the start position (inclusive)
     * @param to   the end position (inclusive)
     */
    public abstract void shuffleFromTo(final int from, final int to);

    /**
     * Sorts the whole array.
     *
     * @see #sortFromTo(int, int)
     */
    public final void sort() {
        sortFromTo(0, size() - 1);
    }

    /**
     * Sorts the specified range of the receiver into ascending order.
     * <p>
     * The sorting algorithm is dynamically chosen according to the characteristics of the data set. This default
     * implementation simply calls quickSort. Override this method if you can determine which sort is most appropriate
     * for the given data set.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     */
    public void sortFromTo(final int from, final int to) {
        quickSortFromTo(from, to);
    }

    /**
     * Trims the capacity of the receiver to be the receiver's current size. Releases any superfluous internal memory.
     * An application can use this operation to minimize the storage of the receiver.
     * <p>
     *
     * @implNote This default implementation does nothing. Override this method in space efficient implementations.
     */
    public abstract void trimToSize();

    /**
     * Ensures that the receiver can hold at least the specified number of
     * elements without needing to allocate new internal memory. If necessary,
     * allocates new internal memory and increases the capacity of the receiver.
     *
     * @param minCapacity the desired minimum capacity.
     */
    public abstract void ensureCapacity(int minCapacity);

    /**
     * Sets the size of the receiver without modifying it otherwise. This method should not release or allocate new
     * memory but simply set some instance variable like {@code size}.
     * <p>
     * If your subclass overrides and delegates size changing methods to some other object, you must make sure that
     * those overriding methods not only update the size of the delegate but also of this class. For example:
     * public DatabaseList extends AbstractByteList { ... public void
     * removeFromTo(int from,int to) { myDatabase.removeFromTo(from,to);
     * this.setSizeRaw(size-(to-from+1)); } }
     */
    public void setSizeRaw(int newSize) {
        size = newSize;
    }

    /**
     * Replaces a number of elements in the receiver with the same number of elements of another list. Replaces elements
     * in the receiver, between {@code from} (inclusive) and {@code to} (inclusive), with elements of {@code other},
     * starting from {@code otherFrom} (inclusive).
     *
     * @param from      the position of the first element to be replaced in the receiver
     * @param to        the position of the last element to be replaced in the receiver
     * @param other     list holding elements to be copied into the receiver.
     * @param otherFrom position of first element within other list to be copied.
     */
    public abstract void replaceFromToWithFrom(final int from, final int to, final @NotNull AbstractList other,
                                               final int otherFrom);

    /**
     * Tests if the receiver has no elements.
     *
     * @return {@code true} if the receiver has no elements; {@code false} otherwise.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of elements contained in the receiver.
     *
     * @return the number of elements contained in the receiver.
     */
    public int size() {
        return size;
    }

    /**
     * Returns a {@link ArrayList} containing all the elements in the receiver.
     */
    public abstract ArrayList<?> toList();

    /**
     * Returns a string representation of the receiver, containing the String representation of each element.
     */
    public String toString() {
        return toList().toString();
    }

    /**
     * An implementation of the {@link Cloneable} interface.
     *
     * @return A collection of a certain type.
     */
    @Override
    public AbstractList clone() {
        try {
            return (AbstractList) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Compares the specified Object with the receiver. Returns true if and only if the specified Object is also an
     * ArrayList of the same type, both Lists have the same size, and all corresponding pairs of elements in the two
     * Lists are identical. In other words, two Lists are defined to be equal if they contain the same elements in the
     * same order.
     *
     * @param otherObj the Object to be compared for equality with the receiver.
     * @return true if the specified Object is equal to the receiver.
     */
    public abstract boolean equals(final @Nullable Object otherObj);
}
