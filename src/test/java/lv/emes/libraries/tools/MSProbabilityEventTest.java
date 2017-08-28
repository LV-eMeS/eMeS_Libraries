package lv.emes.libraries.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSProbabilityEventTest {
    private static MS_ProbabilityEvent event = null;

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        event = new MS_ProbabilityEvent(0);
    }

    @After
    //After every test tear down this mess!
    public void tearDownForEachTest() {
        event.setProbability(0);
    }

    @Test
    public void test00GettersAndSetters() {
        assertEquals(0, event.getCurrentProgress(), 0.01);
        assertEquals(0, event.getProbability(), 0.01);
        assertEquals(0, event.getCurrentProgressAsPercent());
        assertEquals(0, event.getProbabilityAsPercent());

        event.setProbability(5);
        assertEquals(0.05, event.getProbability(), 0.001);
        assertEquals(5, event.getProbabilityAsPercent());

        event.happened(); //try to make it happen 2 times to increase current progress
        assertEquals(5, event.getCurrentProgressAsPercent());
        event.happened();
        assertEquals(0.1, event.getCurrentProgress(), 0.01);
    }

    @Test
    public void test01EventNeverHappens() {
        for (int i = 1; i < 102; i++)
            assertFalse(event.happened());
    }

    @Test
    public void test02EventHappensEveryTime() {
        event.setProbability(1d);
        for (int i = 1; i < 102; i++)
            assertTrue(event.happened());
    }

    @Test
    public void test03EventHappendsEverySecondTime() {
        event.setProbability(50);
        for (int i = 1; i < 11; i++) {
            Boolean happened = event.happened();
            if (i % 2 == 0)
                assertTrue(happened);
        }
    }

    @Test
    public void test04EventHappendsEveryThirdTime() {
        event.setProbability(.3333333333333333); //16 digits after zero are right precision for 1/3
        for (int i = 1; i < 18; i++) {
            Boolean happened = event.happened();
            if (i % 3 == 0)
                assertTrue(happened);
        }
    }

    @Test
    public void test05EventProbabilityChanges() {
        event.setProbability(25);
        Boolean happened = false;

        for (int i = 1; i < 5; i++) { //after 4 times event happens
            happened = event.happened();
        }
        assertTrue(happened);

        event.happened();
        //now to change prob
        event.setProbability(75);
        happened = event.happened();
        assertTrue(happened); //once again it happens

        event.setProbability(2);
        happened = event.happened();
        assertFalse(happened);
        event.setProbability(99);
        happened = event.happened();
        assertTrue(happened); //now it happens for sure

        happened = event.happened();
        assertTrue(happened); //now it happens too

        happened = event.happened();
        assertFalse(happened); //now it cannot happen because lacking 1%
        assertEquals(99, event.getCurrentProgressAsPercent());
    }
}
