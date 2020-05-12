import java.util.ArrayList;
import java.util.List;

public class HorspoolSearch extends StringSearch{


	
	public HorspoolSearch(String text) {
		super(text);
	}
	
	/**
	 * Creates a lookup table for how much to shift the word for horspool's
	 * algorithm Assumes the alphabet is the ascii alphabet Based on ShiftTable from
	 * page 261 of Levitin
	 * 
	 * @param pattern
	 *            String that is being searched for
	 * @return Shift lookup table
	 */
	private List<Integer> shiftTable(String pattern) {
		List<Integer> shiftTable = new ArrayList<Integer>();
		int patternLength = pattern.length();

		for (int i = 0; i < 255; i++) {
			shiftTable.add(patternLength);
		}

		for (int j = 0; j < patternLength - 1; j++) {
			char c = pattern.charAt(j);
			int val = (int) c;
			shiftTable.set(val, patternLength - 1 - j);
		}

		return shiftTable;
	}
	
	

	
	/**
	 * Returns the position of the first appearance of the pattern in the read in
	 * file. If the pattern does not appear returns -1. Based on HorspoolMatching
	 * from page 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @return Index of first occurrence of pattern in text, -1 if pattern does not
	 *         appear
	 */
	@Override
	public int search(String pattern) {
		List<Integer> shiftTable = shiftTable(pattern);

		int patternLength = pattern.length();
		int textLength = text.length();

		
		int index = patternLength - 1;

		while (index < textLength) {
			int k = 0;
			while ((k < patternLength)
					&& (compare(pattern.charAt(patternLength - 1 - k), text.charAt(index - k)))) {
				k++;
			}
			if (k == patternLength) {
				return index - patternLength + 1;
			} else {
				index += shiftTable.get(text.charAt(index));
			}
		}

		return -1;
	}
}
