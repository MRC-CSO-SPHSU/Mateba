package cern.mateba;

import junit.framework.Test;
import junit.framework.TestSuite;
import cern.mateba.matrix.tdcomplex.AllDComplexMatrixTests;
import cern.mateba.matrix.tdouble.AllDoubleMatrixTests;
import cern.mateba.matrix.tint.AllIntMatrixTests;
import cern.mateba.matrix.tlong.AllLongMatrixTests;
import edu.emory.mathcs.utils.ConcurrencyUtils;

public class AllTests {

    public static int NTHREADS = 2;

    public static Test suite() {
        ConcurrencyUtils.setNumberOfThreads(NTHREADS);
        System.out.println("Running Mateba tests using " + ConcurrencyUtils.getNumberOfThreads() + " threads.");
        TestSuite suite = new TestSuite("Mateba tests");
        suite.addTest(AllDoubleMatrixTests.suite());
        suite.addTest(AllDComplexMatrixTests.suite());
        suite.addTest(AllLongMatrixTests.suite());
        suite.addTest(AllIntMatrixTests.suite());
        return suite;
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(AllTests.class.getName().toString());
    }
}
