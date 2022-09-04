/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.random;

import cern.mateba.function.tdouble.DoubleFunction;
import cern.mateba.function.tint.IntFunction;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract base class for all random distributions.
 * <p>
 * A subclass of this class need to override method {@code nextDouble()} and, in rare cases, also {@code nextInt()}.
 * <p>
 * Currently, all subclasses use a uniform pseudo-random number generation engine and transform its results to the
 * target distribution. Thus, they expect such a uniform engine upon instance construction.
 * <p>
 * {@link MersenneTwister} is recommended as uniform pseudo-random number generation engine, since it is very
 * strong and at the same time quick. {@link #makeDefaultGenerator()} will conveniently construct and return such a
 * magic thing. You can also, for example, use {@link MersenneTwister}, a quicker (but much weaker) uniform random
 * number generation engine. Of course, you can also use other strong uniform random number generation engines.
 *
 * @see <a href="https://physics.web.cern.ch/dataanalysis/briefbook/">CERN Data Analysis Briefbook</a>
 * @see <a href="https://docs.tibco.com/data-science/textbook">StatSoft Electronic Textbook</a>
 * @see <a href="https://www.stat.berkeley.edu/users/stark/SticiGui/Text/gloss.htm">Glossary of Statistical Terms</a>
 */
public abstract class AbstractDistribution implements DoubleFunction, IntFunction, Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -8418417234122511358L;
    @Setter
    @Getter
    protected RandomEngine randomGenerator;

    /**
     * Makes this class non instantiable, but still lets others inherit from it.
     */
    protected AbstractDistribution() {
    }

    /**
     * Constructs and returns a new uniform random number generation engine seeded with the current time. Currently,
     * this is {@link MersenneTwister}.
     */
    public static RandomEngine makeDefaultGenerator() {
        return RandomEngine.makeDefault();
    }

    /**
     * Equivalent to {@code nextDouble()}. This has the effect that distributions can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    public double apply(double dummy) {
        return nextDouble();
    }

    /**
     * Equivalent to {@code nextInt()}. This has the effect that distributions can now be used as function objects,
     * returning a random number upon function evaluation.
     */
    public int apply(int dummy) {
        return nextInt();
    }

    /**
     * Returns a deep copy of the receiver; the copy will produce identical sequences. After this call has returned, the
     * copy and the receiver have equal but separate state.
     *
     * @return a copy of the receiver.
     */
    public AbstractDistribution clone() {
        AbstractDistribution copy;
        try {
            copy = (AbstractDistribution) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (this.randomGenerator != null)
            copy.randomGenerator = this.randomGenerator.clone();
        return copy;
    }

    /**
     * Returns a random number from the distribution.
     */
    public abstract double nextDouble();

    /**
     * Returns a random number from the distribution; returns {@code (int) Math.round(nextDouble())}. Override this
     * method if necessary.
     */
    public int nextInt() {
        return (int) Math.round(nextDouble());
    }
}
