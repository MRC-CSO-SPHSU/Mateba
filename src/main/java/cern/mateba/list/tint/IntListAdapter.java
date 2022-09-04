/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.list.tint;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;

/**
 * Adapter that permits an {@link cern.mateba.list.tint.IntArrayList} to be viewed and treated as a
 * JDK {@link java.util.AbstractList}. Makes the contained list compatible with the JDK Collections Framework.
 * <p>
 * Any attempt to pass elements other than {@link java.lang.Number} to setter methods will throw a
 * {@link java.lang.ClassCastException}. {@link java.lang.Number#intValue()} is used to convert objects into primitive
 * values which are then stored in the backing templated list. Getter methods return {@link java.lang.Integer} objects.
 */
@SuppressWarnings("unused")
public class IntListAdapter extends AbstractList<Integer> {
    protected IntArrayList content;

    /**
     * Constructs a list backed by the specified content list.
     */
    public IntListAdapter(final @NotNull IntArrayList content) {
        this.content = content;
    }

    /**
     * Transforms an element of a primitive data type to an object.
     */
    protected static @NotNull Integer object(final int element) {
        return element;
    } // fixme remove as redundant

    /**
     * Inserts the specified element at the specified position in this list (optional operation). Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to their indexes).
     *
     * @param index   index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    public void add(final int index, final @NotNull Integer element) {
        content.beforeInsert(index, element);
        modCount++;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     */

    public @NotNull Integer get(final int index) {
        return content.get(index);
    }

    /**
     * Removes the element at the specified position in this list (optional operation). Shifts any subsequent elements
     * to the left (subtracts one from their indexes). Returns the element that was removed from the list.
     *
     * @param index the index of the element to remove.
     * @return the element previously at the specified position.
     */

    public @NotNull Integer remove(final int index) {
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
    public Integer set(final int index, final @NotNull Integer element) {
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
