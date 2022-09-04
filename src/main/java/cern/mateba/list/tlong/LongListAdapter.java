/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.list.tlong;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;

/**
 * Adapter that permits an {@link LongArrayList} to be viewed and treated as a JDK {@link java.util.AbstractList}.
 * Makes the contained list compatible with the JDK Collections Framework.
 * <p>
 * Any attempt to pass elements other than {@code java.lang.Number} to setter
 * methods will throw a {@code java.lang.ClassCastException}.
 * {@code java.lang.Number.longValue()} is used to convert objects into
 * primitive values which are then stored in the backing templated list. Getter
 * methods return {@code java.lang.Long} objects.
 */
@SuppressWarnings("unused")
public class LongListAdapter extends AbstractList<Long> {
    protected LongArrayList content;

    /**
     * Constructs a list backed by the specified content list.
     */
    public LongListAdapter(final @NotNull LongArrayList content) {
        this.content = content;
    }

    /**
     * Transforms an object element to a primitive data type.
     */
    protected static long value(final @NotNull Long element) {
        return element;
    } // fixme remove as redundant

    /**
     * Inserts the specified element at the specified position in this list (optional operation). Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to their indexes).
     *
     * @param index   index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    public void add(final int index, final @NotNull Long element) {
        content.beforeInsert(index, value(element));
        modCount++;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     */
    public @NotNull Long get(final int index) {
        return content.get(index);
    }

    /**
     * Removes the element at the specified position in this list (optional operation). Shifts any subsequent elements
     * to the left (subtracts one from their indexes). Returns the element that was removed from the list.
     * <p>
     *
     * @param index the index of the element to remove.
     * @return the element previously at the specified position.
     */
    public @NotNull Long remove(final int index) {
        val old = get(index);
        content.remove(index);
        modCount++;
        return old;
    }

    /**
     * Replaces the element at the specified position in this list with the specified element (optional operation).
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     */
    public @NotNull Long set(final int index, final @NotNull Long element) {
        val old = get(index);
        content.set(index, element);
        return old;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list.
     */
    public int size() {
        return content.size();
    }
}
