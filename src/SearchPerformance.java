
public class SearchPerformance {

	
	public static int ONE_THOUSAND = 1000;
	public static int ONE_MILLION = 1000000;
	
	
	private long elapsedTime;
	private String searchString;
	private int pos;
	private int comparisons;
	
	public SearchPerformance(String s, long time, int index, int comps) {
		this.searchString = s;
		this.elapsedTime = time;
		this.pos = index;
		this.comparisons = comps;
	}
	
	/**
	 * Helper to give useful units to the difference between two System.getNanoTime() calls
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String nanoTimeToString(long time) {
		if (time < ONE_THOUSAND) {
			return time + "ns";
		} else if (time < ONE_MILLION) {
			return (time / ONE_THOUSAND) + "us";
		} else {
			return (time / ONE_MILLION) + "ms";
		}
	}
	
	
	
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	public String getElapsedTimeString() {
		return nanoTimeToString(elapsedTime);
	}
	
	public String getElapsedTimeMicroString() {
		return "" + (elapsedTime / ONE_THOUSAND);
	}

	public String getSearchString() {
		return searchString;
	}

	public int getPos() {
		return pos;
	}

	public int getComparisons() {
		return comparisons;
	}

	public String toString() {
		String s = "";
		s += ("pos is " + pos + "\n");
		s += ("time is " + nanoTimeToString(elapsedTime) + "\n");
		s += ("took " + comparisons + " comparisons" + "\n");
		return s;
	}
}
