/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.list.tint;

import cern.mateba.Arrays;
import cern.mateba.Sorting;
import cern.mateba.buffer.tint.IntBuffer1DConsumer;
import cern.mateba.function.tint.IntComparator;
import cern.mateba.function.tint.IntProcedure;
import cern.mateba.list.AbstractList;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Resizable list holding {@code int} elements; implemented with arrays.
 */
@SuppressWarnings("unused")
public class IntArrayList extends AbstractList implements IntBuffer1DConsumer {

    @Serial
    private static final long serialVersionUID = -5770769804456374629L;
    /**
     * The array buffer into which the elements of the list are stored. The capacity of the list is the length of this
     * array buffer.
     */
    protected int[] elements;

    /**
     * Constructs an empty list of size 10.
     */
    public IntArrayList() {
        this(10);
    }

    /**
     * Constructs a list containing the specified elements. The initial size and
     * capacity of the list is the length of the array.
     * <p>
     * <b>WARNING:</b> For efficiency reasons and to keep memory usage low,
     * <b>the array is not copied</b>. So if subsequently you modify the
     * specified array directly via the [] operator, be sure you know what
     * you're doing.
     *
     * @param elements the array to be backed by the constructed list
     */
    public IntArrayList(final int @NotNull [] elements) {
        elements(elements);
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the number of elements the receiver can hold without
     *                        auto-expanding itself by allocating new internal memory.
     */
    public IntArrayList(int initialCapacity) {
        this(new int[initialCapacity]);
        setSizeRaw(0);
    }

    /**
     * Searches the receiver for the specified value using the binary search algorithm. The receiver
     * <strong>must</strong> be sorted (as by the sort method) prior to making this call. If it is not sorted,
     * the results are undefined: in particular, the call may enter an infinite loop. If the receiver contains multiple
     * elements equal to the specified object, there is no guarantee which instance will be found.
     *
     * @param key  the value to be searched for.
     * @param from the leftmost search position, inclusive.
     * @param to   the rightmost search position, inclusive.
     * @return index of the search key, if it is contained in the receiver; otherwise, {@code -insertion-point - 1}. The
     * <i>insertion point</i> is defined as the point at which the value would be inserted into the receiver: the index
     * of the first element greater than the key, or {@code receiver.size()}, if all elements in the receiver are less
     * than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the key is
     * found.
     */
    public int binarySearchFromTo(final int key, final int from, final int to) {
        return Sorting.binarySearchFromTo(this.elements, key, from, to);
    }

    /**
     * Sorts the specified range of the receiver into ascending numerical order.
     * <p>
     * The sorting algorithm is a count sort. This algorithm offers guaranteed <dt>Performance: O(Max(n,max-min+1)).
     * <dt>Space requirements: int[max-min+1] buffer.
     * <p>
     * This algorithm is only applicable if max-min+1 is not large! But if applicable, it usually outperforms quicksort
     * by a factor of 3-4.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     * @param min  the smallest element contained in the range.
     * @param max  the largest element contained in the range.
     */
    protected void countSortFromTo(final int from, final int to, final int min, final int max) {
        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);

        val width = (max - min + 1);

        val counts = new int[width];
        val theElements = elements;
        for (int i = from; i <= to; ) counts[(theElements[i++] - min)]++;

        int fromIndex = from;
        int val = min;
        int c;
        int toIndex;
        for (int i = 0; i < width; i++, val++) {
            c = counts[i];
            if (c > 0) if (c == 1) theElements[fromIndex++] = val;
            else {
                toIndex = fromIndex + c - 1;
                fillFromToWith(fromIndex, toIndex, val);
                fromIndex = toIndex + 1;
            }
        }
    }

    /**
     * Returns the elements currently stored, including invalid elements between size and capacity, if any.
     * <p>
     * <b>WARNING:</b> For efficiency reasons and to keep memory usage low, <b>the array is not copied</b>. So if
     * subsequently you modify the returned array directly via the [] operator, be sure you know what you're doing.
     *
     * @return the elements currently stored.
     */
    public int @NotNull [] elements() {
        return elements;
    }

    /**
     * Sets the receiver's elements to be the specified array (not a copy of it).
     * <p>
     * The size and capacity of the list is the length of the array. <b>WARNING:</b> For efficiency reasons and to keep
     * memory usage low, <b>the array is not copied</b>. So if subsequently you modify the specified array directly via
     * the [] operator, be sure you know what you're doing.
     *
     * @param elements the new elements to be stored.
     * @return the receiver itself.
     */
    public @NotNull IntArrayList elements(final int @NotNull [] elements) {
        this.elements = elements;
        this.size = elements.length;
        return this;
    }

    /**
     * Ensures that the receiver can hold at least the specified number of elements without needing to allocate new
     * internal memory. If necessary, allocates new internal memory and increases the capacity of the receiver.
     *
     * @param minCapacity the desired minimum capacity.
     */
    public void ensureCapacity(final int minCapacity) {
        elements = Arrays.ensureCapacity(elements, minCapacity);
    }

    /**
     * Returns the element at the specified position in the receiver.
     *
     * @param index index of element to return.
     */
    public int get(final int index) {
        checkRange(index, size);
        return elements[index];
    }

    /**
     * Returns the element at the specified position in the receiver; <b>WARNING:</b> Does not check preconditions.
     * Provided with invalid parameters this method may return invalid elements without throwing any exception! <b>You
     * should only use this method when you are absolutely sure that the index is within bounds.</b> Precondition
     * (unchecked): {@code index >= 0 && index < size()}.
     *
     * @param index index of element to return.
     */
    public int getQuick(final int index) {
        return elements[index];
    }

    /**
     * Returns a new list of the part of the receiver between {@code from}, inclusive, and {@code to}, inclusive.
     *
     * @param from the index of the first element (inclusive).
     * @param to   the index of the last element (inclusive).
     * @return a new list
     */
    public @NotNull IntArrayList partFromTo(final int from, final int to) {
        if (size == 0) return new IntArrayList(0);

        checkRangeFromTo(from, to, size);

        val part = new int[to - from + 1];
        System.arraycopy(elements, from, part, 0, to - from + 1);
        return new IntArrayList(part);
    }

    /**
     * Replaces the element at the specified position in the receiver with the specified element.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(final int index, final int element) {
        checkRange(index, size);
        elements[index] = element;
    }

    /**
     * Replaces the element at the specified position in the receiver with the specified element; <b>WARNING:</b> Does
     * not check preconditions. Provided with invalid parameters this method may access invalid indexes without throwing
     * any exception! <b>You should only use this method when you are absolutely sure that the index is within bounds.
     * </b> Precondition (unchecked): {@code index >= 0 && index < size()}.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void setQuick(final int index, final int element) {
        elements[index] = element;
    }

    public void setSizeRaw(final int size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortFromTo(final int from, final int to) {
       val widthThreshold = 10000;

        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);

        int min = elements[from];
        int max = elements[from];

        val theElements = elements;
        int elem;
        for (int i = from + 1; i <= to; ) {
             elem = theElements[i++];
            if (elem > max) max = elem;
            else if (elem < min) min = elem;
        }

        val N = (double) to - (double) from + 1.0;
        val quickSortEstimate = N * Math.log(N) / 0.6931471805599453;

        val width = (double) max - (double) min + 1.0;
        val countSortEstimate = Math.max(width, N);

        if (width < widthThreshold && countSortEstimate < quickSortEstimate) countSortFromTo(from, to, min, max);
        else quickSortFromTo(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trimToSize() {
        elements = Arrays.trimToCapacity(elements, size());
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list.
     */
    public void add(final int element) {
        beforeInsert(size, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAllOf(final @NotNull IntArrayList other) {
        addAllOfFromTo(other, 0, other.size() - 1);
    }

    /**
     * Inserts the specified element before the specified position into the receiver. Shifts the element currently at
     * that position (if any) and any subsequent elements to the right.
     *
     * @param index   index before which the specified element is to be inserted (must be in [0,size]).
     * @param element element to be inserted.
     */
    public void beforeInsert(final int index, final int element) {
        beforeInsertDummies(index, 1);
        set(index, element);
    }

    /**
     * Searches the receiver for the specified value using the binary search algorithm. The receiver
     * <strong>must</strong> be sorted (as by the sort method) prior to making this call. If it is not sorted, the
     * results are undefined: in particular, the call may enter an infinite loop. If the receiver contains multiple
     * elements equal to the specified object, there is no guarantee which instance will be found.
     *
     * @param key the value to be searched for.
     * @return index of the search key, if it is contained in the receiver; otherwise, {@code -insertion_point - 1}. The
     * <i>insertion point</i> is defined as the point at which the value would be inserted into the receiver: the index
     * of the first element greater than the key, or {@code receiver.size()}, if all elements in the receiver are less
     * than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the key is
     * found.
     */
    public int binarySearch(final int key) {
        return this.binarySearchFromTo(key, 0, size - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull IntArrayList clone() {
        val clone = (IntArrayList) super.clone();
        clone.elements = this.elements.clone();
        return clone;
    }

    /**
     * Returns true if the receiver contains the specified element.
     *
     * @param elem element whose presence in the receiver is to be tested.
     */
    public boolean contains(final int elem) {
        return indexOfFromTo(elem, 0, size - 1) >= 0;
    }

    /**
     * Deletes the first element from the receiver that is identical to the specified element. Does nothing, if no such
     * matching element is contained.
     *
     * @param element the element to be deleted.
     */
    public void delete(final int element) {
        val index = indexOfFromTo(element, 0, size - 1);
        if (index >= 0) remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final @Nullable Object otherObj) {
        if (otherObj == null) return false;
        if (!(otherObj instanceof IntArrayList other)) return false;
        if (this == otherObj) return true;
        if (size() != other.size()) return false;
        for (int i = size(); --i >= 0; ) if (getQuick(i) != other.getQuick(i)) return false;
        return true;
    }

    /**
     * Sets the specified range of elements in the specified array to the specified value.
     *
     * @param from the index of the first element (inclusive) to be filled with the specified value.
     * @param to   the index of the last element (inclusive) to be filled with the specified value.
     * @param val  the value to be stored in the specified elements of the receiver.
     */
    public void fillFromToWith(final int from, final int to, final int val) {
        checkRangeFromTo(from, to, this.size);
        IntStream.rangeClosed(from, to).forEach(i -> setQuick(i, val));
    }

    /**
     * Applies a procedure to each element of the receiver, if any. Starts at index 0, moving rightwards.
     *
     * @param procedure the procedure to be applied. Stops iteration if the procedure returns {@code false}, otherwise
     *                  continues.
     * @return {@code false} if the procedure stopped before all elements where iterated over, {@code true} otherwise.
     */
    public boolean forEach(final @NotNull IntProcedure procedure) {
        return IntStream.range(0, size).allMatch(i -> procedure.apply(get(i)));
    }


    /**
     * Returns the index of the first occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element.
     *
     * @param element the element to be searched for.
     * @return the index of the first occurrence of the element in the receiver;
     * returns {@code -1} if the element is not found.
     */
    public int indexOf(final int element) {
        return indexOfFromTo(element, 0, size - 1);
    }

    /**
     * Returns the index of the first occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element. Searches between {@code from}, inclusive and {@code to}, inclusive. Tests for identity.
     *
     * @param element element to search for.
     * @param from    the leftmost search position, inclusive.
     * @param to      the rightmost search position, inclusive.
     * @return the index of the first occurrence of the element in the receiver; returns {@code -1} if the element is
     * not found.
     */
    public int indexOfFromTo(final int element, final int from, final int to) {
        checkRangeFromTo(from, to, size);
        return IntStream.rangeClosed(from, to).filter(i -> element == getQuick(i)).findFirst().orElse(-1);
    }

    /**
     * Returns the index of the last occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element.
     *
     * @param element the element to be searched for.
     * @return the index of the last occurrence of the element in the receiver; returns {@code -1} if the element is not
     * found.
     */
    public int lastIndexOf(final int element) {
        return lastIndexOfFromTo(element, 0, size - 1);
    }

    /**
     * Returns the index of the last occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element. Searches beginning at {@code to}, inclusive until {@code from}, inclusive. Tests for
     * identity.
     *
     * @param element element to search for.
     * @param from    the leftmost search position, inclusive.
     * @param to      the rightmost search position, inclusive.
     * @return the index of the last occurrence of the element in the receiver; returns {@code -1} if the element is not
     * found.
     */
    public int lastIndexOfFromTo(final int element, final int from, final int to) {
        checkRangeFromTo(from, to, size());

        for (int i = to; i >= from; i--) if (element == getQuick(i)) return i;
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeSortFromTo(final int from, final int to) {
        val mySize = size();
        checkRangeFromTo(from, to, mySize);

        val myElements = elements();
        Sorting.mergeSort(myElements, from, to + 1);
        elements(myElements);
        setSizeRaw(mySize);
    }

    /**
     * Sorts the receiver according to the order induced by the specified comparator. All elements in the range must be
     * <i>mutually comparable</i> by the specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and {@code e2} in the range).
     * <p>
     * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result of the sort.
     * <p>
     * <p>
     * The sorting algorithm is a modified mergesort (in which the merge is omitted if the highest element in the low
     * sublist is less than the lowest element in the high sublist). This algorithm offers guaranteed n*log(n)
     * performance, and can approach linear performance on nearly sorted lists.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     * @param c    the comparator to determine the order of the receiver.
     */
    public void mergeSortFromTo(final int from, final int to, final @NotNull IntComparator c) {
        val mySize = size();
        checkRangeFromTo(from, to, mySize);

        val myElements = elements();
        cern.mateba.Sorting.mergeSort(myElements, from, to + 1, c);
        elements(myElements);
        setSizeRaw(mySize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void quickSortFromTo(final int from, final int to) {
        val mySize = size();
        checkRangeFromTo(from, to, mySize);

        val myElements = elements();
        java.util.Arrays.sort(myElements, from, to + 1);
        elements(myElements);
        setSizeRaw(mySize);
    }

    /**
     * Sorts the receiver according to the order induced by the specified comparator. All elements in the range must be
     * <i>mutually comparable</i> by the specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and {@code e2} in the range).
     * <p>
     * <p>
     * The sorting algorithm is a tuned quicksort, adapted from Jon L. Bentley and M. Douglas McIlroy's "Engineering a
     * Sort Function", Software-Practice and Experience, Vol. 23(11) P. 1249-1265 (November 1993). This algorithm offers
     * n*log(n) performance on many data sets that cause other quicksorts to degrade to quadratic performance.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     * @param c    the comparator to determine the order of the receiver.
     */
    public void quickSortFromTo(final int from, final int to, final @NotNull IntComparator c) {
        val mySize = size();
        checkRangeFromTo(from, to, mySize);

        val myElements = elements();
        cern.mateba.Sorting.quickSort(myElements, from, to + 1, c);
        elements(myElements);
        setSizeRaw(mySize);
    }

    /**
     * Removes from the receiver all elements that are contained in the specified list. Tests for identity.
     *
     * @param other the other list.
     * @return {@code true} if the receiver changed as a result of the call.
     */
    public boolean removeAll(final @NotNull IntArrayList other) {
        if (other.size() == 0) return false;
        val limit = other.size() - 1;
        int j = 0;

        for (int i = 0; i < size; i++) if (other.indexOfFromTo(getQuick(i), 0, limit) < 0) setQuick(j++, getQuick(i));
        setSize(j);

        return j != size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceFromToWithFrom(int from, int to, final @NotNull AbstractList other, int otherFrom) {
        if (other instanceof IntArrayList s) {
            int length = to - from + 1;
            if (length > 0) {
                checkRangeFromTo(from, to, size());
                checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());

                if (from <= otherFrom) {
                    while (--length >= 0) {
                        setQuick(from++, s.getQuick(otherFrom++));
                    }
                } else {
                    int otherTo = otherFrom + length - 1;
                    while (--length >= 0) {
                        setQuick(to--, s.getQuick(otherTo--));
                    }
                }
            }
        } else throw new IllegalArgumentException("This type is not supported;");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceFromWith(final int from, final @NotNull Collection<?> other) {
        checkRange(from, size());
        val e = other.iterator();
        int index = from;
        int limit = Math.min(size() - from, other.size());
        for (int i = 0; i < limit; i++)
            set(index++, (Integer) e.next());
    }

    /**
     * Retains (keeps) only the elements in the receiver that are contained in the specified other list. In other words,
     * removes from the receiver all of its elements that are not contained in the specified other list.
     *
     * @param other the other list to test against.
     * @return {@code true} if the receiver changed as a result of the call.
     */
    public boolean retainAll(final @NotNull IntArrayList other) {
        if (other.size() == 0) {
            if (size == 0) return false;
            setSize(0);
            return true;
        }

        int limit = other.size() - 1;
        int j = 0;
        for (int i = 0; i < size; i++) if (other.indexOfFromTo(getQuick(i), 0, limit) >= 0) setQuick(j++, getQuick(i));
        setSize(j);

        return j != size;
    }

    /**
     * Reverses the elements of the receiver. Last becomes first, second last becomes second first, and so on.
     */
    public void reverse() {
        int tmp;
        val limit = size() / 2;
        int j = size() - 1;

        for (int i = 0; i < limit; ) {
            tmp = getQuick(i);
            setQuick(i++, getQuick(j));
            setQuick(j--, tmp);
        }
    }

    /**
     * Randomly permutes the part of the receiver between {@code from} (inclusive) and {@code to} (inclusive).
     *
     * @param from the index of the first element (inclusive) to be permuted.
     * @param to   the index of the last element (inclusive) to be permuted.
     */
    public void shuffleFromTo(final int from, final int to) {
        checkRangeFromTo(from, to, size());

        var gen = new Uniform(new MersenneTwister(new java.util.Date()));
        int random;
        int tmpElement;
        for (int i = from; i < to; i++) {
            random = gen.nextIntFromTo(i, to);
            tmpElement = getQuick(random);
            setQuick(random, getQuick(i));
            setQuick(i, tmpElement);
        }
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
     * Returns a list which is a concatenation of {@code times} times the receiver.
     *
     * @param times the number of times the receiver shall be copied.
     */
    public @NotNull IntArrayList times(final int times) {
        val newList = new IntArrayList(times * size());
        IntStream.range(0, times).forEach(i -> newList.addAllOfFromTo(this, 0, size() - 1));
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ArrayList<Integer> toList() {
        val mySize = size();
        val list = new ArrayList<Integer>(mySize);
        for (int i = 0; i < mySize; i++) list.add(get(i));
        return list;
    }
}
