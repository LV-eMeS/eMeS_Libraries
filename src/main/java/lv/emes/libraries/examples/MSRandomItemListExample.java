package lv.emes.libraries.examples;

import lv.emes.libraries.tools.lists.MS_RandomItemList;
import lv.emes.libraries.tools.lists.MS_RandomItemList.IncorrectProbabilityException;
import lv.emes.libraries.tools.lists.MS_RandomItemList.ItemTookLastSpacesException;
import lv.emes.libraries.tools.lists.MS_RandomItemList.NoMoreFreeSpacesException;

public class MSRandomItemListExample {
	public static final int APPLE = 1;
	public static final int ORANGE = 2;
	public static final int PINEAPPLE = 3;
	public static final int BANANA = 4;
	
	public static void main(String[] args) {
		MS_RandomItemList listOfFruits = new MS_RandomItemList(MS_RandomItemList.SMALL_LIST);
		try {
			listOfFruits.storeItem(APPLE, .4f);
			listOfFruits.storeItem(ORANGE, .2f); //40% of list is free after this action	
			listOfFruits.storeItem(PINEAPPLE, .05f); //35% of list is free after this action
			listOfFruits.storeItem(BANANA, .30f); //5% of list is free after this action	
			//the remaining 5% will stay as empty part of the list
		} catch (IncorrectProbabilityException e) {
			System.out.println(e.getMessage());
		} catch (NoMoreFreeSpacesException e) {
			System.out.println(e.getMessage());
		} catch (ItemTookLastSpacesException e) {
			System.out.println(e.getMessage());
		}	//60% of list is free after this action
		
		System.out.println("Before shuffling: " + listOfFruits); //contents of list before shuffling
		listOfFruits.shuffle(); //for better random mechanism shuffle the list after it is filled.
		System.out.println("After shuffling: " + listOfFruits); //contents of list after shuffling
		
		for (int i = 1; i <= 5; i++) {
			int item = listOfFruits.getItem();
			switch (item) {
			case APPLE:
				System.out.println("Got apple.");
				break;
			case ORANGE:
				System.out.println("Got orange.");
				break;
			case PINEAPPLE:
				System.out.println("Got pineapple.");
				break;
			case BANANA:
				System.out.println("Got banana.");
				break;
			case MS_RandomItemList.EMPTY_ITEM:
				System.out.println("Got nothing...");
				break;
			}		//case end	
		} //for end
		
		//now to test, how many percentage of apples, oranges, pineaples and bananas we got of 1000 hits.
		int appleCount = 0;
		int orangeCount = 0;
		int pineappleCount = 0;
		int bananaCount = 0;
		
		for (int i = 1; i <= 1000; i++) {
			switch (listOfFruits.getItem()) {
			case APPLE:
				appleCount++;
				break;
			case ORANGE:
				orangeCount++;
				break;
			case PINEAPPLE:
				pineappleCount++;
				break;
			case BANANA:
				bananaCount++;
				break;
			}
		}
		System.out.println("Out of 1000 hits we got: " + appleCount + " apples, " + orangeCount + " oranges, " + pineappleCount + " pineapples and " + bananaCount + " bananas. ");
	}
}
