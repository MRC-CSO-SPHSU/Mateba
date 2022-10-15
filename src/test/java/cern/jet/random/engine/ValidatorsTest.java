package cern.jet.random.engine;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;


class ValidatorsTest {

    @Test
    void testValidateStreamSize() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateStreamSize(3L)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateStreamSize2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateStreamSize(-1L));
    }


    @Test
    void testValidateDoubleRange() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateDoubleRange(10.0d, 10.0d));
        assertThrows(IllegalArgumentException.class,
            () -> Validators.validateDoubleRange(Double.POSITIVE_INFINITY, 10.0d));
        assertEquals(9.5d, Validators.validateDoubleRange(0.5d, 10.0d));
        assertEquals(10.5d, Validators.validateDoubleRange(-0.5d, 10.0d));
        assertThrows(IllegalArgumentException.class, () -> Validators.validateDoubleRange(Double.NaN, 10.0d));
    }

    @Test
    void testValidateFloatRange() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateFloatRange(10.0f, 10.0f));
        assertThrows(IllegalArgumentException.class,
            () -> Validators.validateFloatRange(Float.POSITIVE_INFINITY, 10.0f));
        assertEquals(9.5f, Validators.validateFloatRange(0.5f, 10.0f));
        assertEquals(10.5f, Validators.validateFloatRange(-0.5f, 10.0f));
        assertThrows(IllegalArgumentException.class, () -> Validators.validateFloatRange(Float.NaN, 10.0f));
    }

    @Test
    void testValidateIntRange() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateIntRange(1, 3)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateIntRange2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateIntRange(1, 1));
    }

    @Test
    void testValidateLongRange() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateLongRange(1L, 1L));
    }

    @Test
    void testValidateLongRange2() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateLongRange(1L, 3L)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateIntBound() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateIntBound(1)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateIntBound2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateIntBound(0));
    }

    @Test
    void testValidateIntBound3() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateIntBound(-1));
    }

    @Test
    void testValidateLongBound() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateLongBound(1L)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateLongBound2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateLongBound(-1L));
    }

    @Test
    void testValidateLongBound3() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateLongBound(0L));
    }

    @Test
    void testValidateDoubleBound() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateDoubleBound(10.0d)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateDoubleBound2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateDoubleBound(Double.POSITIVE_INFINITY));
    }

    @Test
    void testValidateDoubleBound3() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateDoubleBound(Double.NaN));
    }

    @Test
    void testValidateFloatBound() {
        try (val staticMock = mockStatic(Validators.class)) {
            staticMock.when(() -> Validators.validateFloatBound(10.0f)).thenCallRealMethod();
        }
    }

    @Test
    void testValidateFloatBound2() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateFloatBound(Float.POSITIVE_INFINITY));
    }

    @Test
    void testValidateFloatBound3() {
        assertThrows(IllegalArgumentException.class, () -> Validators.validateFloatBound(Float.NaN));
    }
}

