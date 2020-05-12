import java.util.ArrayList;
import java.util.List;

public class BoyerMooreSearch extends StringSearch {

	
	private int setupCompares = 0;
	
	
	public BoyerMooreSearch(String text) {
		super(text);
	}
	
	/**
	 * Same method as Hoorspool shiftTable()
	 * 
	 * @param pattern
	 * @return
	 */
	private List<Integer> boyerMooreBadCharacterShift(String pattern) {
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
	
	public int getSetupCompares() {
		return this.setupCompares;
	}

	/**
	 * Determines if the substring of the needle starting at 
	 * start and ending at the end of the needle is present at the start of the string
	 * @param needle String being searched for
	 * @param start Place in the needle where the potential prefix starts
	 * @return
	 */
	private boolean isPrefix(String needle, int start) {
		for (int i = start, j = 0; i < needle.length(); ++i, ++j) {
			if (!(compare(needle.charAt(i), needle.charAt(j)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Maximum length of substring of the needle starting at the begining and ending at p
	 * that is a suffix of the needle
	 * @param needle String being searched for
	 * @param p substring end
	 * @return
	 */
	private int suffixLength(String needle, int p) {
		int len = 0;
		for (int i = p, j = needle.length() - 1; i >= 0 && compare(needle.charAt(i), needle.charAt(j)); --i, --j) {
			len += 1;
		}
		return len;
	}

	/**
	 * Creates the offset table for the good suffix rule of the 
	 * Boyer-Moore algorithm
	 * 
	 * @param pattern String being searched for
	 * @return
	 */
	public List<Integer> boyerMooreOffsetTable(String pattern) {
		List<Integer> offsetTable = new ArrayList<Integer>();

		int patternLength = pattern.length();

		// just fill the list with zero's so that it's the correct size
		for (int i = 0; i < patternLength; i++) {
			offsetTable.add(0);
		}

		int lastPrefix = patternLength;

		for (int i = patternLength; i > 0; i--) {
			if (isPrefix(pattern, i)) {
				lastPrefix = i;
			}
			offsetTable.set(patternLength - i, lastPrefix - i + patternLength);
		}
		for (int i = 0; i < patternLength - 1; ++i) {
			int slen = suffixLength(pattern, i);
			offsetTable.set(slen, patternLength - 1 - i + slen);
		}

		return offsetTable;
	}

	/**
	 * Boyer-Moore string search This implementation is heavily based on the java
	 * implementation on the wikipedia page
	 * https://en.wikipedia.org/wiki/Boyer–Moore_string-search_algorithm
	 * The above helper methods are also based off the linked implementation
	 * 
	 * @param pattern String to search for in file
	 * @return Index of first occurrence of the string
	 */
	public int search(String pattern) {
		int pLength = pattern.length();
		if (pLength == 0) {
			return 0;
		}
		List<Integer> charTable = boyerMooreBadCharacterShift(pattern);
		List<Integer> offsetTable = boyerMooreOffsetTable(pattern);
		
		setupCompares = this.compareCount;
		this.resetCompareCount();
		
		
		for (int i = pLength - 1, j; i < text.length();) {
			for (j = pLength - 1; compare(pattern.charAt(j), text.charAt(i)); --i, --j) {
				if (j == 0) {
					return i;
				} 
			}
			// i += needle.length - j; // For naive method
			i += Math.max(offsetTable.get(pLength - 1 - j), charTable.get(text.charAt(i)));
		}
		return -1;
	}
}
