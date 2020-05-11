
public abstract class StringSearch {

	protected String text;
	
	public int compareCount = 0;
	
	public StringSearch(String text) {
		this.text = text;
	}
	
	public void resetCompareCount() {
		compareCount = 0;
	}
	
	public int getCompareCount() {
		return compareCount;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public boolean compare(char a, char b) {
		// used to be able to count the number of comparisons a search makes
		compareCount++;
		return a == b;
	}
	
	public abstract int search(String pattern);
}
