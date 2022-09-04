package cern.mateba.matrix.tdcomplex.impl;

import cern.mateba.matrix.tdcomplex.DComplexMatrix2DTest;

public class SparseCCDComplexMatrix2DTest extends DComplexMatrix2DTest {

    public SparseCCDComplexMatrix2DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseCCDComplexMatrix2D(NROWS, NCOLUMNS);
        B = new SparseCCDComplexMatrix2D(NROWS, NCOLUMNS);
        Bt = new SparseCCDComplexMatrix2D(NCOLUMNS, NROWS);
    }

}
