/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.stat;

import java.io.Serial;
import java.io.Serializable;

/**
 * A buffer holding elements; internally used for computing approximate
 * quantiles.
 */
public abstract class AllocatableBuffer implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -3671715652000165920L;
    public int weight;

    public int level;

    public int k;

    public boolean isAllocated;

    /**
     * This method was created in VisualAge.
     *
     * @param k int
     */
    public AllocatableBuffer(int k) {
        this.k = k;
        this.weight = 1;
        this.level = 0;
        this.isAllocated = false;
    }

    /**
     * Clears the receiver.
     */
    public abstract void clear();

    /**
     * Returns whether the receiver is already allocated.
     */
    public boolean isAllocated() {
        return isAllocated;
    }

    /**
     * Returns whether the receiver is empty.
     */
    public abstract boolean isEmpty();

    /**
     * Returns whether the receiver is empty.
     */
    public abstract boolean isFull();

    /**
     * Returns whether the receiver is partial.
     */
    public boolean isPartial() {
        return !(isEmpty() || isFull());
    }

    /**
     * Returns whether the receiver's level.
     */
    public int level() {
        return level;
    }

    /**
     * Sets the receiver's level.
     */
    public void level(int level) {
        this.level = level;
    }

    /**
     * Returns the number of elements contained in the receiver.
     */
    public abstract int size();

    /**
     * Sorts the receiver.
     */
    public abstract void sort();

    /**
     * Returns whether the receiver's weight.
     */
    public int weight() {
        return weight;
    }

    /**
     * Sets the receiver's weight.
     */
    public void weight(int weight) {
        this.weight = weight;
    }

    @Override
    public AllocatableBuffer clone() {
        try {
            return (AllocatableBuffer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
