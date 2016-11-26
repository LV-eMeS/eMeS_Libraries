package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_Tools;

/**
 * Sometimes we need to test, if some event with defined probability happens. 
 * This is easy way to define events and handle their happening with some probability.
 * -happened
 * -setProbability
 * @author eMeS
 * @version 1.1.
 * @see lv.emes.libraries.examples.MSProbabilityEventExample
 */
public class MS_ProbabilityEvent {
	private Double probability = 0d;
	private Double currentProgress = 0d;
	
	/**
	 * Creates event with defined probability to happen.
	 * @param probabilityPercent 0..100, if violated then maximum or minimum regarding of to which side.
	 */
	public MS_ProbabilityEvent(int probabilityPercent) {
		setProbability(probabilityPercent);
	}
	
	/**
	 * Creates event with defined probability to happen.
	 * @param probability 0.0..1.0, if violated then maximum or minimum regarding of to which side.
	 */
	public MS_ProbabilityEvent(Double probability) {
		setProbability(probability);
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
	public void setProbability(int newProbabilityPercent) {
		if (newProbabilityPercent < 0)
			probability = 0d;
		else
			if (newProbabilityPercent > 100)
				probability = 1d;
			else
				probability = (double) newProbabilityPercent / 100;
	}
	
	/**
	 * Sometimes probability of event happening may change due to other condition changes.
	 * @param probability 0.0..1.0, if violated then maximum or minimum regarding of to which side.
	 */
	public void setProbability(Double probability) {
		if (probability < 0)
			this.probability = 0d;
		else
			if (probability > 1)
				this.probability = 1d;
			else
				this.probability = probability;
	}
	
	/**
	 * @return probability of event to happen with precision of 2 decimals.
	 */
	public int getProbabilityAsPercent() {
		double percent = MS_Tools.round(probability, 2).doubleValue() * 100;
		int res = (int) percent;
		return res;
	}

	/**
	 * @return percentage of event made so far rounded to precision of 2 decimals.
	 */
	public int getCurrentProgressAsPercent() {
		double percent = MS_Tools.round(currentProgress, 2).doubleValue() * 100;
		int res = (int) percent;
		return res;
	}
	
	/**
	 * @return probability of event to happen with precision of 2 decimals.
	 */
	public Double getProbability() {
		return probability;
	}
	
	/**
	 * @return event made so far absolute value (0..1).
	 */
	public Double getCurrentProgress() {
		return currentProgress;
	}
}
