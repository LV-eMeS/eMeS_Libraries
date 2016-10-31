package lv.emes.libraries.examples;

import lv.emes.libraries.utilities.MS_ProbabilityEvent;

public class MSProbabilityEventExample {

	public static void main(String[] args) {
		MS_ProbabilityEvent tossATailOfCoin = new MS_ProbabilityEvent(50);
		System.out.println("First toss: " + tossATailOfCoin.happened());
		System.out.println("Second toss: " + tossATailOfCoin.happened());
		System.out.println("Third toss: " + tossATailOfCoin.happened());
		System.out.println("Fourth toss: " + tossATailOfCoin.happened());
		System.out.println("======================================================================================================================");		
		//======================================================================================================================================
		MS_ProbabilityEvent a5PercentEventToHappen = new MS_ProbabilityEvent(.05f);
		for (int i = 1; i <= 18; i++)
			a5PercentEventToHappen.happened();
		System.out.println("After 19 times: " + a5PercentEventToHappen.happened());
		System.out.println("After 20th time: " + a5PercentEventToHappen.happened());
		System.out.println("After 21st time: " + a5PercentEventToHappen.happened());		
		//======================================================================================================================================
		System.out.println("======================================================================================================================");		
		int redPokeballPikachuCatchingRate = 20; //20% chance to catch pikachu with red pokeball
		int greatPokeballPikachuCatchingRate = 50;
		float masterPokeballPikachuCatchingRate = 1.0f; //Master ball is full proof
		MS_ProbabilityEvent catchAPikachu = new MS_ProbabilityEvent(redPokeballPikachuCatchingRate); 
		System.out.println("--Catching Pikachu...");
		System.out.println("Caught with Red ball (first throw): " + catchAPikachu.happened());
		
		catchAPikachu.changeProbability(greatPokeballPikachuCatchingRate); //lets try to throw great ball
		System.out.println("Caught with Great ball (second throw): " + catchAPikachu.happened());
		System.out.println("Caught with Great ball (third throw): " + catchAPikachu.happened());
		//so one Pikachu is caught. Lets have a try to catch another one
		System.out.println("--So one Pikachu is caught. Lets have a try to catch another one!");
		System.out.println("Caught with Great ball (first throw): " + catchAPikachu.happened());
		System.out.println("Caught with Great ball (second throw): " + catchAPikachu.happened());
		
		System.out.println("--We have 2 Pikachu already. Pretty awesome! Lets have a try to catch yet another one! This time we will use Master ball.");
		catchAPikachu.changeProbability(masterPokeballPikachuCatchingRate); //100% probability for event to happen
		System.out.println("Caught with Master ball (first throw): " + catchAPikachu.happened());
 		System.out.println("======================================================================================================================");		
		//======================================================================================================================================
	}

}
