/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.random;

import java.io.Serial;

/**
 * Abstract base class for all discrete distributions.
 */
@SuppressWarnings("unused")
public abstract class AbstractDiscreteDistribution extends AbstractDistribution {

    @Serial
    private static final long serialVersionUID = 3140695026148750590L;

    /**
     * Makes this class non instantiable, but still lets others inherit from it.
     */
    protected AbstractDiscreteDistribution() {
    }

    /**
     * Returns a random number from the distribution; returns {@code (double) nextInt()}.
     */

    public double nextDouble() {
        return nextInt();
    }

    /**
     * Returns a random number from the distribution.
     */

    public abstract int nextInt();
}
