
public class SimpleSearch extends StringSearch{
	
	public SimpleSearch(String text) {
		super(text);
	}
	
	public int search(String pattern) {
		int patternLength = pattern.length();
		int textLength = text.length();

		int index = 0;

		while (index < (textLength - patternLength)) {
			int k = 0;
			while ((k < patternLength) && (compare(pattern.charAt(k), text.charAt(index + k)))) {
				k++;
			}
			if (k == patternLength) {
				return index;
			} else {
				index += 1;
			}
		}
		return -1;
	}
}
