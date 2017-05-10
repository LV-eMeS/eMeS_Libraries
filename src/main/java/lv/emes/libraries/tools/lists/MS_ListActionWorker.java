package lv.emes.libraries.tools.lists;

import lv.emes.libraries.tools.MS_CodingTools;

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

    public static <T> void forEachItem(IBaseListWithItems<T> list, int startFromIndex, IFuncSomeAction<T> action) {
        //range check for indexes that fits in list size
        if (MS_CodingTools.inRange(startFromIndex, 0, list.count()-1)) {
            list.setBreakDoWithEveryItem(false);
            if (action != null)
                for (int i = startFromIndex; i < list.count(); i++) {
                    T itm = list.get(i);
                    action.doAction(itm, i);
                    if (list.getBreakDoWithEveryItem())
                        break;
                }
        }
    }

    public static <T> void forEachItem(IBaseListWithItems<T> list, IFuncSomeAction<T> action) {
        forEachItem(list, 0, action);
    }
}
