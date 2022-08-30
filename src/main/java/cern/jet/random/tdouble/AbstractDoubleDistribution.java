/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.jet.random.tdouble;

import cern.colt.PersistentObject;
import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.function.tint.IntFunction;
import cern.jet.random.tdouble.engine.DRand;
import cern.jet.random.tdouble.engine.MersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.Serial;

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
 * magic thing. You can also, for example, use {@link DRand}, a quicker (but much weaker) uniform random number
 * generation engine. Of course, you can also use other strong uniform random number generation engines.
 *
 * @see cern.jet.random.tdouble.engine
 * @see <a href="https://physics.web.cern.ch/dataanalysis/briefbook/">CERN Data Analysis Briefbook</a>
 * @see <a href="https://docs.tibco.com/data-science/textbook">StatSoft Electronic Textbook</a>
 * @see <a href="https://www.stat.berkeley.edu/users/stark/SticiGui/Text/gloss.htm">Glossary of Statistical Terms</a>
 */
public abstract class AbstractDoubleDistribution extends PersistentObject implements DoubleFunction, IntFunction {

    @Serial
    private static final long serialVersionUID = 1L;
    @Setter
    @Getter
    protected DoubleRandomEngine randomGenerator;

    /**
     * Makes this class non instantiable, but still lets others inherit from it.
     */
    protected AbstractDoubleDistribution() {
    }

    /**
     * Constructs and returns a new uniform random number generation engine seeded with the current time. Currently,
     * this is {@link MersenneTwister}.
     */
    public static DoubleRandomEngine makeDefaultGenerator() {
        return DoubleRandomEngine.makeDefault();
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

    public Object clone() {
        val copy = (AbstractDoubleDistribution) super.clone();
        if (this.randomGenerator != null)
            copy.randomGenerator = (DoubleRandomEngine) this.randomGenerator.clone();
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
