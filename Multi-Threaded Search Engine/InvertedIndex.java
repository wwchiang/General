import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;


/** INVERTED INDEX
 * @author William Chiang
 *
 * Description - Creates an inverted index data structure which maps each
 * word to the file it appears in and the location(s) it pops up at.
 *
 */

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndex;
	private final MultiReaderLock lock;

	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>();
		lock = new MultiReaderLock();
	}

	/**
	 * Takes in a file path, and writes it to the file location specified.
	 * Formatting is automatically done every line.
	 *
	 * @param filePath The path where the contents of the map will be written to
	 */
	public void write(String writePath) {
		lock.lockRead();
		try (
			BufferedWriter writer = Files.newBufferedWriter(
					Paths.get(writePath),
					Charset.forName("UTF-8"),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);
		) {
			Set<String> mapKeys = invertedIndex.keySet();
			for (String key : mapKeys) {
				writer.write(key);
				writer.newLine();
				Set<String> innerMapKeys = invertedIndex.get(key).keySet();
				for (String innerKey: innerMapKeys) {

					String printedList = invertedIndex.get(key).get(innerKey).toString();
					printedList = printedList.substring(1, printedList.length()-1);

					writer.write("\"" + innerKey.toString() + "\", " + printedList);
					writer.newLine();
				}
				writer.newLine();
			}
			writer.flush();
		} catch (IOException e) {
			System.err.println("An error occurred while writing to file: " + writePath);
		}
		lock.unlockRead();
	}


	/**
	 * Performs a partial search based on the input queries, and sorts the results
	 * by rank of frequencies(times that word appears), position(first index of word
	 * appearance), and file name.
	 *
	 * @param queryList - An array of strings
	 * @return Returns an arraylist of type Search Result
	 */
	public ArrayList<SearchResult> partialSearch(String[] queryList) {
		Integer frequencies, position;
		HashMap<String, SearchResult> resultsMap = new HashMap<String, SearchResult>();

		lock.lockRead();
		for (String word: queryList) {
			Set<String> mapKeys;
			mapKeys = invertedIndex.tailMap(word, true).keySet();
			for (String key : mapKeys) {
				if (!key.startsWith(word)) {
					break;
				}
				Set<String> paths = invertedIndex.get(key).keySet();
				for (String path: paths) {
					position = invertedIndex.get(key).get(path).get(0);
					frequencies = invertedIndex.get(key).get(path).size();

					if (resultsMap.containsKey(path)){
						resultsMap.get(path).addFrequency(frequencies);
						resultsMap.get(path).addPosition(position);
					}
					else {
						resultsMap.put(path, new SearchResult(frequencies, position, path));
					}
				}
			}
		}
		lock.unlockRead();

		ArrayList<SearchResult> resultList = new ArrayList<SearchResult>(resultsMap.values());
		Collections.sort(resultList);

		return resultList;
	}

	/**
	 * Adds the entire contents of one inverted index into the index that called
	 * this method.
	 *
	 * @param other The index whose contents will be added
	 */
	public void addAll(InvertedIndex other) {
		lock.lockWrite();

		TreeMap<String, ArrayList<Integer>> innerMap;

		for (String key : other.invertedIndex.keySet()) {
			innerMap = other.invertedIndex.get(key);
			if (this.invertedIndex.containsKey(key)) {
				innerMap = other.invertedIndex.get(key);
				for (String path: innerMap.keySet()) {
					if (this.invertedIndex.get(key).containsKey(path)) {
						this.invertedIndex.get(key).get(path).addAll(innerMap.get(path));
						Collections.sort(this.invertedIndex.get(key).get(path));
					}
					else {
						this.invertedIndex.get(key).put(path, innerMap.get(path));
					}
				}
			}
			else {
				this.invertedIndex.put(key, innerMap);
			}

		}
		lock.unlockWrite();
	}

	/**
	 * Adds the word, path, and location into the invertedIndex, using the
	 * addLine() as a helper.
	 *
	 * @param word - the word being added in
	 * @param path - the path from which the word is read
	 * @param location - the index at which the word was found
	 */
	public void add(String word, String path, int location) {
		lock.lockWrite();
		String currentKey = word;
		String innerKey = path;

		TreeMap<String, ArrayList<Integer>> innerMap;

		if (invertedIndex.get(currentKey) == null) {
			innerMap = new TreeMap<String, ArrayList<Integer>>();
			ArrayList<Integer> innerList = new ArrayList<Integer>();
			innerList.add(location + 1);
			innerMap.put(innerKey, innerList);
			invertedIndex.put(currentKey, innerMap);
		}
		else {
			innerMap = invertedIndex.get(currentKey);
			ArrayList<Integer> innerList;
			if (innerMap.get(innerKey) == null) {
				innerList = new ArrayList<Integer>();
			}
			else {
				innerList = innerMap.get(innerKey);
			}
			innerList.add(location + 1);
			innerMap.put(innerKey, innerList);
		}
		lock.unlockWrite();
	}
}
