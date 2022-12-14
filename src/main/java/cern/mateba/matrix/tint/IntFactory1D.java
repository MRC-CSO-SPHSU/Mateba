/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.matrix.tint;

import cern.mateba.list.tint.IntArrayList;
import cern.mateba.matrix.tint.impl.DenseIntMatrix1D;
import cern.mateba.matrix.tint.impl.SparseIntMatrix1D;
import cern.jet.math.tint.IntFunctions;
import cern.jet.random.sampling.RandomSamplingAssistant;
import cern.jet.random.engine.MersenneTwister;

import java.io.Serial;
import java.io.Serializable;

/**
 * Factory for convenient construction of 1-d matrices holding <tt>int</tt>
 * cells. Use idioms like <tt>IntFactory1D.dense.make(1000)</tt> to construct
 * dense matrices, <tt>IntFactory1D.sparse.make(1000)</tt> to construct sparse
 * matrices.
 * <p>
 * If the factory is used frequently it might be useful to streamline the
 * notation. For example by aliasing:
 * <table>
 * <td class="PRE">
 *
 * <pre>
 *  IntFactory1D F = IntFactory1D.dense;
 *  F.make(1000);
 *  F.descending(10);
 *  F.random(3);
 *  ...
 * </pre>
 *
 * </td>
 * </table>
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class IntFactory1D implements Serializable, Cloneable {

    /**
     * A factory producing dense matrices.
     */
    public static final IntFactory1D dense = new IntFactory1D();

    /**
     * A factory producing sparse matrices.
     */
    public static final IntFactory1D sparse = new IntFactory1D();
    @Serial
    private static final long serialVersionUID = 4424123406054804492L;

    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected IntFactory1D() {
    }

    /**
     * C = A||B; Constructs a new matrix which is the concatenation of two other
     * matrices. Example: <tt>0 1</tt> append <tt>3 4</tt> --> <tt>0 1 3 4</tt>.
     */
    public IntMatrix1D append(IntMatrix1D A, IntMatrix1D B) {
        // concatenate
        IntMatrix1D matrix = make((int) (A.size() + B.size()));
        matrix.viewPart(0, (int) A.size()).assign(A);
        matrix.viewPart((int) A.size(), (int) B.size()).assign(B);
        return matrix;
    }

    /**
     * Constructs a matrix with cells having ascending values. For debugging
     * purposes. Example: <tt>0 1 2</tt>
     */
    public IntMatrix1D ascending(int size) {
        cern.jet.math.tint.IntFunctions F = cern.jet.math.tint.IntFunctions.intFunctions;
        return descending(size).assign(IntFunctions.chain(IntFunctions.neg, IntFunctions.minus(size)));
    }

    /**
     * Constructs a matrix with cells having descending values. For debugging
     * purposes. Example: <tt>2 1 0</tt>
     */
    public IntMatrix1D descending(int size) {
        IntMatrix1D matrix = make(size);
        int v = 0;
        for (int i = size; --i >= 0; ) {
            matrix.setQuick(i, v++);
        }
        return matrix;
    }

    /**
     * Constructs a matrix with the given cell values. The values are copied. So
     * subsequent changes in <tt>values</tt> are not reflected in the matrix,
     * and vice-versa.
     *
     * @param values The values to be filled into the new matrix.
     */
    public IntMatrix1D make(int[] values) {
        if (this == sparse)
            return new SparseIntMatrix1D(values);
        else
            return new DenseIntMatrix1D(values);
    }

    /**
     * Constructs a matrix which is the concatenation of all given parts. Cells
     * are copied.
     */
    public IntMatrix1D make(IntMatrix1D[] parts) {
        if (parts.length == 0)
            return make(0);

        int size = 0;
        for (int i = 0; i < parts.length; i++)
            size += parts[i].size();

        IntMatrix1D vector = make(size);
        size = 0;
        for (int i = 0; i < parts.length; i++) {
            vector.viewPart(size, (int) parts[i].size()).assign(parts[i]);
            size += parts[i].size();
        }

        return vector;
    }

    /**
     * Constructs a matrix with the given shape, each cell initialized with
     * zero.
     */
    public IntMatrix1D make(int size) {
        if (this == sparse)
            return new SparseIntMatrix1D(size);
        return new DenseIntMatrix1D(size);
    }

    /**
     * Constructs a matrix with the given shape, each cell initialized with the
     * given value.
     */
    public IntMatrix1D make(int size, int initialValue) {
        return make(size).assign(initialValue);
    }

    /**
     * Constructs a matrix from the values of the given list. The values are
     * copied. So subsequent changes in <tt>values</tt> are not reflected in the
     * matrix, and vice-versa.
     *
     * @param values The values to be filled into the new matrix.
     * @return a new matrix.
     */
    public IntMatrix1D make(IntArrayList values) {
        int size = values.size();
        IntMatrix1D vector = make(size);
        for (int i = size; --i >= 0; )
            vector.set(i, values.get(i));
        return vector;
    }

    /**
     * Constructs a matrix with uniformly distributed values in <tt>(0,1)</tt>
     * (exclusive).
     */
    public IntMatrix1D random(int size) {
        return make(size).assign(cern.jet.math.tint.IntFunctions.random());
    }

    /**
     * C = A||A||..||A; Constructs a new matrix which is concatenated
     * <tt>repeat</tt> times. Example:
     *
     * <pre>
     *   0 1
     *   repeat(3) --&gt;
     *   0 1 0 1 0 1
     *
     * </pre>
     */
    public IntMatrix1D repeat(IntMatrix1D A, int repeat) {
        int size = (int) A.size();
        IntMatrix1D matrix = make(repeat * size);
        for (int i = repeat; --i >= 0; ) {
            matrix.viewPart(size * i, size).assign(A);
        }
        return matrix;
    }

    /**
     * Constructs a randomly sampled matrix with the given shape. Randomly picks
     * exactly <tt>Math.round(size*nonZeroFraction)</tt> cells and initializes
     * them to <tt>value</tt>, all the rest will be initialized to zero. Note
     * that this is not the same as setting each cell with probability
     * <tt>nonZeroFraction</tt> to <tt>value</tt>.
     *
     * @throws IllegalArgumentException if <tt>nonZeroFraction < 0 || nonZeroFraction > 1</tt>.
     * @see cern.jet.random.sampling.RandomSamplingAssistant
     */
    public IntMatrix1D sample(int size, int value, int nonZeroFraction) {
        double epsilon = 1e-09;
        if (nonZeroFraction < 0 - epsilon || nonZeroFraction > 1 + epsilon)
            throw new IllegalArgumentException();
        if (nonZeroFraction < 0)
            nonZeroFraction = 0;
        if (nonZeroFraction > 1)
            nonZeroFraction = 1;

        IntMatrix1D matrix = make(size);

        int n = Math.round(size * nonZeroFraction);
        if (n == 0)
            return matrix;

        RandomSamplingAssistant sampler = new RandomSamplingAssistant(n, size, new MersenneTwister());
        for (int i = size; --i >= 0; ) {
            if (sampler.sampleNextElement()) {
                matrix.set(i, value);
            }
        }

        return matrix;
    }

    /**
     * Constructs a list from the given matrix. The values are copied. So
     * subsequent changes in <tt>values</tt> are not reflected in the list, and
     * vice-versa.
     *
     * @param values The values to be filled into the new list.
     * @return a new list.
     */
    public cern.mateba.list.tint.IntArrayList toList(IntMatrix1D values) {
        int size = (int) values.size();
        cern.mateba.list.tint.IntArrayList list = new cern.mateba.list.tint.IntArrayList(size);
        list.setSize(size);
        for (int i = size; --i >= 0; )
            list.set(i, values.get(i));
        return list;
    }

    @Override
    public IntFactory1D clone() {
        try {
            return (IntFactory1D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
