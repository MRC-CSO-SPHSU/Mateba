/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.buffer.tdouble;

import cern.colt.list.tdouble.DoubleArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Fixed sized (non-resizable) streaming buffer connected to a target {@link DoubleBuffer1DConsumer} to which data is
 * automatically flushed upon buffer overflow.
 */
public class DoubleBuffer1D implements DoubleBuffer1DConsumer, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -3539381563764230388L;
    protected DoubleBuffer1DConsumer target;

    protected double[] elements;

    protected DoubleArrayList list;

    protected int capacity;

    protected int size;

    /**
     * Constructs and returns a new buffer with the given target.
     *
     * @param target   the target to flush to.
     * @param capacity the number of points the buffer shall be capable of holding before overflowing and flushing to
     *                 the target.
     */
    public DoubleBuffer1D(final @NotNull DoubleBuffer1DConsumer target, final int capacity) {
        this.target = target;
        this.capacity = capacity;
        this.elements = new double[capacity];
        this.list = new DoubleArrayList(elements);
        this.size = 0;
    }

    /**
     * Adds the specified element to the receiver.
     *
     * @param element the element to add.
     */
    public void add(final double element) {
        if (this.size == this.capacity) flush();
        this.elements[size++] = element;
    }

    /**
     * Adds all elements of the specified list to the receiver.
     *
     * @param list the list of which all elements shall be added.
     */
    public void addAllOf(final @NotNull DoubleArrayList list) {
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
    public @NotNull DoubleBuffer1D clone() {
        try {
            val clone = (DoubleBuffer1D) super.clone();
            clone.elements = this.elements.clone();
            clone.list = this.list.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
