package lv.emes.libraries.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_ProbabilityEventTest {

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
        assertThat(event.getCurrentProgress()).isCloseTo(0, offset(0.01));
        assertThat(event.getProbability()).isCloseTo(0, offset(0.01));
        assertThat(event.getCurrentProgressAsPercent()).isEqualTo(0);
        assertThat(event.getProbabilityAsPercent()).isEqualTo(0);

        event.setProbability(5);
        assertThat(event.getProbability()).isCloseTo(0.05, offset(0.001));
        assertThat(event.getProbabilityAsPercent()).isEqualTo(5);

        event.happened(); //try to make it happen 2 times to increase current progress
        assertThat(event.getCurrentProgressAsPercent()).isEqualTo(5);
        event.happened();
        assertThat(event.getCurrentProgress()).isCloseTo(0.1, offset(0.01));
    }

    @Test
    public void test01EventNeverHappens() {
        for (int i = 1; i < 102; i++)
            assertThat(event.happened()).isFalse();
    }

    @Test
    public void test02EventHappensEveryTime() {
        event.setProbability(1d);
        for (int i = 1; i < 102; i++)
            assertThat(event.happened()).isTrue();
    }

    @Test
    public void test03EventHappendsEverySecondTime() {
        event.setProbability(50);
        for (int i = 1; i < 11; i++) {
            boolean happened = event.happened();
            if (i % 2 == 0)
                assertThat(happened).isTrue();
        }
    }

    @Test
    public void test04EventHappendsEveryThirdTime() {
        event.setProbability(.3333333333333333); //16 digits after zero are right precision for 1/3
        for (int i = 1; i < 18; i++) {
            boolean happened = event.happened();
            if (i % 3 == 0)
                assertThat(happened).isTrue();
        }
    }

    @Test
    public void test05EventProbabilityChanges() {
        event.setProbability(25);
        boolean happened = false;

        for (int i = 1; i < 5; i++) { //after 4 times event happens
            happened = event.happened();
        }
        assertThat(happened).isTrue();

        event.happened();
        //now to change prob
        event.setProbability(75);
        happened = event.happened();
        assertThat(happened).isTrue(); //once again it happens

        event.setProbability(2);
        happened = event.happened();
        assertThat(happened).isFalse();
        event.setProbability(99);
        happened = event.happened();
        assertThat(happened).isTrue(); //now it happens for sure

        happened = event.happened();
        assertThat(happened).isTrue(); //now it happens too

        happened = event.happened();
        assertThat(happened).isFalse(); //now it cannot happen because lacking 1%
        assertThat(event.getCurrentProgressAsPercent()).isEqualTo(99);
    }
}
