/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.buffer.tobject;

import cern.mateba.list.tobject.ObjectArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Fixed sized (non-resizable) streaming buffer connected to a target {@link ObjectBufferConsumer} to which data is
 * automatically flushed upon buffer overflow.
 */
public class ObjectBuffer implements ObjectBufferConsumer, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -8739658059958955045L;
    protected ObjectBufferConsumer target;

    protected Object[] elements;

    protected ObjectArrayList list;

    protected int capacity;

    protected int size;

    /**
     * Constructs and returns a new buffer with the given target.
     *
     * @param target   the target to flush to.
     * @param capacity the number of points the buffer shall be capable of holding before overflowing and flushing to
     *                 the target.
     */
    public ObjectBuffer(final @NotNull ObjectBufferConsumer target, final int capacity) {
        this.target = target;
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.list = new ObjectArrayList(elements);
        this.size = 0;
    }

    /**
     * Adds the specified element to the receiver.
     *
     * @param element the element to add.
     */
    public void add(final @NotNull Object element) {
        if (this.size == this.capacity) flush();
        this.elements[size++] = element;
    }

    /**
     * Adds all elements of the specified list to the receiver.
     *
     * @param list the list of which all elements shall be added.
     */
    public void addAllOf(final @NotNull ObjectArrayList list) {
        val listSize = list.size();
        if (this.size + listSize >= this.capacity) flush();
        this.target.addAllOf(list);
    }

    /**
     * Sets the receiver's size to zero. In other words, forgets about any internally buffered elements.
     */
    public void clear() {
        this.size = 0;
    }

    /**
     * Adds all internally buffered elements to the receiver's target, then resets the current buffer size to zero.
     */
    public void flush() {
        if (this.size > 0) {
            list.setSize(this.size);
            this.target.addAllOf(list);
            this.size = 0;
        }
    }

    /**
     * An implementation of the {@link Cloneable}.
     *
     * @return a copy of the object.
     * @throws AssertionError when there is no {@link Cloneable} interface.
     * @implSpec Deep copy.
     */
    @Override
    public @NotNull ObjectBuffer clone() {
        try {
            val clone = (ObjectBuffer) super.clone();
            clone.elements = this.elements.clone();
            clone.list = this.list.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
