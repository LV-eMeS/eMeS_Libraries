package lv.emes.libraries.tools;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_PropertyInitializerImplTest {

    public enum TestProperties1Enum implements MS_PropertyInitializer {

        test(null), number(null), NOT_EXISTING("NOT_EXISTING"), NOT_EXISTING_NO_DEFAULT(null);

        private MS_PropertyInitializerImpl impl;

        TestProperties1Enum(String defaultValue) {
            impl = new MS_PropertyInitializerImpl(this, "src/test/resources/testProperties1.properties", defaultValue);
            impl.setUseDefaultValue(defaultValue != null);
        }

        @Override
        public String getProperty() {
            return impl.getProperty();
        }

        @Override
        public <T> T getProperty(Class<T> valueClass) {
            return impl.getProperty(valueClass);
        }
    }

    public enum TestProperties404Enum implements MS_PropertyInitializer {

        NOT_EXISTING("NOT_EXISTING"), ANOTHER_NOT_EXISTING(null);

        private MS_PropertyInitializerImpl impl;

        TestProperties404Enum(String defaultValue) {
            impl = new MS_PropertyInitializerImpl(this, "src/test/resources/testProperties3.properties", defaultValue);
            impl.setUseDefaultValue(defaultValue != null);
        }

        @Override
        public String getProperty() {
            return impl.getProperty();
        }

        @Override
        public <T> T getProperty(Class<T> valueClass) {
            return impl.getProperty(valueClass);
        }
    }

    @Test
    public void testValues() {
        assertThat(TestProperties1Enum.test.getProperty()).isEqualTo("test property");
        assertThat(TestProperties1Enum.number.getProperty()).isEqualTo("5");
        assertThat(TestProperties1Enum.number.getProperty(Integer.class)).isEqualTo(5);
        assertThat(TestProperties1Enum.NOT_EXISTING.getProperty()).isEqualTo("NOT_EXISTING");
        assertThat(TestProperties1Enum.NOT_EXISTING_NO_DEFAULT.getProperty()).isNull();
    }

    @Test
    public void testGetNotFoundPropertyDefaultValue() {
        assertThat(TestProperties404Enum.NOT_EXISTING.getProperty()).isEqualTo("NOT_EXISTING");
        assertThatThrownBy(TestProperties404Enum.ANOTHER_NOT_EXISTING::getProperty)
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Properties file [src/test/resources/testProperties3.properties] cannot be read.")
                .hasCauseInstanceOf(IOException.class);
    }
}