package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleAMG;

/**
 * Test of DoubleGMRES with AMG
 */
public class DoubleGMRESAMGTest extends DoubleGMRESTest {

    public DoubleGMRESAMGTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleAMG();
    }

}
