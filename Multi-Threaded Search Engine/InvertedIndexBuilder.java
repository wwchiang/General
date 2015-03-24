import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author William Chiang
 * This class provides functional support for the inverted index's inner
 * data structures, and is necessary to build the index itself.
 */

public class InvertedIndexBuilder {

	/**
	 * Reads in a file and the index, which starts out as empty, and procedurally
	 * builds up the resulting data structure.
	 *
	 * @param path - The directory from which files are read
	 * @param index - An instance of the inverted index
	 * @throws IOException
	 */
	public static void read(Path path, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {

				if (Files.isDirectory(file)) {
					read(file, index);
				}
				else {
					String fileName = file.getFileName().toString().toLowerCase();

					if (fileName.endsWith(".txt")) {
						addFile(file, index);
					}
				}
			}
		}
		catch (Exception e) {
			System.err.println("A reading error occurred. Check command line arguments.");
		}
	}

	/**
	 * Converts text into a consistent format by converting text to lower-
	 * case, replacing non-word characters and underscores with a single
	 * space, and finally removing leading and trailing whitespace.
	 *
	 * @param text - original text
	 * @return text without special characters and leading or trailing spaces
	 */
	public static String cleanText(String text) {
		text = text.toLowerCase().replaceAll("[\\W\\_]", " ")
				.replaceAll("\\s+", " ").trim();
		return text;
	}

	/**
	 * A modification of the parseText method in WordParser, this helper method
	 * uses cleanText to strip any non-word characters from the line, and
	 * passes it's data to the add() method.
	 *
	 * @param line - original line of text
	 * @param filePath - the file from which this line was read
	 * @param currentLocation - the starting index in the line of the file being read
	 *
	 * @return currentLocation - the end index of the line
	 */
	public static int addLine(String line, String filePath, int currentLocation, InvertedIndex index) {
		line = cleanText(line);
		String[] words = line.split("\\s");
		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				index.add(words[i], filePath, currentLocation);
				currentLocation++;
			}
		}
		return currentLocation;
	}


	/**
	 * A modification of parseFile from the WordParser class. Reads a file
	 * line-by-line and writes it to the invertedIndex. Uses the addLine helper method.
	 *
	 * @param path - file path to open
	 * @throws IOException
	 */
	public static void addFile(Path path, InvertedIndex index) throws IOException {
		int currentLocation = 0;
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader input = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = input.readLine()) != null) {
				currentLocation = addLine(line, path.toString(), currentLocation, index);
			}
		} catch (IOException e) {
			throw e;
		}
	}

}
