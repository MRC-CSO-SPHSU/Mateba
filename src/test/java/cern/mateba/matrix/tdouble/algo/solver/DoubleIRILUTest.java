package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleILU;

/**
 * Test of DoubleIR with ILU
 */
public class DoubleIRILUTest extends DoubleIRTest {

    public DoubleIRILUTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleILU(A.rows());
    }

}
