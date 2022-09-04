package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILUT;

/**
 * Test of DoubleQMR with ILUT
 */
public class DoubleQMRILUTTest extends DoubleQMRTest {

    public DoubleQMRILUTTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILUT(A.rows());
    }

}
