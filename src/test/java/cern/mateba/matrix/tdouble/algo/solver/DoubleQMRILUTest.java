package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILU;

/**
 * Test of DoubleQMR with ILU
 */
public class DoubleQMRILUTest extends DoubleQMRTest {

    public DoubleQMRILUTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILU(A.rows());
    }

}
