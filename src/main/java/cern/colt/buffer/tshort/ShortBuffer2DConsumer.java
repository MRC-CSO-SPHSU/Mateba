/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.buffer.tshort;

import cern.colt.list.tshort.ShortArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * Target of a streaming {@link ShortBuffer2D} into which data is flushed upon buffer overflow.
 */
public interface ShortBuffer2DConsumer {
    /**
     * Adds all specified (x,y) points to the receiver.
     *
     * @param x the x-coordinates of the points to be added.
     * @param y the y-coordinates of the points to be added.
     */
    void addAllOf(final @NotNull ShortArrayList x, final @NotNull ShortArrayList y);
}
