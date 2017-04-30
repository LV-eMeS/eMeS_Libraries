package lv.emes.libraries.tools.lists;

import lv.emes.libraries.tools.MS_CodingTools;

/** 
 * Random item list is like bag where you can put different items which are identified as integers.
 * <br>The list has a size. The bigger size is, the more items will be stored with method <b>storeItem</b>, for example, 
 * by storing "carrots" with probability 20% in list of 50 items you actually will store 10 carrots so when trying to <b>getItem</b> from
 * the list there will be small chance to get a carrot, because there will be 40 empty values in the list.
 * <br>Purpose of this kind of list is to fill it with different kind of items that could be found with some probability.
 * <p>Public methods:
 * -storeItem 
 * -getItem
 * -shuffle
 * @version 1.0.
 * @see lv.emes.libraries.examples.MSRandomItemListExample
 */
public class MS_RandomItemList {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
	public static final int _EMPTY_ITEM = 0;
	public static final int _SMALL_LIST = 100;
	public static final int _MEDIUM_LIST = 1000;
	public static final int _LARGE_LIST = 5000;
	//exceptions
	public static class IncorrectProbabilityException extends Exception {
		private static final long serialVersionUID = 1986903532036673012L;
		public IncorrectProbabilityException(float probability) {
			super("Probability ("+ probability +") must be between 0 and 1.");
		}		
	}
	public static class NoMoreFreeSpacesException extends Exception {
		private static final long serialVersionUID = 1986903532036673011L;
		public NoMoreFreeSpacesException() {
			super("There is no more free spaces in list.");
		}		
	}
	public static class ItemTookLastSpacesException extends Exception {
		private static final long serialVersionUID = 1986903532036673010L;
		public ItemTookLastSpacesException(int lastItem) {
			super("Item (" + lastItem + ") took last spaces in list.");
		}		
	}
	
	//PRIVATE VARIABLES
	int totalItemCount, storedItemC;
	int[] items = null;

	//CONSTRUCTORS
	/**
	 * Creates random item list if size <b>listSize</b> which means that maximum count of items in this list can be <b>listSize</b>.
	 * <br>The bigger listSize is provided, the bigger will be random's precision.
	 * @param listSize size of list (recommended 100..1000 regarding of count of different items).
	 */
	public MS_RandomItemList(int listSize) {
		if (listSize > 0) {
			storedItemC = 0;
			totalItemCount = listSize;
			items = new int[listSize]; //set the size of array
			for (int i = 0; i < totalItemCount; i++)
				items[i] = _EMPTY_ITEM; //initialize empty values
		}
	}
	
	//PUBLIC METHODS
	/**
	 * Stores an item to the random item list. 
	 * <p>Actually exceptions shouldn't appear if programmer did calculations correctly. 
	 * @param item id of an item or simply number to recognize item through many items.
	 * @param probability chance of getting an item from the list. 
	 * @throws IncorrectProbabilityException when not 0 &lt; <b>probability</b> &lt;= 1.
	 * @throws NoMoreFreeSpacesException when list is already full.
	 * @throws ItemTookLastSpacesException when list was almost full and last item had too big probability to add item fully.
	 */
	public void storeItem(int item, float probability) 
			throws IncorrectProbabilityException, NoMoreFreeSpacesException, ItemTookLastSpacesException {
	  if (probability < 0 || probability > 1) 
		  throw new IncorrectProbabilityException(probability);

	  int freeSpaces = totalItemCount - storedItemC;
	  if (freeSpaces == 0) 
		  throw new NoMoreFreeSpacesException();
	  
	  int itemsToAddCount = Math.round(probability * totalItemCount);
	  boolean throwTookLastSpacesException = false;
	  
	  if (itemsToAddCount > freeSpaces) { //for example, list has 9/10 items and we are trying to add 2 more items
		  //this will add only 1 remaining item to the list and throw and exception after that
		  throwTookLastSpacesException = true;
		  itemsToAddCount = freeSpaces;
	  }

	  //put items to the list one by one
	  while (itemsToAddCount > 0) {
		  items[this.storedItemC] = item;
		  storedItemC++;
		  itemsToAddCount--;
	  }
	  
	  //when everything is done time to throw an exception
	  if (throwTookLastSpacesException) 
		  throw new ItemTookLastSpacesException(item); 
	}
	
	@Override
	public String toString() {
		if (items != null) {
			StringBuilder sb = new StringBuilder();
			for (int itm : items) 
				sb.append(itm);
			return sb.toString();
		} else
			return "";
	}

	/**
	 * Shuffles the stored items to make different order.
	 */
	public void shuffle() {
		if (items != null) {
			for (int i = items.length - 1; i > 0; i--) {
				int index = MS_CodingTools.randomNumber(0, totalItemCount-1);
				// Simple swap
				int a = items[index];
				items[index] = items[i];
				items[i] = a;
			}
		}
	}
	
	/**
	 * Picks 1 random item from the list.
	 * @return 1 item from stored items. If ; <b>_EMPTY_ITEM</b> if list is empty or
	 */
	public int getItem() {
		if (items != null)
			return items[MS_CodingTools.randomNumber(0, totalItemCount-1)];
		else
			return _EMPTY_ITEM;
	}
}