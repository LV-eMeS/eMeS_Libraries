package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_StringList;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * All tests depends on previous test data.
 * This test operates with text file repository and files can hold only one string line.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_FileRepositoryTest {

    private static final String PROJECT_NAME = TestData.TEMP_DIR + "eMeS_Libraries";
    private static final String CATEGORY_NAME1 = "MS_FileRepositoryTest";
    private static final String CATEGORY_NAME2 = "MS_FileRepositoryTest2";
    private static final String[] ITEM_IDS = {"First.txt", "Second.txt", "Third.txt"};
    private static final String[] ITEMS = {"Item1", "Item2", "Item3", "Replaced item"};

    private MS_FileRepositoryForTest repository1;
    private MS_FileRepositoryForTest repository2;

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {
        MS_FileSystemTools.deleteDirectory(PROJECT_NAME);
    }

    @Test
    public void test01BothReposInCorrectLocations() {
        repository1 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME1);
        repository2 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME2);
        assertThat(MS_FileSystemTools.directoryExists(repository1.getPathToRepository())).isTrue();
        assertThat(MS_FileSystemTools.directoryExists(repository2.getPathToRepository())).isTrue();
    }

    @Test
    public void test02BasicOperations() {
        repository1 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME1);
        repository1.put(ITEM_IDS[0], ITEMS[0]);
        assertThat(repository1.size()).isEqualTo(1);
        assertThat(repository1.get(ITEM_IDS[0])).isEqualTo(ITEMS[0]);
        repository1.remove(ITEM_IDS[0]);
        repository1.remove(ITEM_IDS[0]); //same item cannot be removed again
        assertThat(repository1.size()).isEqualTo(0);
    }

    @Test
    public void test03TwoRepositoriesDifferentCategories() {
        repository1 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME1);
        repository2 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME2);

        repository1.put(ITEM_IDS[0], ITEMS[0]);
        assertThat(repository1.size()).isEqualTo(1);
        assertThat(repository2.size()).isEqualTo(0);

        repository2.put(ITEM_IDS[0], ITEMS[0]);
        repository2.put(ITEM_IDS[1], ITEMS[1]);
        assertThat(repository1.size()).isEqualTo(1);
        assertThat(repository2.size()).isEqualTo(2);
        assertThat(repository1.find(ITEM_IDS[0])).isEqualTo(ITEMS[0]);
        assertThat(repository2.find(ITEM_IDS[0])).isEqualTo(ITEMS[0]);
        assertThat(repository1.find(ITEM_IDS[1])).isEqualTo(null);
        assertThat(repository2.find(ITEM_IDS[1])).isEqualTo(ITEMS[1]);

        repository2.remove(ITEM_IDS[0]);
        assertThat(repository1.size()).isEqualTo(1);
        assertThat(repository2.size()).isEqualTo(1);
        assertThat(repository1.find(ITEM_IDS[0])).isEqualTo(ITEMS[0]);
        assertThat(repository2.find(ITEM_IDS[0])).isEqualTo(null);
        assertThat(repository1.find(ITEM_IDS[1])).isEqualTo(null);
        assertThat(repository2.find(ITEM_IDS[1])).isEqualTo(ITEMS[1]);
    }

    @Test
    public void test04RemoveAllRepositoryItems() {
        repository2 = new MS_FileRepositoryForTest(PROJECT_NAME, CATEGORY_NAME2);
        assertThat(repository2.size()).isEqualTo(1);
        repository2.removeAll();
        assertThat(repository2.size()).isEqualTo(0);
        assertThat(repository2.isInitialized()).isTrue();
        assertThat(MS_FileSystemTools.directoryExists(PROJECT_NAME + "/" + CATEGORY_NAME2)).isTrue();
    }

    /**
     * A class that operates with files containing just 1 string line.
     */
    private static final class MS_FileRepositoryForTest extends MS_FileRepository<String> {

        public MS_FileRepositoryForTest(String repositoryRoot, String repositoryCategoryName) {
            super(repositoryRoot, repositoryCategoryName, true);
        }

        @Override
        protected void doAdd(String identifier, String item) {
            MS_TextFile file = new MS_TextFile(getPathToRepository() + identifier);
            file.writeln(item, true);
        }

        @Override
        protected void doRemove(String identifier) {
            MS_FileSystemTools.deleteFile(getPathToRepository() + identifier);
        }

        @Override
        protected String doFind(String identifier) {
            String pathToAFile = getPathToRepository() + identifier;
            if (MS_FileSystemTools.fileExists(pathToAFile)) {
                MS_TextFile file = new MS_TextFile(pathToAFile);
                return file.readln(true);
            } else {
                return null;
            }
        }

        @Override
        protected Map<String, String> doFindAll() {
            Map<String, String> res = new LinkedHashMap<>();
            MS_StringList filenames = MS_FileSystemTools.getDirectoryFileList_Shortnames(getPathToRepository());
            filenames.forEachItem((filename, i) -> {
                MS_TextFile file = new MS_TextFile(getPathToRepository() + filename);
                res.put(filename, file.readln(true));
            });
            return res;
        }

        @Override
        protected int doGetSize() {
            return MS_FileSystemTools.getDirectoryFileList_Shortnames(getPathToRepository()).size();
        }

        @Override
        public void doRemoveAll() {
            MS_FileSystemTools.deleteDirectory(getPathToRepository());
            doInitialize(); //create directory afterwards
        }
    }
}
