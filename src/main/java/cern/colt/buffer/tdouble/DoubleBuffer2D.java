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
 * Fixed sized (non-resizable) streaming buffer connected to a target {@link DoubleBuffer2DConsumer} to which data is
 * automatically flushed upon buffer overflow.
 */
public class DoubleBuffer2D implements DoubleBuffer2DConsumer, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -6113495916975789855L;
    protected DoubleBuffer2DConsumer target;

    protected double[] xElements;

    protected double[] yElements;

    protected DoubleArrayList xList;

    protected DoubleArrayList yList;

    protected int capacity;

    protected int size;

    /**
     * Constructs and returns a new buffer with the given target.
     *
     * @param target   the target to flush to.
     * @param capacity the number of points the buffer shall be capable of holding before overflowing and flushing to
     *                 the target.
     */
    public DoubleBuffer2D(final @NotNull DoubleBuffer2DConsumer target, final int capacity) {
        this.target = target;
        this.capacity = capacity;
        this.xElements = new double[capacity];
        this.yElements = new double[capacity];
        this.xList = new DoubleArrayList(xElements);
        this.yList = new DoubleArrayList(yElements);
        this.size = 0;
    }

    /**
     * Adds the specified point (x,y) to the receiver.
     *
     * @param x the x-coordinate of the point to add.
     * @param y the y-coordinate of the point to add.
     */
    public void add(final double x, final double y) {
        if (this.size == this.capacity) flush();
        this.xElements[this.size] = x;
        this.yElements[this.size++] = y;
    }

    /**
     * Adds all specified points (x,y) to the receiver.
     *
     * @param x the x-coordinates of the points to add.
     * @param y the y-coordinates of the points to add.
     */
    public void addAllOf(final @NotNull DoubleArrayList x, final @NotNull DoubleArrayList y) {
        val listSize = x.size();
        if (this.size + listSize >= this.capacity) flush();
        this.target.addAllOf(x, y);
    }

    /**
     * Sets the receiver's size to zero. In other words, forgets about any internally buffered elements.
     */
    public void clear() {
        this.size = 0;
    }

    /**
     * Adds all internally buffered points to the receiver's target, then resets the current buffer size to zero.
     */
    public void flush() {
        if (this.size > 0) {
            xList.setSize(this.size);
            yList.setSize(this.size);
            this.target.addAllOf(xList, yList);
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
    public @NotNull DoubleBuffer2D clone() {
        try {
            val clone = (DoubleBuffer2D) super.clone();
            clone.xElements = this.xElements.clone();
            clone.yElements = this.yElements.clone();

            clone.xList = this.xList.clone();
            clone.yList = this.yList.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
