package lv.emes.libraries.tools.lists;

/**
 * Used in lambda expressions to define body of for each item loop.
 * @param <T> type of items in list.
 */
@FunctionalInterface
public interface IFuncForEachItemLoopAction<T> {
	void doAction(T item, int itemIndex);
}