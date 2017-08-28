package lv.emes.libraries.tools.lists;

import lv.emes.libraries.utilities.MS_CodingUtils;

/**
 * Meant for replacing default methods of eMeS list interfaces for easier code reuse.
 * <p>Methods:
 * <ul>
 * <li>forEachItem</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_ListActionWorker {

    public static <T> void forEachItem(IBaseListWithItems<T> list, int startFromIndex, int endIndex, IFuncForEachItemLoopAction<T> action) {
        //range check for indexes that fits in list size and are correct to perform for loop
        int maxIndex = list.count()-1;
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

    public static <T> void forEachItem(IBaseListWithItems<T> list, int startFromIndex, IFuncForEachItemLoopAction<T> action) {
        forEachItem(list, startFromIndex, list.count()-1, action);
    }

    public static <T> void forEachItem(IBaseListWithItems<T> list, IFuncForEachItemLoopAction<T> action) {
        forEachItem(list, 0, action);
    }
}
