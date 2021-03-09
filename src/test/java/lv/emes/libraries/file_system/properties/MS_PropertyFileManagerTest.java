package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_PropertyFileManagerTest {

    private static final String testPropsPath1 = "src/test/resources/testProperties1.properties";
    private static final String testPropsPath2 = "src/test/resources/testProperties2.properties";

    @Test
    public void testGetProperties() {
        MS_PropertiesFile props;

        props = MS_PropertyFileManager.getProperties(testPropsPath1);
        assertThat(props.getProperty("test")).isEqualTo("test property");
        assertThat(props.getProperty("test2")).isNull();
        assertThat(props.getInt("number", 0)).isEqualTo(5);

        props = MS_PropertyFileManager.getProperties(testPropsPath2);
        assertThat(props.getProperty("test1")).isEqualTo("test property 1");
        assertThat(props.getProperty("test3")).isEqualTo("tēšt prōpērtī 3");
    }

    @Test(expected = MS_BadSetupException.class)
    public void testPropertiesFileDoesNotExist() {
        MS_PropertyFileManager.getProperties("src/test/resources/testProperties3.properties");
    }
}