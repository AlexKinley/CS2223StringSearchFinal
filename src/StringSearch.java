
public abstract class StringSearch {

	protected String text;
	
	public int compareCount = 0;
	
	/**
	 * Constructor with text to search in
	 * @param text String to search in
	 */
	public StringSearch(String text) {
		this.text = text;
	}
	
	public void resetCompareCount() {
		compareCount = 0;
	}
	
	public int getCompareCount() {
		return compareCount;
	}
	
	/**
	 * set the text to be searched in
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/** 
	 * Same as a == b, but also increments compareCount
	 * @param a
	 * @param b
	 * @return a == b
	 */
	public boolean compare(char a, char b) {
		// used to be able to count the number of comparisons a search makes
		compareCount++;
		return a == b;
	}
	
	public abstract int search(String pattern);
}
