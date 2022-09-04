package cern.mateba.matrix.tlong.impl;

import cern.mateba.matrix.tlong.LongMatrix3DTest;

public class SparseLongMatrix3DTest extends LongMatrix3DTest {

    public SparseLongMatrix3DTest(String arg0) {
        super(arg0);
    }

    protected void createMatrices() throws Exception {
        A = new SparseLongMatrix3D(NSLICES, NROWS, NCOLUMNS);
        B = new SparseLongMatrix3D(NSLICES, NROWS, NCOLUMNS);
    }
}
