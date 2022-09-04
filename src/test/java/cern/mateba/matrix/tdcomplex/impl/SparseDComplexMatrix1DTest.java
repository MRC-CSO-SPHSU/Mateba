package cern.mateba.matrix.tdcomplex.impl;

import cern.mateba.matrix.tdcomplex.DComplexMatrix1DTest;

public class SparseDComplexMatrix1DTest extends DComplexMatrix1DTest {

    public SparseDComplexMatrix1DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseDComplexMatrix1D(SIZE);
        B = new SparseDComplexMatrix1D(SIZE);
    }

}
