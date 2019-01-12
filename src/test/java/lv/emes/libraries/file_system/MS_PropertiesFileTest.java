package lv.emes.libraries.file_system;

import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
        pro.setProperty("ONLINE", "true");

        pro.save(FILE_NAME, "Database connection info.");
        assertThat(MS_FileSystemTools.fileExists(FILE_NAME))
                .withFailMessage("Failed to create file.")
                .isTrue();
    }

    @Test
    public void test02ReadPropertiesFile() throws IOException {
        MS_PropertiesFile pro = new MS_PropertiesFile();
        pro.load(FILE_NAME);
        assertThat(pro.getProperty("USERNAME")).isEqualTo("root");
        assertThat(pro.getProperty("HOSTNAME")).isEqualTo("localhost");
        assertThat(pro.getInt("PORT", -123456789)).isEqualTo(3306); //tests for integer value
        assertThat(pro.getOrDefault("DEFAULT_USER_ID", 0)).isEqualTo("1"); //positive result is of type String anyways
        assertThat(pro.getPrimitive("ONLINE", Boolean.class)).isTrue();

        //some failure cases with fallback to default
        assertThat(pro.getProperty("DEFAULT_USER_ID")).isNotEqualTo("something wrong");
        assertThat(pro.getProperty("unknown property")).isNull();
        //default value is Integer, so be careful, better not to use different types for expected and default values!
        assertThat(pro.getOrDefault("unknown property", 0)).isEqualTo(0);
        assertThat(pro.getPrimitive("unknown property", Boolean.class, false)).isFalse();
        assertThat(pro.getPrimitive("ONLINE", Integer.class, null)).isNull();
    }
}
