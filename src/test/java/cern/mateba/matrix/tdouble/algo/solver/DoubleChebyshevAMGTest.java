package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleAMG;

/**
 * Test of DoubleChebyshev with AMG
 */
public class DoubleChebyshevAMGTest extends DoubleChebyshevTest {

    public DoubleChebyshevAMGTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleAMG();
    }

}
