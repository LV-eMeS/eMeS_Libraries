package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MS_PropertyFileManagerTest {

    private static final String testPropsPath1 = "src/test/resources/testProperties1.properties";
    private static final String testPropsPath2 = "src/test/resources/testProperties2.properties";

    @Test
    public void testGetProperties() {
        MS_PropertiesFile props;

        props = MS_PropertyFileManager.getProperties(testPropsPath1);
        assertEquals("test property", props.getProperty("test"));
        assertNull(props.getProperty("test2"));
        assertEquals(5, props.getInt("number", 0));

        props = MS_PropertyFileManager.getProperties(testPropsPath2);
        assertEquals("test property 1", props.getProperty("test1"));
        assertEquals("test property 3", props.getProperty("test3"));
    }

    @Test(expected = MS_BadSetupException.class)
    public void testPropertiesFileDoesNotExist() {
        MS_PropertyFileManager.getProperties("src/test/resources/testProperties3.properties");
    }
}