package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILUT;

/**
 * Test of DoubleCGS with ILUT
 */
public class DoubleCGSILUTTest extends DoubleCGSTest {

    public DoubleCGSILUTTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILUT(A.rows());
    }

}
