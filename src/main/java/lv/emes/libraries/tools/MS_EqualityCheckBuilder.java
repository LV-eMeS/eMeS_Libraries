package lv.emes.libraries.tools;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Class to check, whether pair of objects with similar structure (like DTO and respective domain) are equal.
 * In addition to <b>EqualsBuilder</b> standard methods, a new methods, that allows to do custom comparisons, are implemented.
 * <p><u>Note</u>: this class is meant for Unit testing purposes only.
 * <p><b>MS_EqualityCheckBuilder</b> can work in two modes:
 * <ol>
 * <li>With "interruption" in form of an assertion error;</li>
 * <li>Without "interruption" (as it is in <b>EqualsBuilder</b>).</li>
 * </ol>
 * <p>Interruption mean that, if by appending some object pair class detects that objects are not equal, an runtime
 * exception is thrown signalling that this is critical and objects should've be equal.
 * To switch between those modes change value of parameter <b>mustBeEqual</b>!
 * <p><u>Example of use (<b>Without</b> interruption)</u>:<br>
 * <pre>
 * <code>
 * //Just an imaginary TestObjects for an example
 * TestObjectDomain domainObj = new ObjectDomain();
 * TestObjectDTO dtoObj = new TestObjectDTO();
 * MS_EqualityCheckBuilder checkThatDomainAndDTO = new MS_EqualityCheckBuilder(false)
 * .append(domainObj.getSomeLocalDate(), dtoObj.getSomeLocalDate())
 * .append( //custom method to compare two objects
 * domainObj.getSomeCustomObject(), dtoObj.getSomeCustomObject(),
 * (domainCustomObject, dtoCustomObject) -&gt; {
 * return new MS_EqualityCheckBuilder(false)
 * .append(domainCustomObject.getSomeInt(), dtoCustomObject.getSomeInt())
 * .areEqual();
 * }
 * )
 * .appendLists( //custom method to compare two lists
 * domainObj.getSomeListOfObjects(), dtoObj.getSomeListOfObjects(),
 * (domainListElement, dtoListElement) -&gt; {
 * return new MS_EqualityCheckBuilder(false)
 * .append(domainListElement.getSomeInt(), dtoListElement.getSomeInt())
 * .areEqual();
 * }
 * );
 * assertTrue(checkThatDomainAndDTO.areEqual());
 * </code>
 * </pre>
 * <p><u>Example of use (<b>With</b> interruption)</u>:<br>
 * <pre>
 * <code>
 * MS_EqualityCheckBuilder notEqualBuilder = new MS_EqualityCheckBuilder(true)
 * .append(new Object(), new Object(),
 * (firstObj, secondObj) -&gt; {
 * return false; //mock object comparison failure
 * }
 * );
 * //right after append call will finish its work an EqualityCheckException will raise
 * //that is because we defined it to be so by using IComparisonAlgorithm
 * notEqualBuilder.append(true, true); //this will not even execute because of AssertionError
 * assertTrue(checkThatDomainAndDTO.areEqual());
 * </code>
 * </pre>
 *
 * @author eMeS
 * @see AssertionError
 */
public class MS_EqualityCheckBuilder extends EqualsBuilder {

    private boolean mustBeEqual = false;
    /**
     * Needed only when performing list appending to show index of element which failed equality check.
     */
    private String assertionErrorMessage = null;

    /**
     * Constructs an equality check builder which will be working in
     * "{@link MS_EqualityCheckBuilder without interruptions}" mode.
     *
     * @see MS_EqualityCheckBuilder
     */
    public MS_EqualityCheckBuilder() {
    }

    /**
     * Constructs an equality check builder.
     *
     * @param mustBeEqual if <b>true</b>: states that class will be working in interruption mode.
     *                    By default it's false.
     * @see MS_EqualityCheckBuilder
     */
    public MS_EqualityCheckBuilder(boolean mustBeEqual) {
        this.mustBeEqual = mustBeEqual;
    }

    /**
     * Checks, if two objects are equal.
     * Firstly null checks are performed so pair of null objects are considered as equal.
     * Secondly implementation of <b>comparisonAlgorithm</b> method is performed to calculate equality.
     *
     * @param comparisonAlgorithm lambda in form of <code>
     *                            (firstObject, secondObject) -&gt; {return /&#42;
     *                            expression that compares firstObject and secondObject equality.
     *                            &#42;/ }
     *                            </code>
     * @param f                   first object of the object pair.
     * @param s                   second object of the object pair.
     * @param <F>                 type of first object.
     * @param <S>                 type of second object.
     * @return true if first object is equal to second object or both are null;
     * false if first object's value differs from second object's value or one of them is null.
     */
    public <F, S> MS_EqualityCheckBuilder append(F f, S s, IComparisonAlgorithm<F, S> comparisonAlgorithm) {
        if (needToPerformComparisonAfterNullChecks(f, s)) {
            super.setEquals(comparisonAlgorithm.areEqual(f, s));
        }
        performMandatoryEqualityAssurance(f, s);
        return this;
    }

    /**
     * Checks if two lists are equal.
     * As list is just an collection of elements, list of different type equality checking can be divided in 3 parts:
     * <ol>
     * <li>Perform null checks;</li>
     * <li>Check if list sizes are equal;</li>
     * <li>Compare all list elements if they match another list's elements.</li>
     * </ol>
     * This method works similarly to <b>MS_EqualityCheckBuilder.append(F, S, IComparisonAlgorithm)</b>.
     * Only difference is that <b>comparisonAlgorithm</b> participates only when looping through both list elements
     * is started. But this algorithm is performed for each single iteration that will be made.
     *
     * @param comparisonAlgorithm lambda in form of <code>
     *                            (firstObject, secondObject) -&gt; {return /&#42;
     *                            expression that compares firstObject and secondObject equality.
     *                            &#42;/ }
     *                            </code>
     * @param f                   first object list.
     * @param s                   second object list.
     * @param <F>                 type of objects in first list.
     * @param <S>                 type of objects in second list.
     * @return true if first list is equal to second list or both are null;
     * false if first list's contents differs from second list's contents or one of them is null.
     * @see MS_EqualityCheckBuilder#append(Object, Object, IComparisonAlgorithm)
     */
    public <F, S> MS_EqualityCheckBuilder appendLists(List<F> f, List<S> s, IComparisonAlgorithm<F, S> comparisonAlgorithm) {
        if (needToPerformComparisonAfterNullChecks(f, s)) {
            boolean sizesEqual = f.size() == s.size();
            if (sizesEqual) {
                for (int i = 0; i < f.size(); i++) {
                    F elementOfFirstList = f.get(i);
                    S elementOfSecondList = s.get(i);
                    assertionErrorMessage = "at element index: " + i;
                    this.append(elementOfFirstList, elementOfSecondList, comparisonAlgorithm);

                    if (!this.areEqual()) {
                        break;
                    }
                }
            } else {
                super.setEquals(false);
                performMandatoryEqualityAssurance(f, s);
            }
        } else {
            performMandatoryEqualityAssurance(f, s); //if null check failed (one of lists is null)
        }
        assertionErrorMessage = null;
        return this;
    }

    /**
     * Checks if two maps with same key type are equal.
     * As map is just an collection of elements, map of different type equality checking can be divided in 3 parts:
     * <ol>
     * <li>Perform null checks;</li>
     * <li>Check if map sizes are equal;</li>
     * <li>Compare all map values if they match another map's values.</li>
     * </ol>
     * This method works similarly to <b>MS_EqualityCheckBuilder.append(F, S, IComparisonAlgorithm)</b>.
     * Only difference is that <b>comparisonAlgorithm</b> participates only when looping through both map elements
     * is started. But this algorithm is performed for each single iteration that will be made.
     *
     * @param comparisonAlgorithm lambda in form of <code>
     *                            (firstObject, secondObject) -&gt; {return /&#42;
     *                            expression that compares firstObject and secondObject equality.
     *                            &#42;/ }
     *                            </code>
     * @param f                   first map.
     * @param s                   second map.
     * @param <ID>                type of keys in both maps.
     * @param <F>                 type of objects (as values) in first map.
     * @param <S>                 type of objects (as values) in second map.
     * @return true if first map is equal to second map or both are null;
     * false if first map's contents differs from second map's contents or one of them is null.
     * @see MS_EqualityCheckBuilder#append(Object, Object, IComparisonAlgorithm)
     */
    public <ID, F, S> MS_EqualityCheckBuilder appendMaps(Map<ID, F> f, Map<ID, S> s, IComparisonAlgorithm<F, S> comparisonAlgorithm) {
        if (needToPerformComparisonAfterNullChecks(f, s)) {
            boolean sizesEqual = f.size() == s.size();
            if (sizesEqual) {
                for (Map.Entry<ID, F> firstMapEntry : f.entrySet()) {
                    F elementOfFirstMap = firstMapEntry.getValue();
                    S elementOfSecondMap = s.get(firstMapEntry.getKey());
                    assertionErrorMessage = "at first map element key: " + firstMapEntry.getKey();
                    this.append(elementOfFirstMap, elementOfSecondMap, comparisonAlgorithm);
                    if (!this.areEqual()) {
                        break;
                    }
                }
            } else {
                super.setEquals(false);
                performMandatoryEqualityAssurance(f, s);
            }
        } else {
            performMandatoryEqualityAssurance(f, s); //if null check failed (one of maps is null)
        }
        assertionErrorMessage = null;
        return this;
    }

    /**
     * Checks if two maps with different key types are equal.
     * As map keys and values are just an collection of elements, map of different type for keys and values
     * equality checking can be divided in 4 parts:
     * <ol>
     * <li>Perform null checks;</li>
     * <li>Check if map sizes are equal;</li>
     * <li>Compare all map keys if they match another map's keys.</li>
     * <li>Compare all map values if they match another map's values.</li>
     * </ol>
     * This method is heavier than {@link MS_EqualityCheckBuilder#appendMaps(Map, Map, IComparisonAlgorithm)}
     * because it iterates through all entries in first map and performs
     * {@link java.util.stream.Stream#anyMatch(Predicate)} to detect equal key - value pair in second map (ofcourse,
     * by performing given comparison algorithm for both keys and values).
     *
     * @param keyComparisonAlgorithm lambda in form of <code>
     *                            (firstKey, secondKey) -&gt; {return /&#42;
     *                            expression that compares firstKey and secondKey equality.
     *                            &#42;/ }
     *                            </code>
     * @param valueComparisonAlgorithm lambda in form of <code>
     *                            (firstValue, secondValue) -&gt; {return /&#42;
     *                            expression that compares firstValue and secondValue equality.
     *                            &#42;/ }
     *                            </code>
     * @param f                   first map.
     * @param s                   second map.
     * @param <FID>               type of keys of first map.
     * @param <SID>               type of keys of second map.
     * @param <F>                 type of objects (as values) in first map.
     * @param <S>                 type of objects (as values) in second map.
     * @return true if first map is equal to second map or both are null;
     * false if first map's contents differs from second map's contents or one of them is null.
     * @see MS_EqualityCheckBuilder#append(Object, Object, IComparisonAlgorithm)
     */
    public <FID, SID, F, S> MS_EqualityCheckBuilder appendMaps(Map<FID, F> f, Map<SID, S> s,
                                                            IComparisonAlgorithm<FID, SID> keyComparisonAlgorithm,
                                                            IComparisonAlgorithm<F, S> valueComparisonAlgorithm) {
        if (needToPerformComparisonAfterNullChecks(f, s)) {
            boolean sizesEqual = f.size() == s.size();
            if (sizesEqual) {
                for (Map.Entry<FID, F> firstMapEntry : f.entrySet()) {
                    boolean foundSimilarEntry = s.entrySet().stream().anyMatch(secondMapEntry ->
                            keyComparisonAlgorithm.areEqual(firstMapEntry.getKey(), secondMapEntry.getKey()) &&
                                    valueComparisonAlgorithm.areEqual(firstMapEntry.getValue(), secondMapEntry.getValue())
                    );
                    this.setEquals(foundSimilarEntry);

                    if (!this.areEqual()) {
                        assertionErrorMessage = "at first map element key: " + firstMapEntry.getKey();
                        performMandatoryEqualityAssurance(f, s);
                        break;
                    }
                }
            } else {
                super.setEquals(false);
                performMandatoryEqualityAssurance(f, s);
            }
        } else {
            performMandatoryEqualityAssurance(f, s); //if null check failed (one of maps is null)
        }
        assertionErrorMessage = null;
        return this;
    }

    protected <F, S> boolean needToPerformComparisonAfterNullChecks(F f, S s) {
        if (isEquals()) {
            if (f == null && s == null) {
                super.setEquals(true);
                return false;
            } else {
                boolean stillNeedToCompareObjectValuesAfterThis = (f != null && s != null);
                super.setEquals(stillNeedToCompareObjectValuesAfterThis);
                return stillNeedToCompareObjectValuesAfterThis;
            }
        } else {
            return false;
        }
    }

    /**
     * In case flag <b>mustBeEqual</b> is true an assertion check is performed in order to raise an assertion error if
     * objects aren't equal to trace stack, which objects differs.
     *
     * @param f   first object.
     * @param s   second object.
     * @param <F> type of first object.
     * @param <S> type of second object.
     * @see AssertionError
     */
    private <F, S> void performMandatoryEqualityAssurance(F f, S s) {
        if (mustBeEqual && !isEquals()) {
            Assert.assertEquals(assertionErrorMessage, f, s);
        }
    }

    @Override
    public MS_EqualityCheckBuilder append(Object lhs, Object rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(long lhs, long rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(int lhs, int rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(short lhs, short rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(char lhs, char rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(byte lhs, byte rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(double lhs, double rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(float lhs, float rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(boolean lhs, boolean rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(Object[] lhs, Object[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(long[] lhs, long[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(int[] lhs, int[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(short[] lhs, short[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(char[] lhs, char[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(byte[] lhs, byte[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(double[] lhs, double[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(float[] lhs, float[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    @Override
    public MS_EqualityCheckBuilder append(boolean[] lhs, boolean[] rhs) {
        super.append(lhs, rhs);
        performMandatoryEqualityAssurance(lhs, rhs);
        return this;
    }

    public boolean getMustBeEqual() {
        return mustBeEqual;
    }

    public MS_EqualityCheckBuilder setMustBeEqual(boolean mustBeEqual) {
        this.mustBeEqual = mustBeEqual;
        return this;
    }

    /**
     * A synonym of {@link EqualsBuilder#isEquals() EqualsBuilder::isEquals}
     *
     * @return true if the fields that have been checked are all equal.
     */
    public boolean areEqual() {
        return super.isEquals();
    }

    /**
     * An algorithm that defines behavior of two different object comparison using MS_EqualityCheckBuilder.
     * <br>Algorithm can be easily defined using lambda expressions.
     *
     * @param <F> type of first object.
     * @param <S> type of second object.
     */
    @FunctionalInterface
    public interface IComparisonAlgorithm<F, S> {
        boolean areEqual(F f, S s);
    }
}
