package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILUT;

/**
 * Test of DoubleBiCG with ILUT
 */
public class DoubleBiCGILUTTest extends DoubleBiCGTest {

    public DoubleBiCGILUTTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILUT(A.rows());
    }

}
