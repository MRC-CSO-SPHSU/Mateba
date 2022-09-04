package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILUT;

/**
 * Test of DoubleIR with ILUT
 */
public class DoubleIRILUTTest extends DoubleIRTest {

    public DoubleIRILUTTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILUT(A.rows());
    }

}
