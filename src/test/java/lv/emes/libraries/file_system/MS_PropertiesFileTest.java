package lv.emes.libraries.file_system;

import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_PropertiesFileTest {

    private static final String FILE_NAME = "./src/test/resources/" + "MS_PropertiesFileTest.properties";

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
        assertEquals("root", pro.getProperty("USERNAME"));
        assertEquals("localhost", pro.getProperty("HOSTNAME"));
        assertEquals(pro.getProperty("PORT", -123456789), 3306); //tests for integer value
        assertEquals("1", pro.getOrDefault("DEFAULT_USER_ID", 0)); //good result is String anyways

        //some bad cases
        assertNotEquals("something wrong", pro.getProperty("DEFAULT_USER_ID"));
        assertNull(pro.getProperty("unknown property"));
        //default value is Integer, so be careful, better not to use different types for expected and default values!
        assertEquals(0, pro.getOrDefault("unknown property", 0));
    }
}
