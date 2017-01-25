package templates;

import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestKlasesSagatave {
//	private static Variable variable = null;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {

    }

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {

    }

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {

    }

    @After
    //After every test tear down this mess!
    public void tearDownForEachTest() {

    }

    @Test
    public void test01NameOfTest() {
        assertEquals(val, variable);
    }
}
