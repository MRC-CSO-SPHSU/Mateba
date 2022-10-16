package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

class RandomSupportTest {

    @Test
    void generateNonNegativeIntInRangeNotIncludeBound() { // fixme improve this
        val rngMock = Mockito.mock(MersenneTwister.class, Mockito.CALLS_REAL_METHODS);
        doReturn(0).when(rngMock).nextInt();

        int j;
        val rng = new MersenneTwister(0L);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            j = RandomSupport.generateNextIntCO(rng, 2);
            assertTrue(j < 2);
            assertTrue(j >= 0);
        }

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            j = RandomSupport.generateNextIntCO(rng, 13);
            assertTrue(j < 13);
            assertTrue(j >= 0);
        }
    }

    @Test
    void doubleFromLongCC() {
        assertEquals(0., RandomSupport.doubleFromLongCC(0));
        assertEquals(0.49999999999999994, RandomSupport.doubleFromLongCC(Long.MAX_VALUE));
        assertEquals(0.5000000000000001, RandomSupport.doubleFromLongCC(Long.MIN_VALUE));
        assertEquals(0.5000000000000002, RandomSupport.doubleFromLongCC(Long.MIN_VALUE + 2048));
        assertEquals(1., RandomSupport.doubleFromLongCC(-1));
    }

    @Test
    void doubleFromLongCO() {
        assertEquals(0., RandomSupport.doubleFromLongCO(0));
        assertEquals(0.4999999999999999, RandomSupport.doubleFromLongCO(Long.MAX_VALUE));
        assertEquals(0.5, RandomSupport.doubleFromLongCO(Long.MIN_VALUE));
        assertEquals(0.5, RandomSupport.doubleFromLongCO(Long.MIN_VALUE + 1));
        assertEquals(0x1.0000000000001p-1, RandomSupport.doubleFromLongCO(Long.MIN_VALUE + 2048));
        assertEquals(0.9999999999999999, RandomSupport.doubleFromLongCO(-1));
    }

    @Test
    void doubleFromLongOC() {
        assertEquals(0x1.0p-53, RandomSupport.doubleFromLongOC(0));
        assertEquals(0.4999999999999999, RandomSupport.doubleFromLongOC(Long.MAX_VALUE - 2048));
        assertEquals(0.5, RandomSupport.doubleFromLongOC(Long.MAX_VALUE - 2047));
        assertEquals(0.5, RandomSupport.doubleFromLongOC(Long.MAX_VALUE));
        assertEquals(0x1.0000000000001p-1, RandomSupport.doubleFromLongOC(Long.MIN_VALUE));
        assertEquals(1., RandomSupport.doubleFromLongOC(-1));
    }

    @Test
    void doubleFromLongOO() {
        assertEquals(0x1.0p-53, RandomSupport.doubleFromLongOO(0));
        assertEquals(0.9999999999999999, RandomSupport.doubleFromLongOO(-1));
        assertEquals(0.4999999999999999, RandomSupport.doubleFromLongOO(Long.MAX_VALUE));
        assertEquals(0.5000000000000001, RandomSupport.doubleFromLongOO(Long.MIN_VALUE));
        assertEquals(0.5000000000000001, RandomSupport.doubleFromLongOO(Long.MIN_VALUE + 2048));
        assertEquals(0.5000000000000003, RandomSupport.doubleFromLongOO(Long.MIN_VALUE + 4096));
    }

    @Test
    void testGamma() {
        assertEquals(4.440892098500626E-16d, RandomSupport.gamma(2.0d, 3.0d));
        assertEquals(4.440892098500626E-16d, RandomSupport.gamma(1.0d, 3.0d));
    }
}
