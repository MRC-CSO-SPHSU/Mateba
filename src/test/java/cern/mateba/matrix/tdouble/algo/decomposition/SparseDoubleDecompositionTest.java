package cern.mateba.matrix.tdouble.algo.decomposition;

import cern.mateba.matrix.tdouble.DoubleMatrix1D;
import cern.mateba.matrix.tdouble.DoubleMatrix2D;
import cern.mateba.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.mateba.matrix.tdouble.impl.SparseCCDoubleMatrix2D;

public class SparseDoubleDecompositionTest {

    private static final int N = 5;
    private static final int[] Ap = {0, 2, 5, 9, 10, 12};
    private static final int[] Ai = {0, 1, 0, 2, 4, 1, 2, 3, 4, 2, 1, 4};
    private static final double[] Ax = {2., 3., 3., -1., 4., 4., -3., 1., 2., 2., 6., 1.};
    private static final double[] b = {8., 45., -3., 3., 19.};

    /**
     * solution is x = (1,2,3,4,5)
     */
    public static void main(String[] args) {

        DoubleMatrix2D A = new SparseCCDoubleMatrix2D(N, N, Ai, Ap, Ax);
        DoubleMatrix1D Bclu = new DenseDoubleMatrix1D(b);
        DoubleMatrix1D Bklu = new DenseDoubleMatrix1D(b);
        DoubleMatrix1D Bqr = new DenseDoubleMatrix1D(b);

        SparseDoubleLUDecomposition clu = new CSparseDoubleLUDecomposition(A, 0, true);
        clu.solve(Bclu);
        System.out.println(Bclu);

        SparseDoubleLUDecomposition klu = new SparseDoubleKLUDecomposition(A, 0, true);
        klu.solve(Bklu);
        System.out.println(Bklu);

        SparseDoubleQRDecomposition qr = new SparseDoubleQRDecomposition(A, 0);
        qr.solve(Bqr);
        System.out.println(Bqr);
    }

}
