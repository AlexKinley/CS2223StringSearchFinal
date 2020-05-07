import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class StringMatching {

	public static int ONE_THOUSAND = 1000;
	public static int ONE_MILLION = 1000000;

	final static Charset ASCII_ENCODING = StandardCharsets.US_ASCII;
	String fileText;
	public int compareCount = 0;

	public StringMatching() {
		fileText = "";
	}

	public void appendString(String s) {
		fileText += s;
	}

	public void readFile(String fileName) throws IOException {
		FileInputStream f = new FileInputStream(fileName);
		FileChannel ch = f.getChannel();
		MappedByteBuffer mbb = ch.map(FileChannel.MapMode.READ_ONLY, 0L, ch.size());
		while (mbb.hasRemaining()) {
			CharBuffer cb = ASCII_ENCODING.decode(mbb);
			fileText += cb.toString();
		}
	}

	public void clearFileText() {
		fileText = "";
	}

	public int getStringLength() {
		return fileText.length();
	}

	public void resetCompareCount() {
		compareCount = 0;
	}

	public boolean compare(char a, char b) {
		compareCount++;
		return a == b;
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
	public List<Integer> shiftTable(String pattern) {
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
	 * Returns the position of the first appearance of the pattern in the text If
	 * the pattern does not appear returns -1 Based on HorspoolMatching from page
	 * 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @param text
	 *            String to search for pattern in
	 * @return Index of first occurrence of pattern in text, -1 if pattern does not
	 *         appear
	 */
	public int HorspoolMatching(String pattern, String text) {

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
	 * Returns the position of the first appearance of the pattern in the read in
	 * file. If the pattern does not appear returns -1. Based on HorspoolMatching
	 * from page 262 of Levitin
	 * 
	 * @param pattern
	 *            String to find in text
	 * @return Index of first occurrence of pattern in text, -1 if pattern does not
	 *         appear
	 */
	public int FileHorspoolMatching(String pattern) {

		List<Integer> shiftTable = shiftTable(pattern);

		int patternLength = pattern.length();
		int textLength = fileText.length();

		int index = patternLength - 1;

		while (index < textLength) {
			int k = 0;
			while ((k < patternLength)
					&& (compare(pattern.charAt(patternLength - 1 - k), fileText.charAt(index - k)))) {
				k++;
			}
			if (k == patternLength) {
				return index - patternLength + 1;
			} else {
				index += shiftTable.get(fileText.charAt(index));
			}
		}

		return -1;
	}
	
	/**
	 * A simple string search algorithm that just moves forward once each time
	 * 
	 * @param pattern String being searched for in file
	 * @return First index of pattern, -1 if it does not appear
	 */
	public int fileSmpleStringMatching(String pattern) {
		int patternLength = pattern.length();
		int textLength = fileText.length();
		
		int index = 0;
		
		while (index < (textLength - patternLength)) {
			int k = 0;
			while ((k < patternLength)
					&& (compare(pattern.charAt(k), fileText.charAt(index + k)))) {
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
	
	public List<SearchMethodProfile> randomProfileStringMatching(int runCount) {
		List<SearchMethodProfile> profiles = new ArrayList<SearchMethodProfile>();
		SearchMethodProfile horspoolPerf = new SearchMethodProfile("Horspool's Algorithm");
		SearchMethodProfile simplePerf = new SearchMethodProfile("Simple Algorithm");
		profiles.add(horspoolPerf);
		profiles.add(simplePerf);
		
		Random r = new Random();
		
		int textLength = this.getStringLength();
		long startTime = 0;
		long endTime = 0;
		int pos = 0;
		
		
		for(int i = 0; i < runCount; i++) {
			int startIndex = r.nextInt(textLength);
			int maxLength = textLength - startIndex;
			if(maxLength > 1000)
				maxLength = maxLength % 1000;
			
			int length = r.nextInt(maxLength + 1) + 1;
			String s = "";
			for(int n = 0; n < length; n++) {
				s += fileText.charAt(startIndex + n);
			}
			if(s.length() == 0) {
				System.out.println("string length is zero");
				System.out.println("startIndex = " + startIndex);
				System.out.println("maxLength = " + maxLength);
				System.out.println("length = " + length);
			}
			
			this.resetCompareCount();

			
			startTime = System.nanoTime();
			pos = FileHorspoolMatching(s);
			endTime = System.nanoTime();
			
			SearchPerformance sp1 = new SearchPerformance(s, endTime - startTime, pos, compareCount);
			horspoolPerf.addPerf(sp1);

			this.resetCompareCount();
			
			startTime = System.nanoTime();
			pos = fileSmpleStringMatching(s);
			endTime = System.nanoTime();
			
			SearchPerformance sp2 = new SearchPerformance(s, endTime - startTime, pos, compareCount);
			simplePerf.addPerf(sp2);
		}
		
		return profiles;
	}
	/**
	 * Helper to give useful units to the difference between two System.getNanoTime() calls
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String nanoTimeStartAndEndToString(long startTime, long endTime) {
		long time = endTime - startTime;

		if (time < ONE_THOUSAND) {
			return time + "ns";
		} else if (time < ONE_MILLION) {
			return (time / ONE_THOUSAND) + "us";
		} else {
			return (time / ONE_MILLION) + "ms";
		}
	}

	public static void main(String[] args) {
		StringMatching sM = new StringMatching();

		long startTime = 0;
		long endTime = 0;

		try {
			startTime = System.nanoTime();
			sM.readFile("moby10b.txt");
			endTime = System.nanoTime();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Time to read file " + nanoTimeStartAndEndToString(startTime, endTime));
		System.out.println("File text length is " + sM.getStringLength());

		List<SearchMethodProfile> perfs = sM.randomProfileStringMatching(1000);
		System.out.println("Index, Length, Horspool Time, Horspool Comps, Simple time, Simple comp");
		List<SearchPerformance> hPerfs = perfs.get(0).getPerformances();
		List<SearchPerformance> sPerfs = perfs.get(1).getPerformances();
		for(int i = 0; i < hPerfs.size(); i++) {
			SearchPerformance hPerf =hPerfs.get(i);
			SearchPerformance sPerf = sPerfs.get(i);
			System.out.print(hPerf.getPos() + ", ");
			System.out.print(hPerf.getSearchString().length() + ", ");
			System.out.print(hPerf.getElapsedTimeMicroString() + ", ");
			System.out.print(hPerf.getComparisons() + ", ");
			System.out.print(sPerf.getElapsedTimeMicroString() + ", ");
			System.out.print(sPerf.getComparisons() + "\n");
		}
	}
}
