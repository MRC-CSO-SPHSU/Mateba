package cern.mateba.matrix.tdcomplex.impl;

import cern.mateba.matrix.tdcomplex.DComplexMatrix2DTest;

public class SparseDComplexMatrix2DTest extends DComplexMatrix2DTest {
    public SparseDComplexMatrix2DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseDComplexMatrix2D(NROWS, NCOLUMNS);
        B = new SparseDComplexMatrix2D(NROWS, NCOLUMNS);
        Bt = new SparseDComplexMatrix2D(NCOLUMNS, NROWS);
    }

}
