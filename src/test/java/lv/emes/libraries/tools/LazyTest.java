package lv.emes.libraries.tools;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LazyTest {

    private Supplier<Object> someField = Lazy.lazily(() -> someField = Lazy.value(expensiveComputation()));
    private Supplier expensiveComputation;

    @Before
    public void setUp() {
        expensiveComputation = mock(Supplier.class);
        when(expensiveComputation.get()).thenReturn(new Object());
    }

    @Test
    public void testLaziness() {
        verify(expensiveComputation, times(0)).get();
        assertThat((someField.get())).isNotNull();
        verify(expensiveComputation, times(1)).get();

        assertThat((someField.get())).isNotNull();
        verify(expensiveComputation, times(1)).get(); //still 1 after 2nd call
    }

    private Object expensiveComputation() {
        return expensiveComputation.get();
    }
}