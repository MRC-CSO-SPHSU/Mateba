/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.matrix.tlong;

import cern.colt.matrix.tlong.impl.DenseLongMatrix2D;
import cern.colt.matrix.tlong.impl.SparseLongMatrix2D;
import cern.colt.matrix.tlong.impl.SparseRCLongMatrix2D;
import cern.jet.math.tlong.LongFunctions;
import cern.jet.random.tdouble.engine.MersenneTwister;

/**
 * Factory for convenient construction of 2-d matrices holding <tt>int</tt>
 * cells. Also provides convenient methods to compose (concatenate) and
 * decompose (split) matrices from/to constituent blocks. </p>
 * <p>
 * &nbsp;
 * </p>
 * <table border="0" cellspacing="0">
 * <tr align="left" valign="top">
 * <td><i>Construction</i></td>
 * <td>Use idioms like <tt>LongFactory2D.dense.make(4,4)</tt> to construct dense
 * matrices, <tt>LongFactory2D.sparse.make(4,4)</tt> to construct sparse
 * matrices.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i> Construction with initial values </i></td>
 * <td>Use other <tt>make</tt> methods to construct matrices with given initial
 * values.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i> Appending rows and columns </i></td>
 * <td>Use methods {@link #appendColumns(LongMatrix2D,LongMatrix2D)
 * appendColumns}, {@link #appendColumns(LongMatrix2D,LongMatrix2D) appendRows}
 * and {@link #repeat(LongMatrix2D,int,int) repeat} to append rows and columns.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i> General block matrices </i></td>
 * <td>Use methods {@link #compose(LongMatrix2D[][]) compose} and
 * {@link #decompose(LongMatrix2D[][],LongMatrix2D) decompose} to work with
 * general block matrices.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i> Diagonal matrices </i></td>
 * <td>Use methods {@link #diagonal(LongMatrix1D) diagonal(vector)},
 * {@link #diagonal(LongMatrix2D) diagonal(matrix)} and {@link #identity(int)
 * identity} to work with diagonal matrices.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i> Diagonal block matrices </i></td>
 * <td>Use method
 * {@link #composeDiagonal(LongMatrix2D,LongMatrix2D,LongMatrix2D)
 * composeDiagonal} to work with diagonal block matrices.</td>
 * </tr>
 * <tr align="left" valign="top">
 * <td><i>Random</i></td>
 * <td>Use methods {@link #random(int,int) random} and
 * {@link #sample(int,int,int,int) sample} to construct random matrices.</td>
 * </tr>
 * </table>
 * <p>
 * &nbsp;
 * </p>
 * <p>
 * If the factory is used frequently it might be useful to streamline the
 * notation. For example by aliasing:
 * </p>
 * <table>
 * <td class="PRE">
 * 
 * <pre>
 *  LongFactory2D F = LongFactory2D.dense;
 *  F.make(4,4);
 *  F.descending(10,20);
 *  F.random(4,4);
 *  ...
 * </pre>
 * 
 * </td>
 * </table>
 * 
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class LongFactory2D extends cern.colt.PersistentObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A factory producing dense matrices.
     */
    public static final LongFactory2D dense = new LongFactory2D();

    /**
     * A factory producing sparse hash matrices.
     */
    public static final LongFactory2D sparse = new LongFactory2D();

    /**
     * A factory producing sparse row compressed matrices.
     */
    public static final LongFactory2D rowCompressed = new LongFactory2D();

    /*
     * A factory producing sparse row compressed modified matrices.
     */
    // public static final LongFactory2D rowCompressedModified = new
    // LongFactory2D();
    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected LongFactory2D() {
    }

    /**
     * C = A||B; Constructs a new matrix which is the column-wise concatenation
     * of two other matrices.
     * 
     * <pre>
     *   0 1 2
     *   3 4 5
     *   appendColumns
     *   6 7
     *   8 9
     *   --&gt;
     *   0 1 2 6 7 
     *   3 4 5 8 9
     * 
     * </pre>
     */
    public LongMatrix2D appendColumns(LongMatrix2D A, LongMatrix2D B) {
        // force both to have maximal shared number of rows.
        if (B.rows() > A.rows())
            B = B.viewPart(0, 0, A.rows(), B.columns());
        else if (B.rows() < A.rows())
            A = A.viewPart(0, 0, B.rows(), A.columns());

        // concatenate
        int ac = A.columns();
        int bc = B.columns();
        int r = A.rows();
        LongMatrix2D matrix = make(r, ac + bc);
        matrix.viewPart(0, 0, r, ac).assign(A);
        matrix.viewPart(0, ac, r, bc).assign(B);
        return matrix;
    }

    public LongMatrix2D appendColumn(LongMatrix2D A, LongMatrix1D b) {
        // force both to have maximal shared number of rows.
        if (b.size() > A.rows())
            b = b.viewPart(0, A.rows());
        else if (b.size() < A.rows())
            A = A.viewPart(0, 0, (int) b.size(), A.columns());

        // concatenate
        int ac = A.columns();
        int bc = 1;
        int r = A.rows();
        LongMatrix2D matrix = make(r, ac + bc);
        matrix.viewPart(0, 0, r, ac).assign(A);
        matrix.viewColumn(ac).assign(b);
        return matrix;
    }

    /**
     * C = A||B; Constructs a new matrix which is the row-wise concatenation of
     * two other matrices.
     * 
     * <pre>
     *   0 1 
     *   2 3 
     *   4 5
     *   appendRows
     *   6 7
     *   8 9
     *   --&gt;
     *   0 1 
     *   2 3 
     *   4 5
     *   6 7
     *   8 9
     * 
     * </pre>
     */
    public LongMatrix2D appendRows(LongMatrix2D A, LongMatrix2D B) {
        // force both to have maximal shared number of columns.
        if (B.columns() > A.columns())
            B = B.viewPart(0, 0, B.rows(), A.columns());
        else if (B.columns() < A.columns())
            A = A.viewPart(0, 0, A.rows(), B.columns());

        // concatenate
        int ar = A.rows();
        int br = B.rows();
        int c = A.columns();
        LongMatrix2D matrix = make(ar + br, c);
        matrix.viewPart(0, 0, ar, c).assign(A);
        matrix.viewPart(ar, 0, br, c).assign(B);
        return matrix;
    }

    public LongMatrix2D appendRow(LongMatrix2D A, LongMatrix1D b) {
        // force both to have maximal shared number of columns.
        if (b.size() > A.columns())
            b = b.viewPart(0, A.columns());
        else if (b.size() < A.columns())
            A = A.viewPart(0, 0, A.rows(), (int) b.size());

        // concatenate
        int ar = A.rows();
        int br = 1;
        int c = A.columns();
        LongMatrix2D matrix = make(ar + br, c);
        matrix.viewPart(0, 0, ar, c).assign(A);
        matrix.viewRow(ar).assign(b);
        return matrix;
    }

    /**
     * Constructs a matrix with cells having ascending values. For debugging
     * purposes. Example:
     * 
     * <pre>
     *   0 1 2 
     *   3 4 5
     * 
     * </pre>
     */
    public LongMatrix2D ascending(int rows, int columns) {
        cern.jet.math.tlong.LongFunctions F = cern.jet.math.tlong.LongFunctions.longFunctions;
        return descending(rows, columns).assign(
                LongFunctions.chain(LongFunctions.neg, LongFunctions.minus(columns * rows)));
    }

    /**
     * Checks whether the given array is rectangular, that is, whether all rows
     * have the same number of columns.
     * 
     * @throws IllegalArgumentException
     *             if the array is not rectangular.
     */
    protected static void checkRectangularShape(int[][] array) {
        int columns = -1;
        for (int row = array.length; --row >= 0;) {
            if (array[row] != null) {
                if (columns == -1)
                    columns = array[row].length;
                if (array[row].length != columns)
                    throw new IllegalArgumentException("All rows of array must have same number of columns.");
            }
        }
    }

    /**
     * Checks whether the given array is rectangular, that is, whether all rows
     * have the same number of columns.
     * 
     * @throws IllegalArgumentException
     *             if the array is not rectangular.
     */
    protected static void checkRectangularShape(LongMatrix2D[][] array) {
        int columns = -1;
        for (int row = array.length; --row >= 0;) {
            if (array[row] != null) {
                if (columns == -1)
                    columns = array[row].length;
                if (array[row].length != columns)
                    throw new IllegalArgumentException("All rows of array must have same number of columns.");
            }
        }
    }

    public LongMatrix2D reshape(LongMatrix1D a, int rows, int columns) {
        if (a.size() != rows * columns) {
            throw new IllegalArgumentException("a.size() != rows*columns");
        }
        LongMatrix2D A;
        if (this == sparse) {
            A = new SparseLongMatrix2D(rows, columns);
        } else {
            A = new DenseLongMatrix2D(rows, columns);
        }
        for (int c = 0; c < columns; c++) {
            A.viewColumn(c).assign(a.viewPart(c * rows, rows));
        }
        return A;
    }

    /**
     * Constructs a block matrix made from the given parts. The inverse to
     * method {@link #decompose(LongMatrix2D[][], LongMatrix2D)}.
     * <p>
     * All matrices of a given column within <tt>parts</tt> must have the same
     * number of columns. All matrices of a given row within <tt>parts</tt> must
     * have the same number of rows. Otherwise an
     * <tt>IllegalArgumentException</tt> is thrown. Note that <tt>null</tt>s
     * within <tt>parts[row,col]</tt> are an exception to this rule: they are
     * ignored. Cells are copied. Example:
     * <table border="1" cellspacing="0">
     * <tr align="left" valign="top">
     * <td><tt>Code</tt></td>
     * <td><tt>Result</tt></td>
     * </tr>
     * <tr align="left" valign="top">
     * <td>
     * 
     * <pre>
     * LongMatrix2D[][] parts1 = { { null, make(2, 2, 1), null }, { make(4, 4, 2), null, make(4, 3, 3) },
     *         { null, make(2, 2, 4), null } };
     * System.out.println(compose(parts1));
     * </pre>
     * 
     * </td>
     * <td><tt>8&nbsp;x&nbsp;9&nbsp;matrix<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;1&nbsp;1&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;1&nbsp;1&nbsp;0&nbsp;0&nbsp;0<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;0&nbsp;0&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;0&nbsp;0&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;0&nbsp;0&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;0&nbsp;0&nbsp;3&nbsp;3&nbsp;3<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;4&nbsp;4&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;4&nbsp;4&nbsp;0&nbsp;0&nbsp;0</tt></td>
     * </tr>
     * <tr align="left" valign="top">
     * <td>
     * 
     * <pre>
     * LongMatrix2D[][] parts3 = { { identity(3), null, }, { null, identity(3).viewColumnFlip() },
     *         { identity(3).viewRowFlip(), null } };
     * System.out.println(&quot;\n&quot; + make(parts3));
     * </pre>
     * 
     * </td>
     * <td><tt>9&nbsp;x&nbsp;6&nbsp;matrix<br>
     1&nbsp;0&nbsp;0&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;1&nbsp;0&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;1&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;0&nbsp;1<br>
     0&nbsp;0&nbsp;0&nbsp;0&nbsp;1&nbsp;0<br>
     0&nbsp;0&nbsp;0&nbsp;1&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;1&nbsp;0&nbsp;0&nbsp;0<br>
     0&nbsp;1&nbsp;0&nbsp;0&nbsp;0&nbsp;0<br>
     1&nbsp;0&nbsp;0&nbsp;0&nbsp;0&nbsp;0 </tt></td>
     * </tr>
     * <tr align="left" valign="top">
     * <td>
     * 
     * <pre>
     * LongMatrix2D A = ascending(2, 2);
     * LongMatrix2D B = descending(2, 2);
     * LongMatrix2D _ = null;
     * 
     * LongMatrix2D[][] parts4 = { { A, _, A, _ }, { _, A, _, B } };
     * System.out.println(&quot;\n&quot; + make(parts4));
     * </pre>
     * 
     * </td>
     * <td><tt>4&nbsp;x&nbsp;8&nbsp;matrix<br>
     1&nbsp;2&nbsp;0&nbsp;0&nbsp;1&nbsp;2&nbsp;0&nbsp;0<br>
     3&nbsp;4&nbsp;0&nbsp;0&nbsp;3&nbsp;4&nbsp;0&nbsp;0<br>
     0&nbsp;0&nbsp;1&nbsp;2&nbsp;0&nbsp;0&nbsp;3&nbsp;2<br>
     0&nbsp;0&nbsp;3&nbsp;4&nbsp;0&nbsp;0&nbsp;1&nbsp;0 </tt></td>
     * </tr>
     * <tr align="left" valign="top">
     * <td>
     * 
     * <pre>
     * LongMatrix2D[][] parts2 = { { null, make(2, 2, 1), null }, { make(4, 4, 2), null, make(4, 3, 3) },
     *         { null, make(2, 3, 4), null } };
     * System.out.println(&quot;\n&quot; + Factory2D.make(parts2));
     * </pre>
     * 
     * </td>
     * <td><tt>IllegalArgumentException<br>
     A[0,1].columns != A[2,1].columns<br>
     (2 != 3)</tt></td>
     * </tr>
     * </table>
     * 
     * @throws IllegalArgumentException
     *             subject to the conditions outlined above.
     */
    public LongMatrix2D compose(LongMatrix2D[][] parts) {
        checkRectangularShape(parts);
        int rows = parts.length;
        int columns = 0;
        if (parts.length > 0)
            columns = parts[0].length;
        LongMatrix2D empty = make(0, 0);

        if (rows == 0 || columns == 0)
            return empty;

        // determine maximum column width of each column
        int[] maxWidths = new int[columns];
        for (int column = columns; --column >= 0;) {
            int maxWidth = 0;
            for (int row = rows; --row >= 0;) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    int width = part.columns();
                    if (maxWidth > 0 && width > 0 && width != maxWidth)
                        throw new IllegalArgumentException("Different number of columns.");
                    maxWidth = Math.max(maxWidth, width);
                }
            }
            maxWidths[column] = maxWidth;
        }

        // determine row height of each row
        int[] maxHeights = new int[rows];
        for (int row = rows; --row >= 0;) {
            int maxHeight = 0;
            for (int column = columns; --column >= 0;) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    int height = part.rows();
                    if (maxHeight > 0 && height > 0 && height != maxHeight)
                        throw new IllegalArgumentException("Different number of rows.");
                    maxHeight = Math.max(maxHeight, height);
                }
            }
            maxHeights[row] = maxHeight;
        }

        // shape of result
        int resultRows = 0;
        for (int row = rows; --row >= 0;)
            resultRows += maxHeights[row];
        int resultCols = 0;
        for (int column = columns; --column >= 0;)
            resultCols += maxWidths[column];

        LongMatrix2D matrix = make(resultRows, resultCols);

        // copy
        int r = 0;
        for (int row = 0; row < rows; row++) {
            int c = 0;
            for (int column = 0; column < columns; column++) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    matrix.viewPart(r, c, part.rows(), part.columns()).assign(part);
                }
                c += maxWidths[column];
            }
            r += maxHeights[row];
        }

        return matrix;
    }

    /**
     * Constructs a diagonal block matrix from the given parts (the <i>direct
     * sum</i> of two matrices). That is the concatenation
     * 
     * <pre>
     *   A 0
     *   0 B
     * 
     * </pre>
     * 
     * (The direct sum has <tt>A.rows()+B.rows()</tt> rows and
     * <tt>A.columns()+B.columns()</tt> columns). Cells are copied.
     * 
     * @return a new matrix which is the direct sum.
     */
    public LongMatrix2D composeDiagonal(LongMatrix2D A, LongMatrix2D B) {
        int ar = A.rows();
        int ac = A.columns();
        int br = B.rows();
        int bc = B.columns();
        LongMatrix2D sum = make(ar + br, ac + bc);
        sum.viewPart(0, 0, ar, ac).assign(A);
        sum.viewPart(ar, ac, br, bc).assign(B);
        return sum;
    }

    /**
     * Constructs a diagonal block matrix from the given parts. The
     * concatenation has the form
     * 
     * <pre>
     *   A 0 0
     *   0 B 0
     *   0 0 C
     * 
     * </pre>
     * 
     * from the given parts. Cells are copied.
     */
    public LongMatrix2D composeDiagonal(LongMatrix2D A, LongMatrix2D B, LongMatrix2D C) {
        LongMatrix2D diag = make(A.rows() + B.rows() + C.rows(), A.columns() + B.columns() + C.columns());
        diag.viewPart(0, 0, A.rows(), A.columns()).assign(A);
        diag.viewPart(A.rows(), A.columns(), B.rows(), B.columns()).assign(B);
        diag.viewPart(A.rows() + B.rows(), A.columns() + B.columns(), C.rows(), C.columns()).assign(C);
        return diag;
    }

    public LongMatrix2D composeBidiagonal(LongMatrix2D A, LongMatrix2D B) {
        int ar = A.rows();
        int ac = A.columns();
        int br = B.rows();
        int bc = B.columns();
        LongMatrix2D sum = make(ar + br - 1, ac + bc);
        sum.viewPart(0, 0, ar, ac).assign(A);
        sum.viewPart(ar - 1, ac, br, bc).assign(B);
        return sum;
    }

    /**
     * Splits a block matrix into its constituent blocks; Copies blocks of a
     * matrix into the given parts. The inverse to method
     * {@link #compose(LongMatrix2D[][])}.
     * <p>
     * All matrices of a given column within <tt>parts</tt> must have the same
     * number of columns. All matrices of a given row within <tt>parts</tt> must
     * have the same number of rows. Otherwise an
     * <tt>IllegalArgumentException</tt> is thrown. Note that <tt>null</tt>s
     * within <tt>parts[row,col]</tt> are an exception to this rule: they are
     * ignored. Cells are copied. Example:
     * <table border="1" cellspacing="0">
     * <tr align="left" valign="top">
     * <td><tt>Code</tt></td>
     * <td><tt>matrix</tt></td>
     * <td><tt>--&gt; parts </tt></td>
     * </tr>
     * <tr align="left" valign="top">
     * <td>
     * 
     * <pre>
     *   LongMatrix2D matrix = ... ;
     *   LongMatrix2D _ = null;
     *   LongMatrix2D A,B,C,D;
     *   A = make(2,2); B = make (4,4);
     *   C = make(4,3); D = make (2,2);
     *   LongMatrix2D[][] parts = 
     *   {
     *      { _, A, _ },
     *      { B, _, C },
     *      { _, D, _ }
     *   };
     *   decompose(parts,matrix);
     *   System.out.println(&quot;\nA = &quot;+A);
     *   System.out.println(&quot;\nB = &quot;+B);
     *   System.out.println(&quot;\nC = &quot;+C);
     *   System.out.println(&quot;\nD = &quot;+D);
     * 
     * </pre>
     * 
     * </td>
     * <td><tt>8&nbsp;x&nbsp;9&nbsp;matrix<br>
     9&nbsp;9&nbsp;9&nbsp;9&nbsp;1&nbsp;1&nbsp;9&nbsp;9&nbsp;9<br>
     9&nbsp;9&nbsp;9&nbsp;9&nbsp;1&nbsp;1&nbsp;9&nbsp;9&nbsp;9<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;9&nbsp;9&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;9&nbsp;9&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;9&nbsp;9&nbsp;3&nbsp;3&nbsp;3<br>
     2&nbsp;2&nbsp;2&nbsp;2&nbsp;9&nbsp;9&nbsp;3&nbsp;3&nbsp;3<br>
     9&nbsp;9&nbsp;9&nbsp;9&nbsp;4&nbsp;4&nbsp;9&nbsp;9&nbsp;9<br>
     9&nbsp;9&nbsp;9&nbsp;9&nbsp;4&nbsp;4&nbsp;9&nbsp;9&nbsp;9</tt></td>
     * <td>
     * <p>
     * <tt>A = 2&nbsp;x&nbsp;2&nbsp;matrix<br>
     1&nbsp;1<br>
     1&nbsp;1</tt>
     * </p>
     * <p>
     * <tt>B = 4&nbsp;x&nbsp;4&nbsp;matrix<br>
     2&nbsp;2&nbsp;2&nbsp;2<br>
     2&nbsp;2&nbsp;2&nbsp;2<br>
     2&nbsp;2&nbsp;2&nbsp;2<br>
     2&nbsp;2&nbsp;2&nbsp;2</tt>
     * </p>
     * <p>
     * <tt>C = 4&nbsp;x&nbsp;3&nbsp;matrix<br>
     3&nbsp;3&nbsp;3<br>
     3&nbsp;3&nbsp;3<br>
     </tt><tt>3&nbsp;3&nbsp;3<br>
     </tt><tt>3&nbsp;3&nbsp;3</tt>
     * </p>
     * <p>
     * <tt>D = 2&nbsp;x&nbsp;2&nbsp;matrix<br>
     4&nbsp;4<br>
     4&nbsp;4</tt>
     * </p>
     * </td>
     * </tr>
     * </table>
     * 
     * @throws IllegalArgumentException
     *             subject to the conditions outlined above.
     */
    public void decompose(LongMatrix2D[][] parts, LongMatrix2D matrix) {
        checkRectangularShape(parts);
        int rows = parts.length;
        int columns = 0;
        if (parts.length > 0)
            columns = parts[0].length;
        if (rows == 0 || columns == 0)
            return;

        // determine maximum column width of each column
        int[] maxWidths = new int[columns];
        for (int column = columns; --column >= 0;) {
            int maxWidth = 0;
            for (int row = rows; --row >= 0;) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    int width = part.columns();
                    if (maxWidth > 0 && width > 0 && width != maxWidth)
                        throw new IllegalArgumentException("Different number of columns.");
                    maxWidth = Math.max(maxWidth, width);
                }
            }
            maxWidths[column] = maxWidth;
        }

        // determine row height of each row
        int[] maxHeights = new int[rows];
        for (int row = rows; --row >= 0;) {
            int maxHeight = 0;
            for (int column = columns; --column >= 0;) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    int height = part.rows();
                    if (maxHeight > 0 && height > 0 && height != maxHeight)
                        throw new IllegalArgumentException("Different number of rows.");
                    maxHeight = Math.max(maxHeight, height);
                }
            }
            maxHeights[row] = maxHeight;
        }

        // shape of result parts
        int resultRows = 0;
        for (int row = rows; --row >= 0;)
            resultRows += maxHeights[row];
        int resultCols = 0;
        for (int column = columns; --column >= 0;)
            resultCols += maxWidths[column];

        if (matrix.rows() < resultRows || matrix.columns() < resultCols)
            throw new IllegalArgumentException("Parts larger than matrix.");

        // copy
        int r = 0;
        for (int row = 0; row < rows; row++) {
            int c = 0;
            for (int column = 0; column < columns; column++) {
                LongMatrix2D part = parts[row][column];
                if (part != null) {
                    part.assign(matrix.viewPart(r, c, part.rows(), part.columns()));
                }
                c += maxWidths[column];
            }
            r += maxHeights[row];
        }

    }

    /**
     * Demonstrates usage of this class.
     */
    public void demo1() {
        System.out.println("\n\n");
        LongMatrix2D[][] parts1 = { { null, make(2, 2, 1), null }, { make(4, 4, 2), null, make(4, 3, 3) },
                { null, make(2, 2, 4), null } };
        System.out.println("\n" + compose(parts1));
        // System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts1).toString()));

        /*
         * // illegal 2 != 3 LongMatrix2D[][] parts2 = { { null, make(2,2,1),
         * null }, { make(4,4,2), null, make(4,3,3) }, { null, make(2,3,4), null } };
         * System.out.println("\n"+make(parts2));
         */

        LongMatrix2D[][] parts3 = { { identity(3), null, }, { null, identity(3).viewColumnFlip() },
                { identity(3).viewRowFlip(), null } };
        System.out.println("\n" + compose(parts3));
        // System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts3).toString()));

        LongMatrix2D A = ascending(2, 2);
        LongMatrix2D B = descending(2, 2);

        LongMatrix2D[][] parts4 = { { A, null, A, null }, { null, A, null, B } };
        System.out.println("\n" + compose(parts4));
        // System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts4).toString()));

    }

    /**
     * Demonstrates usage of this class.
     */
    public void demo2() {
        System.out.println("\n\n");
        LongMatrix2D matrix;
        LongMatrix2D A, B, C, D, E, F, G;

        A = make(2, 2, 1);
        B = make(4, 4, 2);
        C = make(4, 3, 3);
        D = make(2, 2, 4);
        LongMatrix2D[][] parts1 = { { null, A, null }, { B, null, C }, { null, D, null } };
        matrix = compose(parts1);
        System.out.println("\n" + matrix);

        A.assign(9);
        B.assign(9);
        C.assign(9);
        D.assign(9);
        decompose(parts1, matrix);
        System.out.println(A);
        System.out.println(B);
        System.out.println(C);
        System.out.println(D);
        // System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts1).toString()));

        /*
         * // illegal 2 != 3 LongMatrix2D[][] parts2 = { { null, make(2,2,1),
         * null }, { make(4,4,2), null, make(4,3,3) }, { null, make(2,3,4), null } };
         * System.out.println("\n"+Factory2D.make(parts2));
         */

        /*
         * LongMatrix2D[][] parts3 = { { identity(3), null, }, { null,
         * identity(3).viewColumnFlip() }, { identity(3).viewRowFlip(), null } };
         * System.out.println("\n"+make(parts3));
         * //System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts3).toString()));
         * 
         * LongMatrix2D A = ascending(2,2); LongMatrix2D B =
         * descending(2,2); LongMatrix2D _ = null;
         * 
         * LongMatrix2D[][] parts4 = { { A, _, A, _ }, { _, A, _, B } };
         * System.out.println("\n"+make(parts4));
         * //System.out.println("\n"+cern.colt.matrixpattern.Converting.toHTML(make(parts4).toString()));
         */
    }

    /**
     * Constructs a matrix with cells having descending values. For debugging
     * purposes. Example:
     * 
     * <pre>
     *   5 4 3 
     *   2 1 0
     * 
     * </pre>
     */
    public LongMatrix2D descending(int rows, int columns) {
        LongMatrix2D matrix = make(rows, columns);
        int v = 0;
        for (int row = rows; --row >= 0;) {
            for (int column = columns; --column >= 0;) {
                matrix.setQuick(row, column, v++);
            }
        }
        return matrix;
    }

    /**
     * Constructs a new diagonal matrix whose diagonal elements are the elements
     * of <tt>vector</tt>. Cells values are copied. The new matrix is not a
     * view. Example:
     * 
     * <pre>
     *   5 4 3 --&gt;
     *   5 0 0
     *   0 4 0
     *   0 0 3
     * 
     * </pre>
     * 
     * @return a new matrix.
     */
    public LongMatrix2D diagonal(LongMatrix1D vector) {
        int size = (int) vector.size();
        LongMatrix2D diag = make(size, size);
        for (int i = size; --i >= 0;) {
            diag.setQuick(i, i, vector.getQuick(i));
        }
        return diag;
    }

    /**
     * Constructs a new diagonal matrix whose diagonal elements are the elements
     * of <tt>vector</tt>. Cells values are copied. The new matrix is not a
     * view. Example:
     * 
     * <pre>
     *   5 4 3 --&gt;
     *   5 0 0
     *   0 4 0
     *   0 0 3
     * 
     * </pre>
     * 
     * @return a new matrix.
     */
    public LongMatrix2D diagonal(int[] vector) {
        int size = vector.length;
        LongMatrix2D diag = make(size, size);
        for (int i = 0; i < size; i++) {
            diag.setQuick(i, i, vector[i]);
        }
        return diag;
    }

    /**
     * Constructs a new vector consisting of the diagonal elements of <tt>A</tt>
     * . Cells values are copied. The new vector is not a view. Example:
     * 
     * <pre>
     *   5 0 0 9
     *   0 4 0 9
     *   0 0 3 9
     *   --&gt; 5 4 3
     * 
     * </pre>
     * 
     * @param A
     *            the matrix, need not be square.
     * @return a new vector.
     */
    public LongMatrix1D diagonal(LongMatrix2D A) {
        int min = Math.min(A.rows(), A.columns());
        LongMatrix1D diag = make1D(min);
        for (int i = min; --i >= 0;) {
            diag.setQuick(i, A.getQuick(i, i));
        }
        return diag;
    }

    /**
     * Constructs an identity matrix (having ones on the diagonal and zeros
     * elsewhere).
     */
    public LongMatrix2D identity(int rowsAndColumns) {
        LongMatrix2D matrix = make(rowsAndColumns, rowsAndColumns);
        for (int i = rowsAndColumns; --i >= 0;) {
            matrix.setQuick(i, i, 1);
        }
        return matrix;
    }

    /**
     * Constructs a matrix with the given cell values. <tt>values</tt> is
     * required to have the form <tt>values[row][column]</tt> and have exactly
     * the same number of columns in every row.
     * <p>
     * The values are copied. So subsequent changes in <tt>values</tt> are not
     * reflected in the matrix, and vice-versa.
     * 
     * @param values
     *            The values to be filled into the new matrix.
     * @throws IllegalArgumentException
     *             if
     *             <tt>for any 1 &lt;= row &lt; values.length: values[row].length != values[row-1].length</tt>
     *             .
     */
    public LongMatrix2D make(long[][] values) {
        if (this == sparse)
            return new SparseLongMatrix2D(values);
        else
            return new DenseLongMatrix2D(values);
    }

    /**
     * Construct a matrix from a one-dimensional column-major packed array, ala
     * Fortran. Has the form
     * <tt>matrix.get(row,column) == values[row + column*rows]</tt>. The values
     * are copied.
     * 
     * @param values
     *            One-dimensional array of doubles, packed by columns (ala
     *            Fortran).
     * @param rows
     *            the number of rows.
     * @exception IllegalArgumentException
     *                <tt>values.length</tt> must be a multiple of <tt>rows</tt>
     *                .
     */
    public LongMatrix2D make(int values[], int rows) {
        int columns = (rows != 0 ? values.length / rows : 0);
        if (rows * columns != values.length)
            throw new IllegalArgumentException("Array length must be a multiple of m.");

        LongMatrix2D matrix = make(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                matrix.setQuick(row, column, values[row + column * rows]);
            }
        }
        return matrix;
    }

    /**
     * Constructs a matrix with the given shape, each cell initialized with
     * zero.
     */
    public LongMatrix2D make(int rows, int columns) {
        if (this == sparse)
            return new SparseLongMatrix2D(rows, columns);
        if (this == rowCompressed)
            return new SparseRCLongMatrix2D(rows, columns);
        // if (this==rowCompressedModified) return new
        // RCMLongMatrix2D(rows,columns);
        else
            return new DenseLongMatrix2D(rows, columns);
    }

    /**
     * Constructs a matrix with the given shape, each cell initialized with the
     * given value.
     */
    public LongMatrix2D make(int rows, int columns, long initialValue) {
        if (initialValue == 0)
            return make(rows, columns);
        return make(rows, columns).assign(initialValue);
    }

    /**
     * Constructs a 1d matrix of the right dynamic type.
     */
    protected LongMatrix1D make1D(int size) {
        return make(0, 0).like1D(size);
    }

    /**
     * Constructs a matrix with uniformly distributed values in <tt>(0,1)</tt>
     * (exclusive).
     */
    public LongMatrix2D random(int rows, int columns) {
        return make(rows, columns).assign(cern.jet.math.tlong.LongFunctions.random());
    }

    /**
     * C = A||A||..||A; Constructs a new matrix which is duplicated both along
     * the row and column dimension. Example:
     * 
     * <pre>
     *   0 1
     *   2 3
     *   repeat(2,3) --&gt;
     *   0 1 0 1 0 1
     *   2 3 2 3 2 3
     *   0 1 0 1 0 1
     *   2 3 2 3 2 3
     * 
     * </pre>
     */
    public LongMatrix2D repeat(LongMatrix2D A, int rowRepeat, int columnRepeat) {
        int r = A.rows();
        int c = A.columns();
        LongMatrix2D matrix = make(r * rowRepeat, c * columnRepeat);
        for (int i = rowRepeat; --i >= 0;) {
            for (int j = columnRepeat; --j >= 0;) {
                matrix.viewPart(r * i, c * j, r, c).assign(A);
            }
        }
        return matrix;
    }

    /**
     * Constructs a randomly sampled matrix with the given shape. Randomly picks
     * exactly <tt>Math.round(rows*columns*nonZeroFraction)</tt> cells and
     * initializes them to <tt>value</tt>, all the rest will be initialized to
     * zero. Note that this is not the same as setting each cell with
     * probability <tt>nonZeroFraction</tt> to <tt>value</tt>. Note: The random
     * seed is a constant.
     * 
     * @throws IllegalArgumentException
     *             if <tt>nonZeroFraction < 0 || nonZeroFraction > 1</tt>.
     * @see cern.jet.random.tdouble.sampling.DoubleRandomSamplingAssistant
     */
    public LongMatrix2D sample(int rows, int columns, int value, int nonZeroFraction) {
        LongMatrix2D matrix = make(rows, columns);
        sample(matrix, value, nonZeroFraction);
        return matrix;
    }

    /**
     * Modifies the given matrix to be a randomly sampled matrix. Randomly picks
     * exactly <tt>Math.round(rows*columns*nonZeroFraction)</tt> cells and
     * initializes them to <tt>value</tt>, all the rest will be initialized to
     * zero. Note that this is not the same as setting each cell with
     * probability <tt>nonZeroFraction</tt> to <tt>value</tt>. Note: The random
     * seed is a constant.
     * 
     * @throws IllegalArgumentException
     *             if <tt>nonZeroFraction < 0 || nonZeroFraction > 1</tt>.
     * @see cern.jet.random.tdouble.sampling.DoubleRandomSamplingAssistant
     */
    public LongMatrix2D sample(LongMatrix2D matrix, int value, int nonZeroFraction) {
        int rows = matrix.rows();
        int columns = matrix.columns();
        double epsilon = 1e-09;
        if (nonZeroFraction < 0 - epsilon || nonZeroFraction > 1 + epsilon)
            throw new IllegalArgumentException();
        if (nonZeroFraction < 0)
            nonZeroFraction = 0;
        if (nonZeroFraction > 1)
            nonZeroFraction = 1;

        matrix.assign(0);

        int size = rows * columns;
        int n = Math.round(size * nonZeroFraction);
        if (n == 0)
            return matrix;

        cern.jet.random.tdouble.sampling.DoubleRandomSamplingAssistant sampler = new cern.jet.random.tdouble.sampling.DoubleRandomSamplingAssistant(
                n, size, new MersenneTwister());
        for (int i = 0; i < size; i++) {
            if (sampler.sampleNextElement()) {
                int row = (i / columns);
                int column = (i % columns);
                matrix.set(row, column, value);
            }
        }

        return matrix;
    }
}
