package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_YamlFileManagerTest {

    @Test
    public void testGetConfigurationMostCommonProperties() {
        MS_YamlStructureWithDefaultRoot loadedConfigs = MS_YamlFileManager.loadProperties(TestData.PATH_VALID_YAML, AllPropertiesRootStructure.class);
        assertThat(loadedConfigs).isNotNull();
        assertThat(loadedConfigs.<String>getProperty("testStr")).isEqualTo("test");
        assertThat(loadedConfigs.<Integer>getProperty("testInt")).isEqualTo(123);
        assertThat(loadedConfigs.<Boolean>getProperty("testBool")).isEqualTo(true);
        assertThat(loadedConfigs.<Double>getProperty("testFloat")).isEqualTo(3.14d);
        assertThat(loadedConfigs.getProperty("testMap.nested", String.class)).isEqualTo("value");
        assertThatThrownBy(() -> loadedConfigs.<String>getProperty("testMap.unknown"))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Property cannot be found by given key [testMap.unknown], node [unknown] doesn't exist");
        assertThatThrownBy(() -> loadedConfigs.<String>getProperty("testNotExist"))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Property cannot be found by given key: testNotExist");

        // Test properties that are not located in the file
        assertThat(loadedConfigs.getPropertyOptional("outsideRoot", Integer.class)).isNull();
        System.getProperties().put("outsideRoot", "78");
        assertThat(loadedConfigs.getProperty("outsideRoot", Integer.class)).isEqualTo(78);
        assertThat(loadedConfigs.getProperty("outsideRoot", String.class)).isEqualTo("78");
    }

    @Test
    public void testGetConfigurationDifferentMainRootAndOtherVariable() {
        DifferentRootStructure loadedConfigs = MS_YamlFileManager.loadProperties(TestData.PATH_VALID_YAML, DifferentRootStructure.class);
        assertThat(loadedConfigs.<Integer>getProperty("testInt")).isEqualTo(456);
        assertThatThrownBy(() -> loadedConfigs.<String>getProperty("testStr"))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Property cannot be found by given key: testStr");
        assertThat(loadedConfigs.getDifferentRootList()).containsExactly("Something", "Something else", "14", "Yet something else");
        assertThat(loadedConfigs.getPropertyOptional("unknownProperty", String.class)).isNull();
        assertThat(loadedConfigs.getRandomBool()).isNull(); // Fields that does not exist in yaml are ignored

        // Get properties from different root declared as map
        assertThat(loadedConfigs.getDifferentRoot2Property("testInt", Integer.class)).isEqualTo(789);
        assertThat(loadedConfigs.getDifferentRoot2Property("testStr", String.class)).isEqualTo("abc");
        assertThatThrownBy(() -> loadedConfigs.getDifferentRoot2Property("testDouble", Double.class))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Property cannot be found by given key: testDouble");
    }

    @Test
    public void testLoadConfigFileThatDoesNotExistFailure() {
        assertThatThrownBy(() -> MS_YamlFileManager.loadProperties("incorrect/path.yml", MS_YamlStructureBase.class))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Properties file [incorrect/path.yml] doesn't exist.");
    }

    @Test
    public void testLoadConfigFileContentFormatNotSupported() {
        assertThatThrownBy(() -> MS_YamlFileManager.loadProperties(TestData.PATH_BAD_YAML, MS_YamlStructureBase.class))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Properties file [%s] cannot be read.", TestData.PATH_BAD_YAML);
    }

    public static class DifferentRootStructure extends MS_YamlStructureWithDefaultRoot {
        private Map<String, Object> differentRoot;
        private Map<String, Object> differentRoot2;
        private List<String> differentRootList;
        private Boolean randomBool;

        @Override
        protected Map<String, Object> getRootProperties() {
            return differentRoot;
        }

        public void setDifferentRoot(Map<String, Object> differentRoot) {
            this.differentRoot = differentRoot;
        }

        public void setDifferentRootList(List<String> differentRootList) {
            this.differentRootList = differentRootList;
        }

        public List<String> getDifferentRootList() {
            return differentRootList;
        }

        public void setRandomBool(Boolean randomBool) {
            this.randomBool = randomBool;
        }

        public Boolean getRandomBool() {
            return randomBool;
        }

        public void setDifferentRoot2(Map<String, Object> differentRoot2) {
            this.differentRoot2 = differentRoot2;
        }

        public <T> T getDifferentRoot2Property(String key, Class<T> propertyClass) throws MS_BadSetupException {
            return getConfiguration(key, propertyClass, differentRoot2);
        }
    }

    public static class AllPropertiesRootStructure extends MS_YamlStructureWithDefaultRoot {
        private Map<String, Object> allProperties;

        public void setAllProperties(Map<String, Object> allProperties) {
            this.allProperties = allProperties;
        }

        @Override
        protected Map<String, Object> getRootProperties() {
            return allProperties;
        }
    }
}
