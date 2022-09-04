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
 * An abstract set of buffers; internally used for computing approximate
 * quantiles.
 */
public abstract class BufferSet implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 5924069290045755548L;

    @Override
    public BufferSet clone() {
        try {
            return (BufferSet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
