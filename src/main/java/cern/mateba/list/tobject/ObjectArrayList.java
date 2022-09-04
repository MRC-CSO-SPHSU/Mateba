/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.list.tobject;

import cern.mateba.Arrays;
import cern.mateba.Sorting;
import cern.mateba.function.tobject.ObjectProcedure;
import cern.mateba.list.AbstractList;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

/**
 * Resizable list holding {@link Object} elements; implemented with arrays.
 */
@SuppressWarnings("unused")
public class ObjectArrayList extends AbstractList {

    @Serial
    private static final long serialVersionUID = 3107871349701897316L;

    /**
     * The array buffer into which the elements of the list are stored. The
     * capacity of the list is the length of this array buffer.
     */
    protected Object[] elements;

    /**
     * The size of the list.
     */
    protected int size;

    /**
     * Constructs an empty list of size 10.
     */
    public ObjectArrayList() {
        this(10);
    }

    /**
     * Constructs a list containing the specified elements. The initial size and capacity of the list is the length of
     * the array.
     * <p>
     * <b>WARNING:</b> For efficiency reasons and to keep memory usage low, <b>the array is not copied</b>. So if
     * subsequently you modify the specified array directly via the [] operator, be sure you know what you're doing.
     *
     * @param elements the array to be backed by the constructed list
     */
    public ObjectArrayList(final @NotNull Object @NotNull [] elements) {
        elements(elements);
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the number of elements the receiver can hold without auto-expanding itself by allocating
     *                        new internal memory.
     */
    public ObjectArrayList(final int initialCapacity) {
        this(new Object[initialCapacity]);
        size = 0;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list.
     */
    public void add(final @NotNull Object element) {
        if (size == elements.length) ensureCapacity(size + 1);
        elements[size++] = element;
    }

    /**
     * Inserts the specified element before the specified position into the receiver. Shifts the element currently at
     * that position (if any) and any subsequent elements to the right.
     *
     * @param index   index before which the specified element is to be inserted (must be in [0,size]).
     * @param element element to be inserted.
     */
    public void beforeInsert(final int index, final @NotNull Object element) {
        AbstractList.validateSize(index, size);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    /**
     * Searches the receiver for the specified value using the binary search algorithm. The receiver must be sorted into
     * ascending order according to the <i>natural ordering</i> of its elements (as by the sort method) prior to making
     * this call. If it is not sorted, the results are undefined: in particular, the call may enter an infinite loop. If
     * the receiver contains multiple elements equal to the specified object, there is no guarantee which instance will
     * be found.
     *
     * @param key the value to be searched for.
     * @return index of the search key, if it is contained in the receiver; otherwise, {@code -insertion_point - 1}.
     * The <i>insertion point</i> is defined as the point at which the value would be inserted into the receiver: the
     * index of the first element greater than the key, or {@code receiver.size()}, if all elements in the receiver are
     * less than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the key
     * is found.
     */
    public int binarySearch(final @NotNull Object key) {
        return this.binarySearchFromTo(key, 0, size - 1);
    }

    /**
     * Searches the receiver for the specified value using the binary search algorithm. The receiver must be sorted into
     * ascending order according to the <i>natural ordering</i> of its elements (as by the sort method) prior to making
     * this call. If it is not sorted, the results are undefined: in particular, the call may enter an infinite loop.
     * If the receiver contains multiple elements equal to the specified object, there is no guarantee which instance
     * will be found.
     *
     * @param key  the value to be searched for.
     * @param from the leftmost search position, inclusive.
     * @param to   the rightmost search position, inclusive.
     * @return index of the search key, if it is contained in the receiver; otherwise, {@code -insertion_point - 1}. The
     * <i>insertion point</i> is defined as the point at which the value would be inserted into the receiver: the index
     * of the first element greater than the key, or {@code receiver.size()}, if all elements in the receiver are less
     * than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the key is
     * found.
     */
    public int binarySearchFromTo(final @NotNull Object key, final int from, final int to) {
        int low = from;
        int high = to;

        int mid;
        Object midVal;
        int cmp;
        while (low <= high) {
            mid = (low + high) / 2;
            midVal = elements[mid];

            cmp = ((Comparable) midVal).compareTo(key);

            if (cmp < 0) low = mid + 1;
            else if (cmp > 0) high = mid - 1;
            else return mid;
        }
        return -(low + 1);
    }

    /**
     * Searches the receiver for the specified value using the binary search algorithm. The receiver must be sorted into
     * ascending order according to the specified comparator. All elements in the range must be <i>mutually
     * comparable</i> by the specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and {@code e2} in the range).
     * <p>
     * If the receiver is not sorted, the results are undefined: in particular, the call may enter an infinite loop.
     * If the receiver contains multiple elements equal to the specified object, there is no guarantee which instance
     * will be found.
     *
     * @param key        the value to be searched for.
     * @param from       the leftmost search position, inclusive.
     * @param to         the rightmost search position, inclusive.
     * @param comparator the comparator by which the receiver is sorted.
     * @return index of the search key, if it is contained in the receiver; otherwise, {@code -insertion point - 1}. The
     * <i>insertion point</i> is defined as the point at which the value would be inserted into the receiver: the index
     * of the first element greater than the key, or {@code receiver.size()}, if all elements in the receiver are less
     * than the specified key. Note that this guarantees that the return value will be >= 0 if and only if the key is
     * found.
     */
    public int binarySearchFromTo(Object key, int from, int to, Comparator<Object> comparator) {
        return Sorting.binarySearchFromTo(this.elements, key, from, to, comparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ObjectArrayList clone() {
        val clone = (ObjectArrayList) super.clone();
        clone.elements = this.elements.clone();
        return clone;
    }


    /**
     * Returns true if the receiver contains the specified element. Tests for equality or identity as specified by
     * testForEquality.
     *
     * @param elem            element to search for.
     * @param testForEquality if true -> test for equality, otherwise for identity.
     */
    public boolean contains(final @NotNull Object elem, final boolean testForEquality) {
        return indexOfFromTo(elem, 0, size - 1, testForEquality) >= 0;
    }

    /**
     * Deletes the first element from the receiver that matches the specified element. Does nothing, if no such matching
     * element is contained.
     * <p>
     * Tests elements for equality or identity as specified by {@code testForEquality}. When testing for equality, two
     * elements {@code e1} and {@code e2} are <i>equal</i> if {@code (e1==null ? e2==null : e1.equals(e2))}.)
     *
     * @param testForEquality if true -> tests for equality, otherwise for identity.
     * @param element         the element to be deleted.
     */
    public void delete(final @NotNull Object element, final boolean testForEquality) {
        val index = indexOfFromTo(element, 0, size - 1, testForEquality);
        if (index >= 0) removeFromTo(index, index);
    }

    /**
     * Returns the elements currently stored, including invalid elements between
     * size and capacity, if any.
     * <p>
     * <b>WARNING:</b> For efficiency reasons and to keep memory usage low, <b>the array is not copied</b>. So if
     * subsequently you modify the returned array directly via the [] operator, be sure you know what you're doing.
     *
     * @return the elements currently stored.
     */
    public @NotNull Object @NotNull [] elements() {
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
    public @NotNull ObjectArrayList elements(final @NotNull Object @NotNull [] elements) {
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final @Nullable Object otherObj) {
        if (otherObj == null) return false;
        if (!(otherObj instanceof ObjectArrayList other)) return false;
        if (this == otherObj) return true;
        if (size() != other.size()) return false;
        for (int i = size(); --i >= 0; ) if (getQuick(i) != other.getQuick(i)) return false;
        return true;
    }

    /**
     * Compares the specified Object with the receiver for equality. Returns true if and only if the specified Object
     * is also an ObjectArrayList, both lists have the same size, and all corresponding pairs of elements in the two
     * lists are the same. In other words, two lists are defined to be equal if they contain the same elements in the
     * same order. Tests elements for equality or identity as specified by {@code testForEquality}. When testing for
     * equality, two elements {@code e1} and {@code e2} are <i>equal</i> if {@code (e1==null ? e2==null : e1.equals(e2))}.
     *
     * @param otherObj        the Object to be compared for equality with the receiver.
     * @param testForEquality if true -> tests for equality, otherwise for identity.
     * @return true if the specified Object is equal to the receiver.
     */
    public boolean equals(final @Nullable Object otherObj, final boolean testForEquality) {
        if (otherObj == null) return false;
        if (!(otherObj instanceof ObjectArrayList other)) return false;
        if (this == otherObj) return true;

        if (elements == other.elements()) return true;// todo check this

        if (size != other.size()) return false;

        val otherElements = other.elements();
        val theElements = elements;

        if (testForEquality) {
            for (int i = size; --i >= 0; ) if (!(Objects.equals(theElements[i], otherElements[i]))) return false;
        } else for (int i = size; --i >= 0; ) if (theElements[i] != otherElements[i]) return false;
        return true;
    }

    /**
     * Sets the specified range of elements in the specified array to the specified value.
     *
     * @param from the index of the first element (inclusive) to be filled with the specified value.
     * @param to   the index of the last element (inclusive) to be filled with the specified value.
     * @param val  the value to be stored in the specified elements of the receiver.
     */
    public void fillFromToWith(final int from, final int to, final @NotNull Object val) {
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
    public boolean forEach(final @NotNull ObjectProcedure procedure) {
        return IntStream.range(0, size).allMatch(i -> procedure.apply(elements[i]));
    }

    /**
     * Returns the element at the specified position in the receiver.
     *
     * @param index index of element to return.
     */
    public @NotNull Object get(final int index) {
        checkRange(index, size);
        return elements[index];
    }

    /**
     * Returns the element at the specified position in the receiver; <b>WARNING:</b> Does not check preconditions.
     * Provided with invalid parameters this method may return invalid elements without throwing any exception! <b>You
     * should only use this method when you are absolutely sure that the index is within bounds.</b> Precondition
     * (unchecked):
     * {@code index >= 0 && index < size()}.
     *
     * @param index index of element to return.
     */
    public @NotNull Object getQuick(final int index) {
        return elements[index];
    }

    /**
     * Returns the index of the first occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element.
     * <p>
     * Tests for equality or identity as specified by testForEquality.
     *
     * @param testForEquality if {@code true} -> test for equality, otherwise for identity.
     * @return the index of the first occurrence of the element in the receiver; returns {@code -1} if the element is
     * not found.
     */
    public int indexOf(final @NotNull Object element, final boolean testForEquality) {
        return this.indexOfFromTo(element, 0, size - 1, testForEquality);
    }

    /**
     * Returns the index of the first occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element. Searches between {@code from}, inclusive and {@code to}, inclusive.
     * <p>
     * Tests for equality or identity as specified by {@code testForEquality}.
     *
     * @param element         element to search for.
     * @param from            the leftmost search position, inclusive.
     * @param to              the rightmost search position, inclusive.
     * @param testForEquality if }true} -> test for equality, otherwise for identity.
     * @return the index of the first occurrence of the element in the receiver; returns {@code -1} if the element is
     * not found.
     */
    public int indexOfFromTo(final @NotNull Object element, final int from, final int to,
                             final boolean testForEquality) {
        if (size == 0)
            return -1;
        checkRangeFromTo(from, to, size);

        val theElements = elements;
        if (testForEquality && element != null) {
            for (int i = from; i <= to; i++) {
                if (element.equals(theElements[i])) {
                    return i;
                }
            }

        } else {
            for (int i = from; i <= to; i++) {
                if (element == theElements[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Determines whether the receiver is sorted ascending, according to the <i>natural ordering</i> of its elements.
     * All elements in this range must implement the {@code Comparable} interface. Furthermore, all elements in this
     * range must be <i>mutually comparable</i> (that is, {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and {@code e2} in the array).
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     * @return {@code true} if the receiver is sorted ascending, {@code false} otherwise.
     */
    public boolean isSortedFromTo(final int from, final int to) {
        if (size == 0) return true;
        checkRangeFromTo(from, to, size);

        val theElements = elements;
        for (int i = from + 1; i <= to; i++) {
            if (((Comparable) theElements[i]).compareTo(theElements[i - 1]) < 0)
                return false;
        }
        return true;
    }

    /**
     * Returns the index of the last occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element. Tests for equality or identity as specified by {@code testForEquality}.
     *
     * @param element         the element to be searched for.
     * @param testForEquality if {@code true} -> test for equality, otherwise for identity.
     * @return the index of the last occurrence of the element in the receiver; returns {@code -1} if the element is
     * not found.
     */
    public int lastIndexOf(final @NotNull Object element, final boolean testForEquality) {
        return lastIndexOfFromTo(element, 0, size - 1, testForEquality);
    }

    /**
     * Returns the index of the last occurrence of the specified element. Returns {@code -1} if the receiver does not
     * contain this element. Searches beginning at {@code to}, inclusive until {@code from}, inclusive. Tests for
     * equality or identity as specified by {@code testForEquality}.
     *
     * @param element         element to search for.
     * @param from            the leftmost search position, inclusive.
     * @param to              the rightmost search position, inclusive.
     * @param testForEquality if {@code true} -> test for equality, otherwise for identity.
     * @return the index of the last occurrence of the element in the receiver; returns {@code -1} if the element is not
     * found.
     */
    public int lastIndexOfFromTo(final @NotNull Object element, final int from, final int to,
                                 final boolean testForEquality) {
        if (size == 0) return -1;
        checkRangeFromTo(from, to, size);

        Object[] theElements = elements;
        if (testForEquality && element != null) {
            for (int i = to; i >= from; i--) {
                if (element.equals(theElements[i])) {
                    return i;
                }
            }

        } else {
            for (int i = to; i >= from; i--) {
                if (element == theElements[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeSortFromTo(final int from, final int to) {
        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);
        java.util.Arrays.sort(elements, from, to + 1);
    }

    /**
     * Sorts the receiver according to the order induced by the specified comparator. All elements in the range must
     * be <i>mutually comparable</i> by the specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
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
    public void mergeSortFromTo(final int from, final int to, final @NotNull Comparator<Object> c) {
        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);
        java.util.Arrays.sort(elements, from, to + 1, c);
    }

    /**
     * Returns a new list of the part of the receiver between {@code from}, inclusive, and {@code to}, inclusive.
     *
     * @param from the index of the first element (inclusive).
     * @param to   the index of the last element (inclusive).
     * @return a new list
     */
    public @NotNull ObjectArrayList partFromTo(final int from, final int to) {
        if (size == 0) return new ObjectArrayList(0);

        checkRangeFromTo(from, to, size);

        val part = new Object[to - from + 1];
        System.arraycopy(elements, from, part, 0, to - from + 1);
        return new ObjectArrayList(part);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void quickSortFromTo(final int from, final int to) {
        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);
        Sorting.quickSort(elements, from, to + 1);
    }

    /**
     * Sorts the receiver according to the order induced by the specified comparator. All elements in the range must be
     * <i>mutually comparable</i> by the specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and {@code e2} in the range).
     * <p>
     * The sorting algorithm is a tuned quicksort, adapted from Jon L. Bentley and M. Douglas McIlroy's "Engineering a
     * Sort Function", Software-Practice and Experience, Vol. 23(11) P. 1249-1265 (November 1993). This algorithm
     * offers n*log(n) performance on many data sets that cause other quicksorts to degrade to quadratic performance.
     *
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to   the index of the last element (inclusive) to be sorted.
     * @param c    the comparator to determine the order of the receiver.
     */
    public void quickSortFromTo(final int from, final int to, final @NotNull Comparator<Object> c) {
        if (size == 0 || size == 1) return;
        checkRangeFromTo(from, to, size);
        Sorting.quickSort(elements, from, to + 1, c);
    }

    /**
     * Removes from the receiver all elements that are contained in the specified list. Tests for equality or identity
     * as specified by {@code testForEquality}.
     *
     * @param other           the other list.
     * @param testForEquality if {@code true} -> test for equality, otherwise for identity.
     * @return {@code true} if the receiver changed as a result of the call.
     */
    public boolean removeAll(final @NotNull ObjectArrayList other, final boolean testForEquality) {
        if (other.size == 0) return false;
        val limit = other.size - 1;
        int j = 0;
        val theElements = elements;
        for (int i = 0; i < size; i++)
            if (other.indexOfFromTo(theElements[i], 0, limit, testForEquality) < 0)
                theElements[j++] = theElements[i];
        setSize(j);

        return j != size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceFromToWithFrom(int from, int to, final @NotNull AbstractList other, int otherFrom) {
        if (other instanceof ObjectArrayList s) {
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
        checkRange(from, size);
        val e = other.iterator();
        int index = from;
        int limit = Math.min(size - from, other.size());
        for (int i = 0; i < limit; i++) elements[index++] = e.next();
    }

    /**
     * Retains (keeps) only the elements in the receiver that are contained in the specified other list. In other words,
     * removes from the receiver all of its elements that are not contained in the specified other list. Tests for
     * equality or identity as specified by {@code testForEquality}.
     *
     * @param other           the other list to test against.
     * @param testForEquality if {@code true} -> test for equality, otherwise for identity.
     * @return {@code true} if the receiver changed as a result of the call.
     */
    public boolean retainAll(final @NotNull ObjectArrayList other, final boolean testForEquality) {
        if (other.size == 0) {
            if (size == 0)
                return false;
            setSize(0);
            return true;
        }

        val limit = other.size - 1;
        int j = 0;
        val theElements = elements;

        for (int i = 0; i < size; i++) {
            if (other.indexOfFromTo(theElements[i], 0, limit, testForEquality) >= 0)
                theElements[j++] = theElements[i];
        }

        setSize(j);

        return j != size;
    }

    /**
     * Reverses the elements of the receiver. Last becomes first, second last becomes second first, and so on.
     */
    public void reverse() {
        Object tmp;
        val limit = size / 2;
        int j = size - 1;

        val theElements = elements;
        for (int i = 0; i < limit; ) {
            tmp = theElements[i];
            theElements[i++] = theElements[j];
            theElements[j--] = tmp;
        }
    }

    /**
     * Replaces the element at the specified position in the receiver with the specified element.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(final int index, final @NotNull Object element) {
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
    public void setQuick(final int index, final @NotNull Object element) {
        elements[index] = element;
    }

    /**
     * Randomly permutes the part of the receiver between {@code from} (inclusive) and {@code to} (inclusive).
     *
     * @param from the index of the first element (inclusive) to be permuted.
     * @param to   the index of the last element (inclusive) to be permuted.
     */
    public void shuffleFromTo(int from, int to) {
        if (size == 0 || size == 1)
            return;
        checkRangeFromTo(from, to, size);

        val gen = new Uniform(new MersenneTwister(new java.util.Date()));
        Object tmpElement;
        val theElements = elements;
        int random;
        for (int i = from; i < to; i++) {
            random = gen.nextIntFromTo(i, to);
            tmpElement = theElements[random];
            theElements[random] = theElements[i];
            theElements[i] = tmpElement;
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
    public @NotNull ObjectArrayList times(final int times) {
        val newList = new ObjectArrayList(times * size);
        IntStream.range(0, times).forEach(i -> newList.addAllOfFromTo(this, 0, size() - 1));
        return newList;
    }

    /**
     * Returns an array containing all of the elements in the receiver in the correct order. The runtime type of the
     * returned array is that of the specified array. If the receiver fits in the specified array, it is returned
     * therein. Otherwise, a new array is allocated with the runtime type of the specified array and the size of the
     * receiver.
     * <p>
     * If the receiver fits in the specified array with room to spare (i.e., the array has more elements than the
     * receiver), the element in the array immediately following the end of the receiver is set to null. This is useful
     * in determining the length of the receiver <em>only</em> if the caller knows that the receiver does not contain
     * any null elements.
     *
     * @param array the array into which the elements of the receiver are to be stored, if it is big enough; otherwise,
     *             a new array of the same runtime type is allocated for this purpose.
     * @return an array containing the elements of the receiver.
     */
    public @NotNull Object[] toArray(Object @NotNull [] array) {// fixme go through this: some objects can be null as a padding technique
        if (array.length < size)
            array = (Object[]) Array.newInstance(array.getClass().getComponentType(), size);

        Object[] theElements = elements;
        for (int i = size; --i >= 0; )
            array[i] = theElements[i];

        if (array.length > size)
            array[size] = null;

        return array;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ArrayList<Object> toList() {
        val mySize = size();
        val theElements = elements;
        val list = new java.util.ArrayList<>(mySize);
        list.addAll(asList(theElements).subList(0, mySize));
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trimToSize() {
        elements = Arrays.trimToCapacity(elements, size());
    }
}
