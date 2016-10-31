package lv.emes.libraries.tools.lists;

@FunctionalInterface
public interface IFuncSomeAction<T> {
	void doAction(T item, int itemIndex);
}