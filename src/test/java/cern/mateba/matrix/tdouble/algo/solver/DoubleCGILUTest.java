package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILU;

/**
 * Test of DoubleCG with ILU
 */
public class DoubleCGILUTest extends DoubleCGTest {

    public DoubleCGILUTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILU(A.rows());
    }

}
