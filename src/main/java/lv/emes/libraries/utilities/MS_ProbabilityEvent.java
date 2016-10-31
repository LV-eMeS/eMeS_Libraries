package lv.emes.libraries.utilities;

/**
 * Sometimes we need to test, if some event with defined probability happens. 
 * This is easy way to define events and handle their happening with some probability.
 * -happened
 * -changeProbability
 * @author eMeS
 * @version 1.1.
 * @see lv.emes.libraries.examples.MSProbabilityEventExample
 */
public class MS_ProbabilityEvent {
	private float probability = 0;
	private float currentProgress = 0;
	
	/**
	 * Creates event with defined probability to happen.
	 * @param probabilityPercent 0..100, if violated then maximum or minimum regarding of to which side.
	 */
	public MS_ProbabilityEvent(int probabilityPercent) {
		changeProbability(probabilityPercent);
	}
	
	/**
	 * Creates event with defined probability to happen.
	 * @param probability 0.0..1.0, if violated then maximum or minimum regarding of to which side.
	 */
	public MS_ProbabilityEvent(float probability) {
		changeProbability(probability);
	}
	
	/**
	 * Tests, if event happened and increase current progress so next time there will be greater chance for event to happen.
	 * @return true if event happened, false, if not.
	 */
	public boolean happened() {
		currentProgress += probability;
		if (currentProgress >= 1) {
			currentProgress -= 1;
			return true;
		}
		else
			return false; 
	}
	
	/**
	 * Sometimes probability of event happening may change due to other condition changes.
	 * @param newProbabilityPercent 0..100, if violated then maximum or minimum regarding of to which side.
	 */
	public void changeProbability(int newProbabilityPercent) {
		if (newProbabilityPercent < 0)
			probability = 0;
		else
			if (newProbabilityPercent > 100)
				probability = 1;
			else
				probability = (float) newProbabilityPercent / 100;
	}
	
	/**
	 * Sometimes probability of event happening may change due to other condition changes.
	 * @param probability 0.0..1.0, if violated then maximum or minimum regarding of to which side.
	 */
	public void changeProbability(float probability) {
		if (probability < 0)
			this.probability = 0;
		else
			if (probability > 1)
				this.probability = 1;
			else
				this.probability = probability;
	}
	
	/**
	 * @return probability of event to happen with precision of 2 decimals.
	 */
	public int getProbabilityAsPercent() {
		return Math.round(probability * 100);
	}

	/**
	 * @return percentage of event made so far rounded to precision of 2 decimals.
	 */
	public int getCurrentProgressAsPercent() {
		return Math.round(currentProgress * 100);
	}
	
	/**
	 * @return probability of event to happen with precision of 2 decimals.
	 */
	public float getProbability() {
		return probability;
	}
	
	/**
	 * @return event made so far absolute value (0..1).
	 */
	public float getCurrentProgress() {
		return currentProgress;
	}
}
