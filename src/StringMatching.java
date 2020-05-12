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
	public int compareCount = 0;

	public StringMatching() {
		fileText = "";
	}

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

	public void clearFileText() {
		fileText = "";
	}

	public int getStringLength() {
		return fileText.length();
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

//		for (int i = 0; i < runCount; i++) {
//			int startIndex = r.nextInt(textLength);
//			int maxLength = textLength - startIndex;
//			if (maxLength > 1000)
//				maxLength = maxLength % 1000;
//
//			int length = r.nextInt(maxLength + 1) + 1;
//			String s = "";
//			for (int n = 0; n < length; n++) {
//				s += fileText.charAt(startIndex + n);
//			}
//			if (s.length() == 0) {
//				System.out.println("string length is zero");
//				System.out.println("startIndex = " + startIndex);
//				System.out.println("maxLength = " + maxLength);
//				System.out.println("length = " + length);
//			}
//
//			this.resetCompareCount();
//
//			startTime = System.nanoTime();
//			pos = FileHorspoolMatching(s);
//			endTime = System.nanoTime();
//
//			SearchPerformance sp1 = new SearchPerformance(s, endTime - startTime, pos, compareCount);
//			horspoolPerf.addPerf(sp1);
//
//			this.resetCompareCount();
//
//			startTime = System.nanoTime();
//			pos = fileSmpleStringMatching(s);
//			endTime = System.nanoTime();
//
//			SearchPerformance sp2 = new SearchPerformance(s, endTime - startTime, pos, compareCount);
//			simplePerf.addPerf(sp2);
//		}

		return profiles;
	}
	
	public String makeRepeatingLetter(char c, int length) {
		String s = "";
		for(int i = 0; i < length; i++) {
			s += c;
		}
		return s;
	}
	
	/**
	 * A helper method to run multiple implementations with the same text and pattern
	 * and print out the results
	 * @param text The text to search in
	 * @param pattern The pattern to search for
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
	
	public void runTests() {
		long startTime = 0;
		long endTime = 0;

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
		
		String longString = "In less than a minute, without quitting his little craft, he and his\r\n" + 
				"crew were dropped to the water, and were soon alongside of the\r\n" + 
				"stranger.  But here a curious difficulty presented itself.  In the\r\n" + 
				"excitement of the moment, Ahab had forgotten that since the loss of\r\n" + 
				"his leg he had never once stepped on board of any vessel at sea but\r\n" + 
				"his own, and then it was always by an ingenious and very handy\r\n" + 
				"mechanical contrivance peculiar to the Pequod, and a thing not to be\r\n" + 
				"rigged and shipped in any other vessel at a moment's warning.  Now,\r\n" + 
				"it is no very easy matter for anybody--except those who are almost\r\n" + 
				"hourly used to it, like whalemen--to clamber up a ship's side from a\r\n" + 
				"boat on the open sea; for the great swells now lift the boat high up\r\n" + 
				"towards the bulwarks, and then instantaneously drop it half way down\r\n" + 
				"to the kelson.  So, deprived of one leg, and the strange ship of\r\n" + 
				"course being altogether unsupplied with the kindly invention, Ahab\r\n" + 
				"now found himself abjectly reduced to a clumsy landsman again;\r\n" + 
				"hopelessly eyeing the uncertain changeful height he could hardly hope\r\n" + 
				"to attain.";
		
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

	public static void main(String[] args) {
		StringMatching sM = new StringMatching();
		
		sM.runTests();
		
		
	}
}
