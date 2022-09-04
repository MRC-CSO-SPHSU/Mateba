package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleAMG;

/**
 * Test of DoubleCGS with AMG
 */
public class DoubleCGSAMGTest extends DoubleCGSTest {

    public DoubleCGSAMGTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleAMG();
    }

}
