package cern.mateba.matrix.tdcomplex.impl;

import cern.mateba.matrix.tdcomplex.DComplexMatrix2DTest;

public class SparseCCMDComplexMatrix2DTest extends DComplexMatrix2DTest {

    public SparseCCMDComplexMatrix2DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseCCMDComplexMatrix2D(NROWS, NCOLUMNS);
        B = new SparseCCMDComplexMatrix2D(NROWS, NCOLUMNS);
        Bt = new SparseCCMDComplexMatrix2D(NCOLUMNS, NROWS);
    }

}
