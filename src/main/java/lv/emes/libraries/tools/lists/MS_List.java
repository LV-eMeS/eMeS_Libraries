package lv.emes.libraries.tools.lists;

import java.util.ArrayList;

import static lv.emes.libraries.tools.MS_Tools.inRange;
/** 
 * Purpose of this class is to make lists of different objects. It's possible to perambulate list using methods from interface <b>IPerambulateListActions</b>.
 * @version 1.1.
 * @see IPerambulateListActions
 */
public class MS_List<T> extends ArrayList<T> implements IPerambulateListActions<T> {
	/**
	 * Makes it possible to set index for the current element.
	 */
	public int indexOfCurrent = -1;
	
	//PRIVĀTĀS METODES
	private boolean listIsEmptyOrIndexNotInRange(int aIndex) {
		return (count() == 0) || ! inRange(aIndex, 0, this.count()-1);
	}
	//PUBLISKĀS METODES
	
	/**
	 * Converts array to list.
	 * @param aArray array with T type of objects that will fill the list.
	 */
	public static MS_List<Object> newInstance(Object[] aArray) {	
		MS_List<Object> res = new MS_List<>();
		for (Object obj : aArray)
			res.add(obj);
		return res;
	}
	
	@Override
	public int count() {
		return this.size();
	}

	@Override
	public int getIndexOfCurrent() {		
		return indexOfCurrent;	
	}

	@Override
	public void setIndexOfCurrent(int aIndexOfCurrent) {
		if (listIsEmptyOrIndexNotInRange(aIndexOfCurrent))
			indexOfCurrent = -1;
		else 
			indexOfCurrent = aIndexOfCurrent;
	}

	@Override
	public T current() {
		return this.get(indexOfCurrent);
	}

	@Override
	public void first() {
		setIndexOfCurrent(0);	
	}

	@Override
	public void last() {
		setIndexOfCurrent(count()-1);			
	}

	@Override
	public void next() {
		setIndexOfCurrent(indexOfCurrent+1);	
	}

	@Override
	public void prev() {
		setIndexOfCurrent(indexOfCurrent-1);	
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
}
