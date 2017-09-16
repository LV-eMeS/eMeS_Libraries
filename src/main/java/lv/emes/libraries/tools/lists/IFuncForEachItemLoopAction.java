package lv.emes.libraries.tools.lists;

/**
 * Used in lambda expressions to define body of for each item loop.
 * @param <T> type of items in list.
 * @param <I> type of item index.
 */
@FunctionalInterface
public interface IFuncForEachItemLoopAction<T, I> {
	void doAction(T item, I itemIndex);
}