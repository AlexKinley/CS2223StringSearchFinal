import java.util.ArrayList;
import java.util.List;

public class HorspoolSearch extends StringSearch {

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
			while ((k < patternLength) && (compare(pattern.charAt(patternLength - 1 - k), text.charAt(index - k)))) {
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

	/**
	 * Returns the position of the first appearance of the pattern (case insensitive)
	 * in the class member text. If the pattern does not appear returns -1. Based
	 * on HorspoolMatching from page 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @return Index of first occurrence of pattern in text, -1 if pattern does not
	 *         appear
	 */
	public int searchCaseInsensitive(String pattern) {
		List<Integer> shiftTable = shiftTable(pattern.toLowerCase());

		int patternLength = pattern.length();
		int textLength = text.length();

		int index = patternLength - 1;

		while (index < textLength) {
			int k = 0;
			while ((k < patternLength) && compareCaseInsensitive(pattern.charAt(patternLength - 1 - k), text.charAt(index - k))) {
				k++;
			}
			if (k == patternLength) {
				return index - patternLength + 1;
			} else {
				index += shiftTable.get(Character.toLowerCase(text.charAt(index)));
			}
		}

		return -1;
	}

	/**
	 * Returns the a list of the positions of the all of the appearance of the
	 * pattern in the text. If the pattern does not appear returns an empty list.
	 * Based on HorspoolMatching from page 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @return List of the index of all of the occurrences of the pattern. If the
	 *         pattern does not appear, list will be empty
	 * 
	 * 
	 */
	public List<Integer> findAll(String pattern) {
		List<Integer> locations = new ArrayList<Integer>();

		List<Integer> shiftTable = shiftTable(pattern);

		int patternLength = pattern.length();
		int textLength = text.length();

		int index = patternLength - 1;

		while (index < textLength) {
			int k = 0;
			while ((k < patternLength) && (compare(pattern.charAt(patternLength - 1 - k), text.charAt(index - k)))) {
				k++;
			}
			if (k == patternLength) {
				locations.add(index - patternLength + 1);
			}
			index += shiftTable.get(text.charAt(index));
		}

		return locations;
	}

	/**
	 * Same as toLowerCase(a) == toLowerCase(b), but also increments compareCount
	 * 
	 * @param a
	 * @param b
	 * @return toLowerCase(a) == toLowerCase(b)
	 */
	public boolean compareCaseInsensitive(char a, char b) {
		// used to be able to count the number of comparisons a search makes
		compareCount++;
		return Character.toLowerCase(a) == Character.toLowerCase(b);
	}

	/**
	 * Returns the a list of the positions of the all of the appearance of the
	 * pattern in the text. Upper and lowercase characters are considered equal. If
	 * the pattern does not appear returns an empty list. Based on HorspoolMatching
	 * from page 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @return List of the index of all of the occurrences of the pattern. If the
	 *         pattern does not appear, list will be empty
	 * 
	 * 
	 */
	public List<Integer> findAllCaseInsensitive(String pattern) {
		List<Integer> locations = new ArrayList<Integer>();

		List<Integer> shiftTable = shiftTable(pattern.toLowerCase());

		int patternLength = pattern.length();
		int textLength = text.length();

		int index = patternLength - 1;

		while (index < textLength) {
			int k = 0;
			while ((k < patternLength)
					&& compareCaseInsensitive(pattern.charAt(patternLength - 1 - k), text.charAt(index - k))) {
				k++;
			}
			if (k == patternLength) {
				locations.add(index - patternLength + 1);
			}
			index += shiftTable.get(Character.toLowerCase(text.charAt(index)));

		}

		return locations;
	}
}
