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

	// 8 bit character encoding allows the elephants child to work correctly
	final static Charset ASCII_ENCODING = StandardCharsets.ISO_8859_1;
	private String fileText;

	public String[] findAllStrings = { "the", "whale-fish", "white", "white shark", "white whale", "." };

	/**
	 * Constructor, starts with empty fileText
	 */
	public StringMatching() {
		fileText = "";
	}

	/**
	 * Add string to the end of fileText
	 * 
	 * @param s
	 */
	public void appendString(String s) {
		fileText += s;
	}

	/**
	 * read an ASCII file in for reasons beyond me this is like 1000x faster than a
	 * buffered file reader based on the response from user Pih from
	 * https://stackoverflow.com/questions/5854859/faster-way-to-read-file
	 * 
	 * @param fileName
	 *            Path and name of file to read in
	 * @throws IOException
	 */
	public void readFile(String fileName) throws IOException {
		FileInputStream f = new FileInputStream(fileName);
		FileChannel ch = f.getChannel();
		MappedByteBuffer mbb = ch.map(FileChannel.MapMode.READ_ONLY, 0L, ch.size());
		while (mbb.hasRemaining()) {
			CharBuffer cb = ASCII_ENCODING.decode(mbb);
			fileText += cb.toString();
		}
	}

	/**
	 * Sets fileText to an emtpy string
	 */
	public void clearFileText() {
		fileText = "";
	}

	/**
	 * 
	 * @return Length of fileText
	 */
	public int getStringLength() {
		return fileText.length();
	}

	/**
	 * Compares the three algorithms on runCount, random strings from the Moby Dick
	 * file
	 * 
	 * @param runCount
	 *            Number of comparisons to make
	 * @return A list of SearchMethodProfiles, which were each element contains a
	 *         list of SearchPerformances
	 */
	public List<SearchMethodProfile> randomProfileStringMatching(int runCount) {
		clearFileText();
		// read in moby dick file
		try {
			readFile("moby10b.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringSearch horspool = new HorspoolSearch(fileText);
		BoyerMooreSearch boyerMoore = new BoyerMooreSearch(fileText);
		StringSearch simple = new SimpleSearch(fileText);

		List<SearchMethodProfile> profiles = new ArrayList<SearchMethodProfile>();
		SearchMethodProfile horspoolPerf = new SearchMethodProfile("Horspool's Algorithm");
		SearchMethodProfile simplePerf = new SearchMethodProfile("Simple Algorithm");
		SearchMethodProfile bMPerf = new SearchMethodProfile("Boyer-Moore Algorithm");
		profiles.add(horspoolPerf);
		profiles.add(simplePerf);
		profiles.add(bMPerf);

		Random r = new Random();

		int textLength = this.getStringLength();
		int pos = 0;

		for (int i = 0; i < runCount; i++) {
			// get a random index in the text
			int startIndex = r.nextInt(textLength);
			// make sure we don't go beyond the end of the file
			int maxLength = textLength - startIndex;
			if (maxLength > 1000)
				maxLength = maxLength % 1000;

			// random length 1 -> maxLength (inclusive)
			int length = r.nextInt(maxLength + 1) + 1;

			// build string
			// it is worth nothing that this string may not be unique at this location
			// if "the" occurs at index 1,024,503 it probably also occurs earlier
			// similarly for other really short reaches (such as single letters)
			String s = "";
			for (int n = 0; n < length; n++) {
				s += fileText.charAt(startIndex + n);
			}

			// start searches and recording comparisons
			// we have the capability to time the searches and record those,
			// not being made use of currently
			pos = horspool.search(s);
			SearchPerformance sp1 = new SearchPerformance(s, 0, pos, horspool.getCompareCount());
			horspoolPerf.addPerf(sp1);
			horspool.resetCompareCount();

			pos = boyerMoore.search(s);
			SearchPerformance sp2 = new SearchPerformance(s, 0, pos,
					boyerMoore.getCompareCount() + boyerMoore.getSetupCompares());
			bMPerf.addPerf(sp2);
			boyerMoore.resetCompareCount();

			pos = simple.search(s);
			SearchPerformance sp3 = new SearchPerformance(s, 0, pos, simple.getCompareCount());
			simplePerf.addPerf(sp3);
			simple.resetCompareCount();
		}

		return profiles;
	}

	/**
	 * Helper method for creating worst case test
	 * 
	 * @param c
	 *            Character to repeat
	 * @param length
	 *            Number of times
	 * @return A string with character c repeated length times
	 */
	public String makeRepeatingLetter(char c, int length) {
		String s = "";
		for (int i = 0; i < length; i++) {
			s += c;
		}
		return s;
	}

	/**
	 * A helper method to run multiple implementations with the same text and
	 * pattern and print out the results
	 * 
	 * @param text
	 *            The text to search in
	 * @param pattern
	 *            The pattern to search for
	 */
	public void compareAlgos(String text, String pattern) {
		StringSearch horspool = new HorspoolSearch(text);
		BoyerMooreSearch boyerMoore = new BoyerMooreSearch(text);
		StringSearch simple = new SimpleSearch(text);

		System.out.println("HORSPOOL'S ALGORITHM");
		int index = horspool.search(pattern);
		System.out.println("index: " + index);
		System.out.println("comparisons: " + horspool.getCompareCount());
		horspool.resetCompareCount();

		System.out.println("BOYER-MOORE ALGORITHM");
		index = boyerMoore.search(pattern);
		System.out.println("index: " + index);
		System.out.println("setup comparisons: " + boyerMoore.getSetupCompares());
		System.out.println("search comparisons: " + boyerMoore.getCompareCount());
		System.out.println("total: " + (boyerMoore.getSetupCompares() + boyerMoore.getCompareCount()));
		boyerMoore.resetCompareCount();

		System.out.println("SIMPLE STRING SEARCH ALGORITHM");
		index = simple.search(pattern);
		System.out.println("index: " + index);
		System.out.println("comparisons: " + simple.getCompareCount());
		simple.resetCompareCount();
	}

	/**
	 * Helper to give useful units to the difference between two
	 * System.getNanoTime() calls
	 * 
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

	/**
	 * Compares the three algorithms for a variety of texts and patterns Prints out
	 * the string and pattern being searched for, and the index and number of
	 * comparisons each algorithm did
	 */
	public void runTests() {
		long startTime = 0;
		long endTime = 0;

		clearFileText();
		try {
			startTime = System.nanoTime();
			readFile("moby10b.txt");
			endTime = System.nanoTime();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Time to read file " + nanoTimeStartAndEndToString(startTime, endTime));
		System.out.println("File text length is " + getStringLength());

		System.out.println("\nsearching for \"BARBER\" in \"JIM SAW ME IN THE BARBERSHOP\"");
		compareAlgos("JIM SAW ME IN THE BARBERSHOP", "BARBER");

		System.out.println("\nsearching for \"lamb\" in \"Mary had a little lamb\"");
		compareAlgos("Mary had a little lamb", "lamb");

		System.out.println("\nsearching for \"Mary\" in \"Mary had a little lamb\"");
		compareAlgos("Mary had a little lamb", "Mary");

		System.out.println("\nsearching for \"cat\" in \"Mary had a little lamb\"");
		compareAlgos("Mary had a little lamb", "cat");

		System.out.println("\nsearching for \"hamlet\"");
		compareAlgos(fileText, "hamlet");

		System.out.println("\nsearching for \"Hamlet\"");
		compareAlgos(fileText, "Hamlet");

		String longString = "In less than a minute, without quitting his little craft, he and his\r\n"
				+ "crew were dropped to the water, and were soon alongside of the\r\n"
				+ "stranger.  But here a curious difficulty presented itself.  In the\r\n"
				+ "excitement of the moment, Ahab had forgotten that since the loss of\r\n"
				+ "his leg he had never once stepped on board of any vessel at sea but\r\n"
				+ "his own, and then it was always by an ingenious and very handy\r\n"
				+ "mechanical contrivance peculiar to the Pequod, and a thing not to be\r\n"
				+ "rigged and shipped in any other vessel at a moment's warning.  Now,\r\n"
				+ "it is no very easy matter for anybody--except those who are almost\r\n"
				+ "hourly used to it, like whalemen--to clamber up a ship's side from a\r\n"
				+ "boat on the open sea; for the great swells now lift the boat high up\r\n"
				+ "towards the bulwarks, and then instantaneously drop it half way down\r\n"
				+ "to the kelson.  So, deprived of one leg, and the strange ship of\r\n"
				+ "course being altogether unsupplied with the kindly invention, Ahab\r\n"
				+ "now found himself abjectly reduced to a clumsy landsman again;\r\n"
				+ "hopelessly eyeing the uncertain changeful height he could hardly hope\r\n" + "to attain.";

		System.out.println("\nsearching for an entire paragraph in chapter 100");
		compareAlgos(fileText, longString);

		System.out.println("\nsearching for an \"whale-fish\"");
		compareAlgos(fileText, "whale-fish");

		try {
			clearFileText();
			readFile("ElephantsChild.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("\nsearching for \"Bi-Coloured-Python-Rock-Snake\"");
		compareAlgos(fileText, "Bi-Coloured-Python-Rock-Snake");

		System.out.println("\nsearching for \"\"But it's very useful,\"\"");
		compareAlgos(fileText, "\"But it's very useful,\"");

		String badCase = makeRepeatingLetter('E', 10000);
		String pattern = "M" + makeRepeatingLetter('E', ONE_THOUSAND);
		System.out.println("\nsearching for 'M' followed by 1000 'E' in ten thousand 'E's");
		compareAlgos(badCase, pattern);

		pattern = makeRepeatingLetter('E', ONE_THOUSAND) + "M";
		System.out.println("\nsearching for 1000 'E' followed by 'M' in ten thousand 'E's");
		compareAlgos(badCase, pattern);

	}

	/**
	 * Prints out the data from a list of SearchMethodProfiles in a comma-separated
	 * values format
	 * 
	 * @param perfs
	 *            List of SearchMethodProfiles
	 */
	public void printSearchMethodProfileCSV(List<SearchMethodProfile> perfs) {
		System.out.println("Index, Length, Horspool Comps, B-M Comps, Simple comp");
		List<SearchPerformance> hPerfs = perfs.get(0).getPerformances();
		List<SearchPerformance> sPerfs = perfs.get(1).getPerformances();
		List<SearchPerformance> bMPerfs = perfs.get(2).getPerformances();
		for (int i = 0; i < hPerfs.size(); i++) {
			SearchPerformance hPerf = hPerfs.get(i);
			SearchPerformance bMPerf = bMPerfs.get(i);
			SearchPerformance sPerf = sPerfs.get(i);

			System.out.print(hPerf.getPos() + ", ");
			System.out.print(hPerf.getSearchString().length() + ", ");
			System.out.print(hPerf.getComparisons() + ", ");
			System.out.print(bMPerf.getComparisons() + ", ");
			System.out.print(sPerf.getComparisons() + "\n");
		}
	}

	private void printFirstN(List<Integer> values, int n) {
		int count = 0;
		System.out.print("[");
		for (Integer val : values) {
			if (count == n) {
				break;
			}
			if (count == n - 1 || count == values.size() - 1) {
				// don't print comma for last one
				System.out.print(val);
			} else {
				System.out.print(val + ", ");

			}
			count++;
		}
		System.out.println("]");
	}

	/**
	 * Finds all the locations of each of the strings in the given array, in the
	 * moby dick text file For each string, prints out the number of matches, and up
	 * to the first 100 indexes
	 * 
	 * @param stringsToSearchFor
	 *            Array of strings to find all occurances of
	 */
	public void testfindAll(String[] stringsToSearchFor) {
		clearFileText();
		try {
			readFile("moby10b.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		HorspoolSearch horspool = new HorspoolSearch(fileText);
		List<Integer> indexes;

		for (String s : stringsToSearchFor) {
			indexes = horspool.findAll(s);
			System.out.println("Found " + indexes.size() + " occurrences of \"" + s + "\"");
			// System.out.println("The first 100 indexes:");
			// printFirstN(indexes, 100);
		}
	}

	/**
	 * Finds all the locations (case insensitive) of each of the strings in the
	 * given array, in the moby dick text file For each string, prints out the
	 * number of matches, and up to the first 100 indexes
	 * 
	 * @param stringsToSearchFor
	 *            Array of strings to find all occurances of
	 */
	public void testfindAllCaseInsensitive(String[] stringsToSearchFor) {
		clearFileText();
		try {
			readFile("moby10b.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		HorspoolSearch horspool = new HorspoolSearch(fileText);
		List<Integer> indexes;

		for (String s : stringsToSearchFor) {
			indexes = horspool.findAllCaseInsensitive(s);
			System.out.println("Found " + indexes.size() + " occurrences of \"" + s + "\" (case insensitive)");
			// System.out.println("The first 100 indexes:");
			// printFirstN(indexes, 100);
		}
	}

	public static void main(String[] args) {
		StringMatching sM = new StringMatching();

		// Compare all three algorithms using predetermined strings
		// sM.runTests();

		// Compare all three algorithms using 1,000 random strings from moby dick
		// List<SearchMethodProfile> perfs = sM.randomProfileStringMatching(1000);
		// Print the results in CSV format
		// sM.printSearchMethodProfileCSV(perfs);

		// Demonstrate the ability to find all the occurrences of a string
		sM.testfindAll(sM.findAllStrings);
		
		// Demonstrate the ability to find all the occurrences of a string (case insensitive)
		sM.testfindAllCaseInsensitive(sM.findAllStrings);

	}
}
