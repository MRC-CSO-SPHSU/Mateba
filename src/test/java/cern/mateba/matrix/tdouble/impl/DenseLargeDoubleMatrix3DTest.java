package cern.mateba.matrix.tdouble.impl;

import cern.mateba.matrix.tdcomplex.DComplexMatrix3D;
import cern.mateba.matrix.tdcomplex.impl.DenseLargeDComplexMatrix3D;
import cern.mateba.matrix.tdouble.DoubleMatrix3D;
import cern.mateba.matrix.tdouble.DoubleMatrix3DTest;

public class DenseLargeDoubleMatrix3DTest extends DoubleMatrix3DTest {

    public DenseLargeDoubleMatrix3DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new DenseLargeDoubleMatrix3D(NSLICES, NROWS, NCOLUMNS);
        B = new DenseLargeDoubleMatrix3D(NSLICES, NROWS, NCOLUMNS);
    }

    public void testDct3() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dct3(true);
        ((WrapperDoubleMatrix3D) A).idct3(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testDst3() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dst3(true);
        ((WrapperDoubleMatrix3D) A).idst3(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testDht3() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dht3();
        ((WrapperDoubleMatrix3D) A).idht3(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testFft3() {
        int nslices = 16;
        int nrows = 32;
        int ncolumns = 64;
        DoubleMatrix3D A = new DenseLargeDoubleMatrix3D(nslices, nrows, ncolumns);
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).fft3();
        ((WrapperDoubleMatrix3D) A).ifft3(true);
        for (int s = 0; s < nslices; s++) {
            for (int r = 0; r < nrows; r++) {
                for (int c = 0; c < ncolumns; c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }

        A = A.viewDice(2, 1, 0);
        Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).fft3();
        ((WrapperDoubleMatrix3D) A).ifft3(true);
        for (int s = 0; s < ncolumns; s++) {
            for (int r = 0; r < nrows; r++) {
                for (int c = 0; c < nslices; c++) {
                    assertEquals(0, Math.abs(Acopy.getQuick(s, r, c) - A.getQuick(s, r, c)), TOL);
                }
            }
        }

    }

    public void testDct2Slices() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dct2Slices(true);
        ((WrapperDoubleMatrix3D) A).idct2Slices(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testDst2Slices() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dst2Slices(true);
        ((WrapperDoubleMatrix3D) A).idst2Slices(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testDft2Slices() {
        DoubleMatrix3D Acopy = A.copy();
        ((WrapperDoubleMatrix3D) A).dht2Slices();
        ((WrapperDoubleMatrix3D) A).idht2Slices(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    assertEquals(Acopy.getQuick(s, r, c), A.getQuick(s, r, c), TOL);
                }
            }
        }
    }

    public void testGetFft3() {
        DComplexMatrix3D Ac = ((WrapperDoubleMatrix3D) A).getFft3();
        ((DenseLargeDComplexMatrix3D) Ac).ifft3(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    double[] elem = Ac.getQuick(s, r, c);
                    assertEquals(A.getQuick(s, r, c), elem[0], TOL);
                    assertEquals(0, elem[1], TOL);
                }
            }
        }
    }

    public void testGetIfft3() {
        DComplexMatrix3D Ac = ((WrapperDoubleMatrix3D) A).getIfft3(true);
        ((DenseLargeDComplexMatrix3D) Ac).fft3();
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    double[] elem = Ac.getQuick(s, r, c);
                    assertEquals(A.getQuick(s, r, c), elem[0], TOL);
                    assertEquals(0, elem[1], TOL);
                }
            }
        }
    }

    public void testGetFft2Slices() {
        DComplexMatrix3D Ac = ((WrapperDoubleMatrix3D) A).getFft2Slices();
        ((DenseLargeDComplexMatrix3D) Ac).ifft2Slices(true);
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    double[] elem = Ac.getQuick(s, r, c);
                    assertEquals(A.getQuick(s, r, c), elem[0], TOL);
                    assertEquals(0, elem[1], TOL);
                }
            }
        }
    }

    public void testGetIfft2Slices() {
        DComplexMatrix3D Ac = ((WrapperDoubleMatrix3D) A).getIfft2Slices(true);
        ((DenseLargeDComplexMatrix3D) Ac).fft2Slices();
        for (int s = 0; s < A.slices(); s++) {
            for (int r = 0; r < A.rows(); r++) {
                for (int c = 0; c < A.columns(); c++) {
                    double[] elem = Ac.getQuick(s, r, c);
                    assertEquals(A.getQuick(s, r, c), elem[0], TOL);
                    assertEquals(0, elem[1], TOL);
                }
            }
        }
    }
}
