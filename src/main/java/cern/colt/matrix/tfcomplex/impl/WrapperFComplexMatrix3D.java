/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.colt.matrix.tfcomplex.impl;

import java.util.concurrent.Future;

import cern.colt.matrix.tfcomplex.FComplexMatrix1D;
import cern.colt.matrix.tfcomplex.FComplexMatrix2D;
import cern.colt.matrix.tfcomplex.FComplexMatrix3D;
import cern.colt.matrix.tfloat.FloatMatrix3D;
import cern.colt.matrix.tfloat.impl.DenseLargeFloatMatrix3D;
import edu.emory.mathcs.utils.pc.ConcurrencyUtils;

/**
 * 3-d matrix holding <tt>complex</tt> elements; either a view wrapping another
 * matrix or a matrix whose views are wrappers.
 * 
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public class WrapperFComplexMatrix3D extends FComplexMatrix3D {
    private static final long serialVersionUID = 1L;
    /*
     * The elements of the matrix.
     */
    protected FComplexMatrix3D content;

    public WrapperFComplexMatrix3D(FComplexMatrix3D newContent) {
        if (newContent != null)
            setUp(newContent.slices(), newContent.rows(), newContent.columns());
        this.content = newContent;
    }

    public Object elements() {
        return content.elements();
    }

    /**
     * Computes the 2D discrete Fourier transform (DFT) of each slice of this
     * matrix.
     */
    public void fft2Slices() {
        if (content instanceof DenseLargeFComplexMatrix3D) {
            if (this.isNoView == true) {
                ((DenseLargeFComplexMatrix3D) content).fft2Slices();
            } else {
                DenseLargeFComplexMatrix3D copy = (DenseLargeFComplexMatrix3D) copy();
                copy.fft2Slices();
                assign(copy);
            }
        } else {
            throw new IllegalArgumentException("This method is not supported");
        }
    }

    /**
     * Computes the 3D discrete Fourier transform (DFT) of this matrix.
     */
    public void fft3() {
        if (content instanceof DenseLargeFComplexMatrix3D) {
            if (this.isNoView == true) {
                ((DenseLargeFComplexMatrix3D) content).fft3();
            } else {
                DenseLargeFComplexMatrix3D copy = (DenseLargeFComplexMatrix3D) copy();
                copy.fft3();
                assign(copy);
            }
        } else {
            throw new IllegalArgumentException("This method is not supported");
        }
    }

    /**
     * Computes the 2D inverse of the discrete Fourier transform (IDFT) of each
     * slice of this matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     */
    public void ifft2Slices(final boolean scale) {
        if (content instanceof DenseLargeFComplexMatrix3D) {
            if (this.isNoView == true) {
                ((DenseLargeFComplexMatrix3D) content).ifft2Slices(scale);
            } else {
                DenseLargeFComplexMatrix3D copy = (DenseLargeFComplexMatrix3D) copy();
                copy.ifft2Slices(scale);
                assign(copy);
            }
        } else {
            throw new IllegalArgumentException("This method is not supported");
        }
    }

    /**
     * Computes the 3D inverse of the discrete Fourier transform (IDFT) of this
     * matrix.
     * 
     * @param scale
     *            if true then scaling is performed
     */
    public void ifft3(boolean scale) {
        if (content instanceof DenseLargeFComplexMatrix3D) {
            if (this.isNoView == true) {
                ((DenseLargeFComplexMatrix3D) content).ifft3(scale);
            } else {
                DenseLargeFComplexMatrix3D copy = (DenseLargeFComplexMatrix3D) copy();
                copy.ifft3(scale);
                assign(copy);
            }
        } else {
            throw new IllegalArgumentException("This method is not supported");
        }
    }

    public synchronized float[] getQuick(int slice, int row, int column) {
        return content.getQuick(slice, row, column);
    }

    public FComplexMatrix3D like(int slices, int rows, int columns) {
        return content.like(slices, rows, columns);
    }

    public synchronized void setQuick(int slice, int row, int column, float[] value) {
        content.setQuick(slice, row, column, value);
    }

    public synchronized void setQuick(int slice, int row, int column, float re, float im) {
        content.setQuick(slice, row, column, re, im);
    }

    public FComplexMatrix1D vectorize() {
        FComplexMatrix1D v = new DenseFComplexMatrix1D((int) size());
        int length = rows * columns;
        for (int s = 0; s < slices; s++) {
            v.viewPart(s * length, length).assign(viewSlice(s).vectorize());
        }
        return v;
    }

    public FComplexMatrix2D viewColumn(int column) {
        checkColumn(column);
        return new DelegateFComplexMatrix2D(this, 2, column);
    }

    public FComplexMatrix3D viewColumnFlip() {
        if (columns == 0)
            return this;
        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int slice, int row, int column) {
                return content.getQuick(slice, row, columns - 1 - column);
            }

            public synchronized void setQuick(int slice, int row, int column, float[] value) {
                content.setQuick(slice, row, columns - 1 - column, value);
            }

            public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                content.setQuick(slice, row, columns - 1 - column, re, im);
            }

            public synchronized float[] get(int slice, int row, int column) {
                return content.get(slice, row, columns - 1 - column);
            }

            public synchronized void set(int slice, int row, int column, float[] value) {
                content.set(slice, row, columns - 1 - column, value);
            }

            public synchronized void set(int slice, int row, int column, float re, float im) {
                content.set(slice, row, columns - 1 - column, re, im);
            }
        };
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix2D viewSlice(int slice) {
        checkSlice(slice);
        return new DelegateFComplexMatrix2D(this, 0, slice);
    }

    public FComplexMatrix3D viewSliceFlip() {
        if (slices == 0)
            return this;
        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int slice, int row, int column) {
                return content.getQuick(slices - 1 - slice, row, column);
            }

            public synchronized void setQuick(int slice, int row, int column, float[] value) {
                content.setQuick(slices - 1 - slice, row, column, value);
            }

            public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                content.setQuick(slices - 1 - slice, row, column, re, im);
            }

            public synchronized float[] get(int slice, int row, int column) {
                return content.get(slices - 1 - slice, row, column);
            }

            public synchronized void set(int slice, int row, int column, float[] value) {
                content.set(slices - 1 - slice, row, column, value);
            }

            public synchronized void set(int slice, int row, int column, float re, float im) {
                content.set(slices - 1 - slice, row, column, re, im);
            }
        };
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix3D viewDice(int axis0, int axis1, int axis2) {
        int d = 3;
        if (axis0 < 0 || axis0 >= d || axis1 < 0 || axis1 >= d || axis2 < 0 || axis2 >= d || axis0 == axis1
                || axis0 == axis2 || axis1 == axis2) {
            throw new IllegalArgumentException("Illegal Axes: " + axis0 + ", " + axis1 + ", " + axis2);
        }
        WrapperFComplexMatrix3D view = null;
        if (axis0 == 0 && axis1 == 1 && axis2 == 2) {
            view = new WrapperFComplexMatrix3D(this);
        } else if (axis0 == 1 && axis1 == 0 && axis2 == 2) {
            view = new WrapperFComplexMatrix3D(this) {
                private static final long serialVersionUID = 1L;

                public synchronized float[] getQuick(int slice, int row, int column) {
                    return content.getQuick(row, slice, column);
                }

                public synchronized void setQuick(int slice, int row, int column, float[] value) {
                    content.setQuick(row, slice, column, value);
                }

                public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                    content.setQuick(row, slice, column, re, im);
                }

                public synchronized float[] get(int slice, int row, int column) {
                    return content.get(row, slice, column);
                }

                public synchronized void set(int slice, int row, int column, float[] value) {
                    content.set(row, slice, column, value);
                }

                public synchronized void set(int slice, int row, int column, float re, float im) {
                    content.set(row, slice, column, re, im);
                }

            };
        } else if (axis0 == 1 && axis1 == 2 && axis2 == 0) {
            view = new WrapperFComplexMatrix3D(this) {
                private static final long serialVersionUID = 1L;

                public synchronized float[] getQuick(int slice, int row, int column) {
                    return content.getQuick(row, column, slice);
                }

                public synchronized void setQuick(int slice, int row, int column, float[] value) {
                    content.setQuick(row, column, slice, value);
                }

                public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                    content.setQuick(row, column, slice, re, im);
                }

                public synchronized float[] get(int slice, int row, int column) {
                    return content.get(row, column, slice);
                }

                public synchronized void set(int slice, int row, int column, float[] value) {
                    content.set(row, column, slice, value);
                }

                public synchronized void set(int slice, int row, int column, float re, float im) {
                    content.set(row, column, slice, re, im);
                }

            };
        } else if (axis0 == 2 && axis1 == 1 && axis2 == 0) {
            view = new WrapperFComplexMatrix3D(this) {
                private static final long serialVersionUID = 1L;

                public synchronized float[] getQuick(int slice, int row, int column) {
                    return content.getQuick(column, row, slice);
                }

                public synchronized void setQuick(int slice, int row, int column, float[] value) {
                    content.setQuick(column, row, slice, value);
                }

                public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                    content.setQuick(column, row, slice, re, im);
                }

                public synchronized float[] get(int slice, int row, int column) {
                    return content.get(column, row, slice);
                }

                public synchronized void set(int slice, int row, int column, float[] value) {
                    content.set(column, row, slice, value);
                }

                public synchronized void set(int slice, int row, int column, float re, float im) {
                    content.set(column, row, slice, re, im);
                }
            };
        } else if (axis0 == 2 && axis1 == 0 && axis2 == 1) {
            view = new WrapperFComplexMatrix3D(this) {
                private static final long serialVersionUID = 1L;

                public synchronized float[] getQuick(int slice, int row, int column) {
                    return content.getQuick(column, slice, row);
                }

                public synchronized void setQuick(int slice, int row, int column, float[] value) {
                    content.setQuick(column, slice, row, value);
                }

                public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                    content.setQuick(column, slice, row, re, im);
                }

                public synchronized float[] get(int slice, int row, int column) {
                    return content.get(column, slice, row);
                }

                public synchronized void set(int slice, int row, int column, float[] value) {
                    content.set(column, slice, row, value);
                }

                public synchronized void set(int slice, int row, int column, float re, float im) {
                    content.set(column, slice, row, re, im);
                }

            };
        }
        int[] shape = shape();
        view.slices = shape[axis0];
        view.rows = shape[axis1];
        view.columns = shape[axis2];
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix3D viewPart(final int slice, final int row, final int column, int depth, int height, int width) {
        checkBox(slice, row, column, depth, height, width);
        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int i, int j, int k) {
                return content.getQuick(slice + i, row + j, column + k);
            }

            public synchronized void setQuick(int i, int j, int k, float[] value) {
                content.setQuick(slice + i, row + j, column + k, value);
            }

            public synchronized void setQuick(int i, int j, int k, float re, float im) {
                content.setQuick(slice + i, row + j, column + k, re, im);
            }

            public synchronized float[] get(int i, int j, int k) {
                return content.get(slice + i, row + j, column + k);
            }

            public synchronized void set(int i, int j, int k, float[] value) {
                content.set(slice + i, row + j, column + k, value);
            }

            public synchronized void set(int i, int j, int k, float re, float im) {
                content.set(slice + i, row + j, column + k, re, im);
            }

        };
        view.slices = depth;
        view.rows = height;
        view.columns = width;
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix2D viewRow(int row) {
        checkRow(row);
        return new DelegateFComplexMatrix2D(this, 1, row);
    }

    public FComplexMatrix3D viewRowFlip() {
        if (rows == 0)
            return this;
        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int slice, int row, int column) {
                return content.getQuick(slice, rows - 1 - row, column);
            }

            public synchronized void setQuick(int slice, int row, int column, float[] value) {
                content.setQuick(slice, rows - 1 - row, column, value);
            }

            public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                content.setQuick(slice, rows - 1 - row, column, re, im);
            }

            public synchronized float[] get(int slice, int row, int column) {
                return content.get(slice, rows - 1 - row, column);
            }

            public synchronized void set(int slice, int row, int column, float[] value) {
                content.set(slice, rows - 1 - row, column, value);
            }

            public synchronized void set(int slice, int row, int column, float re, float im) {
                content.set(slice, rows - 1 - row, column, re, im);
            }
        };
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix3D viewSelection(int[] sliceIndexes, int[] rowIndexes, int[] columnIndexes) {
        // check for "all"
        if (sliceIndexes == null) {
            sliceIndexes = new int[slices];
            for (int i = slices; --i >= 0;)
                sliceIndexes[i] = i;
        }
        if (rowIndexes == null) {
            rowIndexes = new int[rows];
            for (int i = rows; --i >= 0;)
                rowIndexes[i] = i;
        }
        if (columnIndexes == null) {
            columnIndexes = new int[columns];
            for (int i = columns; --i >= 0;)
                columnIndexes[i] = i;
        }

        checkSliceIndexes(sliceIndexes);
        checkRowIndexes(rowIndexes);
        checkColumnIndexes(columnIndexes);
        final int[] six = sliceIndexes;
        final int[] rix = rowIndexes;
        final int[] cix = columnIndexes;

        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int i, int j, int k) {
                return content.getQuick(six[i], rix[j], cix[k]);
            }

            public synchronized void setQuick(int i, int j, int k, float[] value) {
                content.setQuick(six[i], rix[j], cix[k], value);
            }

            public synchronized void setQuick(int i, int j, int k, float re, float im) {
                content.setQuick(six[i], rix[j], cix[k], re, im);
            }

            public synchronized float[] get(int i, int j, int k) {
                return content.get(six[i], rix[j], cix[k]);
            }

            public synchronized void set(int i, int j, int k, float[] value) {
                content.set(six[i], rix[j], cix[k], value);
            }

            public synchronized void set(int i, int j, int k, float re, float im) {
                content.set(six[i], rix[j], cix[k], re, im);
            }

        };
        view.slices = sliceIndexes.length;
        view.rows = rowIndexes.length;
        view.columns = columnIndexes.length;
        view.isNoView = false;
        return view;
    }

    public FComplexMatrix3D viewStrides(final int _sliceStride, final int _rowStride, final int _columnStride) {
        if (_sliceStride <= 0 || _rowStride <= 0 || _columnStride <= 0)
            throw new IndexOutOfBoundsException("illegal stride");
        WrapperFComplexMatrix3D view = new WrapperFComplexMatrix3D(this) {
            private static final long serialVersionUID = 1L;

            public synchronized float[] getQuick(int slice, int row, int column) {
                return content.getQuick(_sliceStride * slice, _rowStride * row, _columnStride * column);
            }

            public synchronized void setQuick(int slice, int row, int column, float value[]) {
                content.setQuick(_sliceStride * slice, _rowStride * row, _columnStride * column, value);
            }

            public synchronized void setQuick(int slice, int row, int column, float re, float im) {
                content.setQuick(_sliceStride * slice, _rowStride * row, _columnStride * column, re, im);
            }

            public synchronized float[] get(int slice, int row, int column) {
                return content.get(_sliceStride * slice, _rowStride * row, _columnStride * column);
            }

            public synchronized void set(int slice, int row, int column, float[] value) {
                content.set(_sliceStride * slice, _rowStride * row, _columnStride * column, value);
            }

            public synchronized void set(int slice, int row, int column, float re, float im) {
                content.set(_sliceStride * slice, _rowStride * row, _columnStride * column, re, im);
            }

        };
        if (slices != 0)
            view.slices = (slices - 1) / _sliceStride + 1;
        if (rows != 0)
            view.rows = (rows - 1) / _rowStride + 1;
        if (columns != 0)
            view.columns = (columns - 1) / _columnStride + 1;
        view.isNoView = false;
        return view;
    }

    protected FComplexMatrix3D getContent() {
        return content;
    }

    public FComplexMatrix2D like2D(int rows, int columns) {
        throw new InternalError(); // should never get called
    }

    protected FComplexMatrix2D like2D(int rows, int columns, int rowZero, int columnZero, int rowStride,
            int columnStride) {
        throw new InternalError(); // should never get called
    }

    protected FComplexMatrix3D viewSelectionLike(int[] sliceOffsets, int[] rowOffsets, int[] columnOffsets) {
        throw new InternalError(); // should never get called
    }

    public FloatMatrix3D getImaginaryPart() {
        final DenseLargeFloatMatrix3D Im = new DenseLargeFloatMatrix3D(slices, rows, columns);
        int nthreads = ConcurrencyUtils.getNumberOfThreads();
        if ((nthreads > 1) && (size() >= ConcurrencyUtils.getThreadsBeginN_3D())) {
            nthreads = Math.min(nthreads, slices);
            Future<?>[] futures = new Future[nthreads];
            int k = slices / nthreads;
            for (int j = 0; j < nthreads; j++) {
                final int firstSlice = j * k;
                final int lastSlice = (j == nthreads - 1) ? slices : firstSlice + k;
                futures[j] = ConcurrencyUtils.submit(new Runnable() {
                    public void run() {
                        for (int s = firstSlice; s < lastSlice; s++) {
                            for (int r = 0; r < rows; r++) {
                                for (int c = 0; c < columns; c++) {
                                    Im.setQuick(s, r, c, getQuick(s, r, c)[1]);
                                }
                            }
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);
        } else {
            for (int s = 0; s < slices; s++) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        Im.setQuick(s, r, c, getQuick(s, r, c)[1]);
                    }
                }
            }
        }
        return Im;
    }

    public FloatMatrix3D getRealPart() {
        final DenseLargeFloatMatrix3D Re = new DenseLargeFloatMatrix3D(slices, rows, columns);
        int nthreads = ConcurrencyUtils.getNumberOfThreads();
        if ((nthreads > 1) && (size() >= ConcurrencyUtils.getThreadsBeginN_3D())) {
            nthreads = Math.min(nthreads, slices);
            Future<?>[] futures = new Future[nthreads];
            int k = slices / nthreads;
            for (int j = 0; j < nthreads; j++) {
                final int firstSlice = j * k;
                final int lastSlice = (j == nthreads - 1) ? slices : firstSlice + k;
                futures[j] = ConcurrencyUtils.submit(new Runnable() {
                    public void run() {
                        for (int s = firstSlice; s < lastSlice; s++) {
                            for (int r = 0; r < rows; r++) {
                                for (int c = 0; c < columns; c++) {
                                    Re.setQuick(s, r, c, getQuick(s, r, c)[0]);
                                }
                            }
                        }
                    }
                });
            }
            ConcurrencyUtils.waitForCompletion(futures);
        } else {
            for (int s = 0; s < slices; s++) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        Re.setQuick(s, r, c, getQuick(s, r, c)[0]);
                    }
                }
            }
        }
        return Re;
    }
}
