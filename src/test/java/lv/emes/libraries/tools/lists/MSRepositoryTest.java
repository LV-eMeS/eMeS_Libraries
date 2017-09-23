package lv.emes.libraries.tools.lists;

import lv.emes.libraries.tools.MS_EqualityCheckBuilder;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * All tests depends on previous test data.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSRepositoryTest {

    private static final String PROJECT_NAME1 = "Test";
    private static final String CATEGORY_NAME1 = "First category";
    private static final String PROJECT_NAME2 = "Test";
    private static final String CATEGORY_NAME2 = "Second category";
    private static final char DATA_DELIMITER = '|';
    private static final String[] ITEMS = {"Item1", "Item2", "Item3", "Replaced item"};
    private static final String[] ITEM_IDS = {"First", "Second", "Third"};

    private MS_Repository<String, String> repository1;
    private MS_Repository<String, String> repository2;
    private static MS_List<Character> initedRepos;
    private static MS_StringList environment;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        initedRepos = new MS_List<>();
        environment = new MS_StringList();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test01ExceptionNotInitialized() {
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1);
        repository1.put("", "");
    }

    @Test
    public void test02AutoInitialization() {
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1, true);
        //at this point no exception occurs because auto initialization is made
        repository1.put("", "");
        repository1.remove(""); //lets remove this item, so it doesn't affect next tests
        initedRepos.clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test03ExceptionLoop() {
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1);
        repository1.init(); //this time initialize
        repository1.forEachItem("", "", (a, i) -> {
        });
    }

    @Test
    public void test11OneRepositoryAllOperations() { //DATA till here: [ITEM_IDS,ITEMS] = {}
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1);
        for (int i = 0; i < 3; i++) repository1.put(ITEM_IDS[i], ITEMS[i]);

        assertEquals(3, environment.count()); //all three items have been added
        assertEquals(3, repository1.count());

        new MS_EqualityCheckBuilder(true).append(environment, repository1, REPOSITORY_DATA_COMPARE);

        //DATA till here: [ITEM_IDS,ITEMS] = {[0,1,2][0,1,2]}
        for (int i = 0; i < 3; i++) {
            String previousValue = repository1.put(ITEM_IDS[i], ITEMS[3]); //replace all the existing values
            assertEquals(ITEMS[i], previousValue);
        }
        //DATA after changes: [ITEM_IDS,ITEMS] = {[0,1,2][3,3,3]}

        assertEquals(3, repository1.size()); //only values are changed, but records are the same
        assertEquals(ITEMS[3], repository1.get(ITEM_IDS[0]));
        assertEquals(ITEMS[3], repository1.find(ITEM_IDS[1]));
        assertEquals(ITEMS[3], repository1.findAll().get(ITEM_IDS[2]));
        //check that changes are synchronized with environment
        new MS_EqualityCheckBuilder(true).append(environment, repository1, REPOSITORY_DATA_COMPARE);

        //DATA till here: [ITEM_IDS,ITEMS] = {[0,1,2][3,3,3]}
        boolean isRemoved = repository1.remove(ITEM_IDS[1]);
        assertTrue(isRemoved);
        assertNull(repository1.get(ITEM_IDS[1])); //item with such ID cannot be found anymore
        assertEquals(2, repository1.length());
        assertEquals(2, environment.count());
    }

    @Test
    public void test12AccessPreviousData() { //DATA till here: [ITEM_IDS,ITEMS] = {[0,2][3,3]}
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1);
        assertTrue(repository1.isInitialized());
        new MS_EqualityCheckBuilder(true).append(environment, repository1, REPOSITORY_DATA_COMPARE);

        repository1.put(ITEM_IDS[1], ITEMS[1]);

        //DATA till here: [ITEM_IDS,ITEMS] = {[0,1,2][3,1,3]}
        repository2 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1); //new repository object with same location
        assertTrue(repository2.isInitialized());
        assertEquals(ITEMS[1], repository2.get(ITEM_IDS[1]));
    }

    @Test
    public void test21TwoRepositoriesDifferentCategories() { //DATA till here: [ITEM_IDS,ITEMS] = {[0,1,2][3,1,3]}
        repository1 = new MS_RepositoryForTest(PROJECT_NAME1, CATEGORY_NAME1);
        assertTrue(repository1.isInitialized());
        repository2 = new MS_RepositoryForTest(PROJECT_NAME2, CATEGORY_NAME2);
        assertFalse(repository2.isInitialized());

        repository2.init();
        assertTrue(repository2.isInitialized());
        assertEquals(0, repository2.size());

        for (int i = 0; i < 3; i++) repository2.put(ITEM_IDS[i], ITEMS[i]);
        //DATA after changes: [ITEM_IDS,ITEMS] = {[0,1,2][3,1,3]} {[0,1,2][0,1,2]}
        assertEquals(3, repository1.length());
        assertEquals(3, repository2.length());
        assertEquals(6, environment.count());
        new MS_EqualityCheckBuilder(true).append(environment, repository1, REPOSITORY_DATA_COMPARE);
        new MS_EqualityCheckBuilder(true).append(environment, repository2, REPOSITORY_DATA_COMPARE);

        //DATA till here: [ITEM_IDS,ITEMS] = {[0,1,2][3,1,3]} {[0,1,2][0,1,2]}
        repository1.remove(ITEM_IDS[0]);
        repository1.remove(ITEM_IDS[2]);
        repository2.put(ITEM_IDS[2], ITEMS[3]);
        //DATA after changes: [ITEM_IDS,ITEMS] = {[1][1]} {[0,1,2][0,1,3]}
        assertEquals(1, repository1.length());
        assertEquals(3, repository2.length());
        assertEquals(4, environment.count());
        new MS_EqualityCheckBuilder(true).append(environment, repository1, REPOSITORY_DATA_COMPARE);
        new MS_EqualityCheckBuilder(true).append(environment, repository2, REPOSITORY_DATA_COMPARE);
    }

    private static final class MS_RepositoryForTest extends MS_Repository<String, String> {

        private char repositoryLabel; //repository content will look like: repositoryLabel|identifier|item

        public MS_RepositoryForTest(String projectName, String categoryName) {
            super(projectName, categoryName);
            repositoryLabel = categoryName.charAt(0);
        }

        public MS_RepositoryForTest(String projectName, String categoryName, boolean autoInitialize) {
            this(projectName, categoryName);
            if (autoInitialize)
                init();
        }

        @Override
        public void add(String identifier, String item) {
            environment.add(itemToString(identifier, item));
        }

        @Override
        public int doGetSize() {
            AtomicInteger res = new AtomicInteger(0);
            environment.forEachItem((item, i) -> {
                if (item.charAt(0) == repositoryLabel)
                    res.incrementAndGet();
            });
            return res.get();
        }

        @Override
        protected boolean isInitialized() {
            return initedRepos.contains(repositoryLabel);
        }

        @Override
        protected void doInitialize() {
            initedRepos.add(repositoryLabel);
        }

        @Override
        public void doRemove(String identifier) {
            environment.forEachItem((item, i) -> {
                MS_StringList data = new MS_StringList(item, DATA_DELIMITER);
                if (data.get(0).charAt(0) == repositoryLabel)
                    if (data.get(1).equals(identifier)) {
                        environment.remove(i);
                        environment.breakOngoingForLoop();
                    }
            });
        }

        @Override
        public Map<String, String> doFindAll() {
            Map<String, String> res = new LinkedHashMap<>();
            environment.forEachItem((recordData, i) -> {
                MS_StringList record = new MS_StringList(recordData, DATA_DELIMITER);
                if (record.get(0).charAt(0) == repositoryLabel) {
                    res.put(record.get(1), record.get(2));
                }
            });
            return res;
        }

        @Override
        public String doFind(String identifier) {
            AtomicReference<String> res = new AtomicReference<>(null);
            environment.forEachItem((recordData, i) -> {
                MS_StringList record = new MS_StringList(recordData, DATA_DELIMITER);
                if (record.get(0).charAt(0) == repositoryLabel) {
                    if (record.get(1).equals(identifier)) {
                        res.set(record.get(2));
                        environment.breakOngoingForLoop();
                    }
                }
            });
            return res.get();
        }

        private String itemToString(String identifier, String item) {
            return String.valueOf(repositoryLabel) +
                    DATA_DELIMITER +
                    identifier +
                    DATA_DELIMITER +
                    item;
        }

    }

    private static final MS_EqualityCheckBuilder.IComparisonAlgorithm
            <MS_StringList, MS_Repository<String, String>> REPOSITORY_DATA_COMPARE =
            (realEnvironment, repo) -> {
                MS_EqualityCheckBuilder checker = new MS_EqualityCheckBuilder(true);
                realEnvironment.first();
                repo.forEachItem((item, id) -> {
                    recursivelyAppendEquality(realEnvironment, item, id, ((MS_RepositoryForTest) repo).repositoryLabel, checker);
                    realEnvironment.next();
                });
                return checker.areEqual();
            };

    private static void recursivelyAppendEquality(MS_StringList realEnvironment, String item, String id, char repoLabel, MS_EqualityCheckBuilder checker) {
        MS_StringList envElements = new MS_StringList(realEnvironment.current(), DATA_DELIMITER);
        if (envElements.get(0).charAt(0) == repoLabel) {
            checker.append(envElements.get(2), item);
            checker.append(envElements.get(1), id);
        } else {
            realEnvironment.next();
            recursivelyAppendEquality(realEnvironment, item, id, repoLabel, checker);
        }
    }
}
