/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.buffer.tboolean;

import cern.mateba.list.tboolean.BooleanArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Fixed sized (non-resizable) streaming buffer connected to a target {@link BooleanBuffer3DConsumer} to which data is
 * automatically flushed upon buffer overflow.
 */
public class BooleanBuffer3D implements BooleanBuffer3DConsumer, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 2192548328925657855L;
    protected BooleanBuffer3DConsumer target;

    protected boolean[] xElements;

    protected boolean[] yElements;

    protected boolean[] zElements;

    protected BooleanArrayList xList;

    protected BooleanArrayList yList;

    protected BooleanArrayList zList;

    protected int capacity;

    protected int size;

    /**
     * Constructs and returns a new buffer with the given target.
     *
     * @param target   the target to flush to.
     * @param capacity the number of points the buffer shall be capable of holding before overflowing and flushing to
     *                 the target.
     */
    public BooleanBuffer3D(final @NotNull BooleanBuffer3DConsumer target, final int capacity) {
        this.target = target;
        this.capacity = capacity;
        this.xElements = new boolean[capacity];
        this.yElements = new boolean[capacity];
        this.zElements = new boolean[capacity];
        this.xList = new BooleanArrayList(xElements);
        this.yList = new BooleanArrayList(yElements);
        this.zList = new BooleanArrayList(zElements);
        this.size = 0;
    }

    /**
     * Adds the specified point (x,y,z) to the receiver.
     *
     * @param x the x-coordinate of the point to add.
     * @param y the y-coordinate of the point to add.
     * @param z the z-coordinate of the point to add.
     */
    public void add(final boolean x, final boolean y, final boolean z) {
        if (this.size == this.capacity) flush();
        this.xElements[this.size] = x;
        this.yElements[this.size++] = y;
        this.zElements[this.size++] = z;
    }

    /**
     * Adds all specified (x,y,z) points to the receiver.
     *
     * @param xElements the x-coordinates of the points.
     * @param yElements the y-coordinates of the points.
     * @param zElements the y-coordinates of the points.
     */
    public void addAllOf(@NotNull BooleanArrayList xElements, @NotNull BooleanArrayList yElements,
                         @NotNull BooleanArrayList zElements) {
        val listSize = xElements.size();
        if (this.size + listSize >= this.capacity) flush();
        this.target.addAllOf(xElements, yElements, zElements);
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
            zList.setSize(this.size);
            this.target.addAllOf(xList, yList, zList);
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
    public @NotNull BooleanBuffer3D clone() {
        try {
            val clone = (BooleanBuffer3D) super.clone();

            clone.xElements = this.xElements.clone();
            clone.yElements = this.yElements.clone();
            clone.zElements = this.zElements.clone();

            clone.xList = this.xList.clone();
            clone.yList = this.yList.clone();
            clone.zList = this.zList.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
