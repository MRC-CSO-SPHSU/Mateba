/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.buffer.tboolean;

import cern.colt.list.tboolean.BooleanArrayList;

/**
 * Fixed sized (non resizable) streaming buffer connected to a target
 * <tt>BooleanBuffer2DConsumer</tt> to which data is automatically flushed upon
 * buffer overflow.
 * 
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public class BooleanBuffer2D extends cern.colt.PersistentObject implements BooleanBuffer2DConsumer {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected BooleanBuffer2DConsumer target;

    protected boolean[] xElements;

    protected boolean[] yElements;

    // vars cached for speed
    protected BooleanArrayList xList;

    protected BooleanArrayList yList;

    protected int capacity;

    protected int size;

    /**
     * Constructs and returns a new buffer with the given target.
     * 
     * @param target
     *            the target to flush to.
     * @param capacity
     *            the number of points the buffer shall be capable of holding
     *            before overflowing and flushing to the target.
     */
    public BooleanBuffer2D(BooleanBuffer2DConsumer target, int capacity) {
        this.target = target;
        this.capacity = capacity;
        this.xElements = new boolean[capacity];
        this.yElements = new boolean[capacity];
        this.xList = new BooleanArrayList(xElements);
        this.yList = new BooleanArrayList(yElements);
        this.size = 0;
    }

    /**
     * Adds the specified point (x,y) to the receiver.
     * 
     * @param x
     *            the x-coordinate of the point to add.
     * @param y
     *            the y-coordinate of the point to add.
     */
    public void add(boolean x, boolean y) {
        if (this.size == this.capacity)
            flush();
        this.xElements[this.size] = x;
        this.yElements[this.size++] = y;
    }

    /**
     * Adds all specified points (x,y) to the receiver.
     * 
     * @param x
     *            the x-coordinates of the points to add.
     * @param y
     *            the y-coordinates of the points to add.
     */
    public void addAllOf(BooleanArrayList x, BooleanArrayList y) {
        int listSize = x.size();
        if (this.size + listSize >= this.capacity)
            flush();
        this.target.addAllOf(x, y);
    }

    /**
     * Sets the receiver's size to zero. In other words, forgets about any
     * internally buffered elements.
     */
    public void clear() {
        this.size = 0;
    }

    /**
     * Adds all internally buffered points to the receiver's target, then resets
     * the current buffer size to zero.
     */
    public void flush() {
        if (this.size > 0) {
            xList.setSize(this.size);
            yList.setSize(this.size);
            this.target.addAllOf(xList, yList);
            this.size = 0;
        }
    }
}
