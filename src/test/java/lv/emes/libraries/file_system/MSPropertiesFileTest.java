package lv.emes.libraries.file_system;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSPropertiesFileTest {
    private static final String FILE_NAME = "./src/test/resources/" + "MSPropertiesFileTest.properties";

    @AfterClass
    public static void finalizeTestObjects() {
        MS_FileSystemTools.deleteFile(FILE_NAME);
    }

    @Test
    public void test01CreatePropertiesFile() throws IOException {
        MS_PropertiesFile pro = new MS_PropertiesFile();
        pro.setProperty("HOSTNAME", "localhost");
        pro.setProperty("USERNAME", "root");
        pro.setProperty("PORT", "3306");
        pro.setProperty("DEFAULT_USER_ID", "1");

        pro.save(FILE_NAME, "Database connection info.");
        assertTrue("Failed to create file.", MS_FileSystemTools.fileExists(FILE_NAME));
    }

    @Test
    public void test02ReadPropertiesFile() throws IOException {
        MS_PropertiesFile pro = new MS_PropertiesFile();
        pro.load(FILE_NAME);
        assertTrue(pro.getProperty("USERNAME").equals("root"));
        assertTrue(pro.getProperty("HOSTNAME").equals("localhost"));
        assertEquals(pro.getProperty("PORT", -123456789), 3306); //tests for integer value
        assertTrue(pro.getOrDefault("DEFAULT_USER_ID", 0).equals("1")); //good result is String anyways

        //some bad cases
        assertFalse(pro.getProperty("DEFAULT_USER_ID").equals("something wrong"));
        assertTrue(pro.getProperty("unknown property") == null);
        //default value is Integer, so be careful, better not to use different types for expected and default values!
        assertTrue(pro.getOrDefault("unknown property", 0).equals(0));
    }
}
