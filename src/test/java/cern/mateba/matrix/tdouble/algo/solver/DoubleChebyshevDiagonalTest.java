package cern.mateba.matrix.tdouble.algo.solver;

import cern.mateba.matrix.tdouble.algo.solver.preconditioner.DoubleDiagonal;

/**
 * Test of DoubleChebyshev with diagonal preconditioner
 */
public class DoubleChebyshevDiagonalTest extends DoubleChebyshevTest {

    public DoubleChebyshevDiagonalTest(String arg0) {
        super(arg0);
    }

    protected void createSolver() throws Exception {
        super.createSolver();
        M = new DoubleDiagonal(A.rows());
    }

}
