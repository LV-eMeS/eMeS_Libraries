package lv.emes.libraries.tools.lists;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_CodingUtils;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Meant for replacing default methods of eMeS list interfaces for easier code reuse.
 * <p>Static Methods:
 * <ul>
 * <li>forEachItem</li>
 * <li>forEach</li>
 * <li>reverseList</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_ListActionWorker {

    private MS_ListActionWorker() {
    }

    public static <T> void forEachItem(MS_IterableListWithItems<T, Integer> list, int startFromIndex, int endIndex, IFuncForEachItemLoopAction<T, Integer> action) {
        //range check for indexes that fits in list size and are correct to perform for loop
        int maxIndex = list.count() - 1;
        if (endIndex < startFromIndex) { //make swap
            int swapper = startFromIndex;
            startFromIndex = endIndex;
            endIndex = swapper;
        }

        if (MS_CodingUtils.inRange(startFromIndex, 0, maxIndex) &&
                MS_CodingUtils.inRange(endIndex, startFromIndex, maxIndex)) {
            list.setBreakOngoingForLoop(false);
            if (action != null)
                for (int i = startFromIndex; i <= endIndex; i++) {
                    T itm = list.get(i);
                    action.doAction(itm, i);
                    if (list.getBreakOngoingForLoop())
                        break;
                }
        }
    }

    public static <T> void forEachItem(MS_IterableListWithItems<T, Integer> list, int startFromIndex, IFuncForEachItemLoopAction<T, Integer> action) {
        forEachItem(list, startFromIndex, list.count() - 1, action);
    }

    public static <T> void forEachItem(MS_IterableListWithItems<T, Integer> list, IFuncForEachItemLoopAction<T, Integer> action) {
        forEachItem(list, 0, action);
    }

    /**
     * Iterates through iterable elements and performs given action <b>action</b> while <b>breakLoop</b> flag,
     * which is passed as second argument of <b>action</b> bi-consumer is <b>false</b>.
     *
     * @param iterable NonNull iterable collection of elements of type <b>T</b>.
     * @param action   NonNull consumer, which accepts iterable element of type <b>T</b> and flag of type {@link AtomicBoolean}, with
     *                 initial value <b>false</b>. Iterating will continue unless the value of this flag will be set to
     *                 <b>true</b>, which will be signal to break iterating and thus next element will not be iterated.
     * @param <T>      type of iterable elements.
     */
    public static <T> void forEach(Iterable<T> iterable, BiConsumer<T, AtomicBoolean> action) {
        Objects.requireNonNull(iterable);
        Objects.requireNonNull(action);

        AtomicBoolean breakLoop = new AtomicBoolean(false);
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext() && !breakLoop.get()) {
            action.accept(iter.next(), breakLoop);
        }
    }

    /**
     * Returns new list (passed as second parameter <b>targetList</b>), which will hold elements of specified list,
     * but in reverse order.
     * Those elements are still references to list's actual elements, so editing will change those, although
     * removing elements from this list will not affect original list.
     *
     * @param original list to reverse.
     * @param targetList    empty list that will be filled with elements from <b>original</b>, but in reverse order.
     * @param <T>      type of elements inside list.
     * @param <X>      type of list filled with original list's elements in reverse order.
     * @return new iterable list instance or null if original list was null.
     */
    public static <T, X extends MS_ListActions<T>> X reverseList(MS_ListActions<T> original, X targetList) {
        if (original == null) return null;
        if (targetList == null || targetList.size() > 0)
            throw new MS_BadSetupException("Second parameter of this method must be an empty list, but list is: " + targetList);

        original.forEachItem((originalElement, ind) -> targetList.insert(0, originalElement));
        return targetList;
    }
}
