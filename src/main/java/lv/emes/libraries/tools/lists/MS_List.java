package lv.emes.libraries.tools.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static lv.emes.libraries.utilities.MS_CodingUtils.inRange;

/** 
 * Purpose of this class is to make lists of different objects.
 * @version 2.0.
 */
public class MS_List<T> extends ArrayList<T> implements MS_ContactableList<T, Integer> {

	private boolean flagForLoopBreaking;
	
	//PRIVATE METHODS
	private boolean listIsEmptyOrIndexNotInRange(int aIndex) {
		return (count() == 0) || ! inRange(aIndex, 0, this.count()-1);
	}
	//PUBLIC METHODS

	/**
	 * Converts array to eMeS list.
	 * @param aArray array with T type of objects that will fill the list.
	 * @param <T> type of objects that list will contain.
	 * @return list of objects.
	 */
	public static <T> MS_List<T> newInstance(T[] aArray) {
		MS_List<T> res = new MS_List<>();
		Collections.addAll(res, aArray);
		return res;
	}

	/**
	 * Converts list to eMeS list.
	 * @param list a list with T type of objects that will fill the eMeS list.
	 * @param <T> type of objects that list will contain.
	 * @return list of objects.
	 */
	public static <T> MS_List<T> newInstance(List<T> list) {
		MS_List<T> res = new MS_List<>();
		if (list != null) res.addAll(list);
		return res;
	}
	
	@Override
	public int count() {
		return this.size();
	}

	@Override
	public int length() {
		return count();
	}

	@Override
	public T get(Integer aIndex) {
		return super.get(aIndex);
	}

	@Override
	public void breakOngoingForLoop() {
		setBreakOngoingForLoop(true);
	}

	@Override
	public void setBreakOngoingForLoop(boolean value) {
		flagForLoopBreaking = value;
	}

	@Override
	public boolean getBreakOngoingForLoop() {
		return flagForLoopBreaking;
	}

	@Override
	public void forEachItem(IFuncForEachItemLoopAction<T, Integer> action) {
		MS_ListActionWorker.forEachItem(this, action);
	}

	@Override
	public void forEachItem(Integer startFromIndex, IFuncForEachItemLoopAction<T, Integer> action) {
		MS_ListActionWorker.forEachItem(this, startFromIndex, action);
	}

	@Override
	public void forEachItem(Integer startFromIndex, Integer endIndex, IFuncForEachItemLoopAction<T, Integer> action) {
		MS_ListActionWorker.forEachItem(this, startFromIndex, endIndex, action);
	}

	/**
	 * Converts list to array of T kind of objects.
	 */
	public Object[] toArray() {
		int c = this.count();
		Object[] res = new Object[c];
		for (int i=0; i < c; i++)
			res[i] = this.get(i);
		return res;		
	}

	@Override
	public void concatenate(List<T> otherList) {
		this.addAll(otherList);
	}
}
