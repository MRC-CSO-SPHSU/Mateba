package cern.mateba.matrix.tint.impl;

import cern.mateba.matrix.tint.IntMatrix1DTest;

public class SparseIntMatrix1DTest extends IntMatrix1DTest {

    public SparseIntMatrix1DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseIntMatrix1D(SIZE);
        B = new SparseIntMatrix1D(SIZE);
    }
}
