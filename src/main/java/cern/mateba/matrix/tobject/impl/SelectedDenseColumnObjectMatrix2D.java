/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.matrix.tobject.impl;

import cern.mateba.matrix.AbstractMatrix2D;
import cern.mateba.matrix.tobject.ObjectMatrix1D;
import cern.mateba.matrix.tobject.ObjectMatrix2D;

import java.io.Serial;

/**
 * Selection view on dense 2-d matrices holding <tt>Object</tt> elements. First
 * see the <a href="package-summary.html">package summary</a> and javadoc <a
 * href="package-tree.html">tree view</a> to get the broad picture.
 * <p>
 * <b>Implementation:</b>
 * <p>
 * Objects of this class are typically constructed via <tt>viewIndexes</tt>
 * methods on some source matrix. The interface introduced in abstract super
 * classes defines everything a user can do. From a user point of view there is
 * nothing special about this class; it presents the same functionality with the
 * same signatures and semantics as its abstract superclass(es) while
 * introducing no additional functionality. Thus, this class need not be visible
 * to users. By the way, the same principle applies to concrete DenseXXX and
 * SparseXXX classes: they presents the same functionality with the same
 * signatures and semantics as abstract superclass(es) while introducing no
 * additional functionality. Thus, they need not be visible to users, either.
 * Factory methods could hide all these concrete types.
 * <p>
 * This class uses no delegation. Its instances point directly to the data. Cell
 * addressing overhead is 1 additional int addition and 2 additional array index
 * accesses per get/set.
 * <p>
 * Note that this implementation is not synchronized.
 * <p>
 * <b>Memory requirements:</b>
 * <p>
 * <tt>memory [bytes] = 4*(rowIndexes.length+columnIndexes.length)</tt>. Thus,
 * an index view with 1000 x 1000 indexes additionally uses 8 KB.
 * <p>
 * <b>Time complexity:</b>
 * <p>
 * Depends on the parent view holding cells.
 * <p>
 *
 * @author wolfgang.hoschek@cern.ch
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * @version 1.1, 08/22/2007
 */
class SelectedDenseColumnObjectMatrix2D extends ObjectMatrix2D {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 410672107554560480L;

    /**
     * The elements of this matrix.
     */
    protected Object[] elements;

    /**
     * The offsets of the visible cells of this matrix.
     */
    protected int[] rowOffsets;

    protected int[] columnOffsets;

    /**
     * The offset.
     */
    protected int offset;

    /**
     * Constructs a matrix view with the given parameters.
     *
     * @param elements      the cells.
     * @param rowOffsets    The row offsets of the cells that shall be visible.
     * @param columnOffsets The column offsets of the cells that shall be visible.
     * @param offset
     */
    protected SelectedDenseColumnObjectMatrix2D(Object[] elements, int[] rowOffsets, int[] columnOffsets, int offset) {
        this(rowOffsets.length, columnOffsets.length, elements, 0, 0, 1, 1, rowOffsets, columnOffsets, offset);
    }

    /**
     * Constructs a matrix view with the given parameters.
     *
     * @param rows          the number of rows the matrix shall have.
     * @param columns       the number of columns the matrix shall have.
     * @param elements      the cells.
     * @param rowZero       the position of the first element.
     * @param columnZero    the position of the first element.
     * @param rowStride     the number of elements between two rows, i.e.
     *                      <tt>index(i+1,j)-index(i,j)</tt>.
     * @param columnStride  the number of elements between two columns, i.e.
     *                      <tt>index(i,j+1)-index(i,j)</tt>.
     * @param rowOffsets    The row offsets of the cells that shall be visible.
     * @param columnOffsets The column offsets of the cells that shall be visible.
     * @param offset
     */
    protected SelectedDenseColumnObjectMatrix2D(int rows, int columns, Object[] elements, int rowZero, int columnZero,
                                                int rowStride, int columnStride, int[] rowOffsets, int[] columnOffsets, int offset) {
        // be sure parameters are valid, we do not check...
        setUp(rows, columns, rowZero, columnZero, rowStride, columnStride);

        this.elements = elements;
        this.rowOffsets = rowOffsets;
        this.columnOffsets = columnOffsets;
        this.offset = offset;

        this.isNoView = false;
    }

    public Object[] elements() {
        return elements;
    }

    /**
     * Returns the matrix cell value at coordinate <tt>[row,column]</tt>.
     *
     * <p>
     * Provided with invalid parameters this method may return invalid objects
     * without throwing any exception. <b>You should only use this method when
     * you are absolutely sure that the coordinate is within bounds.</b>
     * Precondition (unchecked):
     * <tt>0 &lt;= column &lt; columns() && 0 &lt;= row &lt; rows()</tt>.
     *
     * @param row    the index of the row-coordinate.
     * @param column the index of the column-coordinate.
     * @return the value at the specified coordinate.
     */

    public Object getQuick(int row, int column) {
        // if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
        // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
        // return elements[index(row,column)];
        // manually inlined:
        return elements[offset + rowOffsets[rowZero + row * rowStride]
            + columnOffsets[columnZero + column * columnStride]];
    }

    /**
     * Returns the position of the given coordinate within the (virtual or
     * non-virtual) internal 1-dimensional array.
     *
     * @param row    the index of the row-coordinate.
     * @param column the index of the column-coordinate.
     */

    public long index(int row, int column) {
        // return this.offset + super.index(row,column);
        // manually inlined:
        return this.offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride];
    }

    /**
     * Construct and returns a new empty matrix <i>of the same dynamic type</i>
     * as the receiver, having the specified number of rows and columns. For
     * example, if the receiver is an instance of type
     * <tt>DenseColObjectMatrix2D</tt> the new matrix must also be of type
     * <tt>DenseColObjectMatrix2D</tt>, if the receiver is an instance of type
     * <tt>SparseColObjectMatrix2D</tt> the new matrix must also be of type
     * <tt>SparseColObjectMatrix2D</tt>, etc. In general, the new matrix should
     * have internal parametrization as similar as possible.
     *
     * @param rows    the number of rows the matrix shall have.
     * @param columns the number of columns the matrix shall have.
     * @return a new empty matrix of the same dynamic type.
     */

    public ObjectMatrix2D like(int rows, int columns) {
        return new DenseColumnObjectMatrix2D(rows, columns);
    }

    /**
     * Construct and returns a new 1-d matrix <i>of the corresponding dynamic
     * type</i>, entirelly independent of the receiver. For example, if the
     * receiver is an instance of type <tt>DenseColObjectMatrix2D</tt> the new
     * matrix must be of type <tt>DenseObjectMatrix1D</tt>, if the receiver is an
     * instance of type <tt>SparseColObjectMatrix2D</tt> the new matrix must be of
     * type <tt>SparseObjectMatrix1D</tt>, etc.
     *
     * @param size the number of cells the matrix shall have.
     * @return a new matrix of the corresponding dynamic type.
     */

    public ObjectMatrix1D like1D(int size) {
        return new DenseObjectMatrix1D(size);
    }

    /**
     * Sets the matrix cell at coordinate <tt>[row,column]</tt> to the specified
     * value.
     *
     * <p>
     * Provided with invalid parameters this method may access illegal indexes
     * without throwing any exception. <b>You should only use this method when
     * you are absolutely sure that the coordinate is within bounds.</b>
     * Precondition (unchecked):
     * <tt>0 &lt;= column &lt; columns() && 0 &lt;= row &lt; rows()</tt>.
     *
     * @param row    the index of the row-coordinate.
     * @param column the index of the column-coordinate.
     * @param value  the value to be filled into the specified cell.
     */

    public void setQuick(int row, int column, Object value) {
        // if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
        // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
        // elements[index(row,column)] = value;
        // manually inlined:
        elements[offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride]] = value;
    }

    /**
     * Returns a vector obtained by stacking the columns of the matrix on top of
     * one another.
     *
     * @return
     */

    public ObjectMatrix1D vectorize() {
        DenseObjectMatrix1D v = new DenseObjectMatrix1D((int) size());
        int idx = 0;
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                v.setQuick(idx++, getQuick(c, r));
            }
        }
        return v;
    }

    /**
     * Constructs and returns a new <i>slice view</i> representing the rows of
     * the given column. The returned view is backed by this matrix, so changes
     * in the returned view are reflected in this matrix, and vice-versa. To
     * obtain a slice view on subranges, construct a sub-ranging view (
     * <tt>viewPart(...)</tt>), then apply this method to the sub-range view.
     * <p>
     * <b>Example:</b>
     * <table border="0">
     * <tr nowrap>
     * <td valign="top">2 x 3 matrix: <br>
     * 1, 2, 3<br>
     * 4, 5, 6</td>
     * <td>viewColumn(0) ==></td>
     * <td valign="top">Matrix1D of size 2:<br>
     * 1, 4</td>
     * </tr>
     * </table>
     *
     * @param the column to fix.
     * @return a new slice view.
     * @throws IllegalArgumentException if <tt>column < 0 || column >= columns()</tt>.
     * @see #viewRow(int)
     */

    public ObjectMatrix1D viewColumn(int column) {
        checkColumn(column);
        int viewSize = this.rows;
        int viewZero = this.rowZero;
        int viewStride = this.rowStride;
        int[] viewOffsets = this.rowOffsets;
        int viewOffset = this.offset + _columnOffset(_columnRank(column));
        return new SelectedDenseObjectMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
    }

    /**
     * Constructs and returns a new <i>slice view</i> representing the columns
     * of the given row. The returned view is backed by this matrix, so changes
     * in the returned view are reflected in this matrix, and vice-versa. To
     * obtain a slice view on subranges, construct a sub-ranging view (
     * <tt>viewPart(...)</tt>), then apply this method to the sub-range view.
     * <p>
     * <b>Example:</b>
     * <table border="0">
     * <tr nowrap>
     * <td valign="top">2 x 3 matrix: <br>
     * 1, 2, 3<br>
     * 4, 5, 6</td>
     * <td>viewRow(0) ==></td>
     * <td valign="top">Matrix1D of size 3:<br>
     * 1, 2, 3</td>
     * </tr>
     * </table>
     *
     * @param the row to fix.
     * @return a new slice view.
     * @throws IndexOutOfBoundsException if <tt>row < 0 || row >= rows()</tt>.
     * @see #viewColumn(int)
     */

    public ObjectMatrix1D viewRow(int row) {
        checkRow(row);
        int viewSize = this.columns;
        int viewZero = columnZero;
        int viewStride = this.columnStride;
        int[] viewOffsets = this.columnOffsets;
        int viewOffset = this.offset + _rowOffset(_rowRank(row));
        return new SelectedDenseObjectMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
    }

    /**
     * Returns the position of the given absolute rank within the (virtual or
     * non-virtual) internal 1-dimensional array. Default implementation.
     * Override, if necessary.
     *
     * @param rank the absolute rank of the element.
     * @return the position.
     */

    protected int _columnOffset(int absRank) {
        return columnOffsets[absRank];
    }

    /**
     * Returns the position of the given absolute rank within the (virtual or
     * non-virtual) internal 1-dimensional array. Default implementation.
     * Override, if necessary.
     *
     * @param rank the absolute rank of the element.
     * @return the position.
     */

    protected int _rowOffset(int absRank) {
        return rowOffsets[absRank];
    }

    /**
     * Returns <tt>true</tt> if both matrices share common cells. More formally,
     * returns <tt>true</tt> if <tt>other != null</tt> and at least one of the
     * following conditions is met
     * <ul>
     * <li>the receiver is a view of the other matrix
     * <li>the other matrix is a view of the receiver
     * <li><tt>this == other</tt>
     * </ul>
     */

    protected boolean haveSharedCellsRaw(ObjectMatrix2D other) {
        if (other instanceof SelectedDenseColumnObjectMatrix2D otherMatrix) {
            return this.elements == otherMatrix.elements;
        } else if (other instanceof DenseColumnObjectMatrix2D otherMatrix) {
            return this.elements == otherMatrix.elements;
        }
        return false;
    }

    /**
     * Construct and returns a new 1-d matrix <i>of the corresponding dynamic
     * type</i>, sharing the same cells. For example, if the receiver is an
     * instance of type <tt>DenseColObjectMatrix2D</tt> the new matrix must be of
     * type <tt>DenseObjectMatrix1D</tt>, if the receiver is an instance of type
     * <tt>SparseColObjectMatrix2D</tt> the new matrix must be of type
     * <tt>SparseObjectMatrix1D</tt>, etc.
     *
     * @param size   the number of cells the matrix shall have.
     * @param zero   the index of the first element.
     * @param stride the number of indexes between any two elements, i.e.
     *               <tt>index(i+1)-index(i)</tt>.
     * @return a new matrix of the corresponding dynamic type.
     */

    protected ObjectMatrix1D like1D(int size, int zero, int stride) {
        throw new InternalError(); // this method is never called since
        // viewRow() and viewColumn are overridden
        // properly.
    }

    /**
     * Sets up a matrix with a given number of rows and columns.
     *
     * @param rows    the number of rows the matrix shall have.
     * @param columns the number of columns the matrix shall have.
     * @throws IllegalArgumentException if <tt>(double)columns*rows > Integer.MAX_VALUE</tt>.
     */

    protected void setUp(int rows, int columns) {
        super.setUp(rows, columns);
        this.rowStride = 1;
        this.columnStride = 1;
        this.offset = 0;
    }

    /**
     * Self modifying version of viewDice().
     */

    protected AbstractMatrix2D vDice() {
        super.vDice();
        // swap
        int[] tmp = rowOffsets;
        rowOffsets = columnOffsets;
        columnOffsets = tmp;

        // flips stay unaffected

        this.isNoView = false;
        return this;
    }

    /**
     * Construct and returns a new selection view.
     *
     * @param rowOffsets    the offsets of the visible elements.
     * @param columnOffsets the offsets of the visible elements.
     * @return a new view.
     */

    protected ObjectMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets) {
        return new SelectedDenseColumnObjectMatrix2D(this.elements, rowOffsets, columnOffsets, this.offset);
    }
}
