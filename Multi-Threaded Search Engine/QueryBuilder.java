import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

// AT THE MOMENT THIS IS UNUSED //
// THIS CLASS IS ONLY BEING KEPT FOR REFERENCE //
/**
 *
 * @author William Chiang
 * Desc: This class is specialized for partial searching using the inverted index.
 *
 */
public class QueryBuilder {
	private final LinkedHashMap<String, ArrayList<SearchResult>> resultsMap;
	private final MultiReaderLock lock;

	public QueryBuilder() {
		resultsMap = new LinkedHashMap<String, ArrayList<SearchResult>>();
		lock = new MultiReaderLock();
	}

	private static String cleanText(String text) {
		text = text.toLowerCase().replaceAll("[\\W\\_]", " ")
				.replaceAll("\\s+", " ").trim();
		return text;
	}

	/**
	 * Writes results to the specified parameter path.
	 * @param path
	 */
	public void writeResults(String path) {
		try (
			BufferedWriter writer = Files.newBufferedWriter(
					Paths.get(path),
					Charset.forName("UTF-8"),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);
			) {
			lock.lockWrite();
			Set<String> queries = resultsMap.keySet();
			for (String query: queries) {
				writer.write(query);
				writer.newLine();
				ArrayList<SearchResult> resultList = resultsMap.get(query);
				for (SearchResult result: resultList) {
					writer.write("\"" + result.getPath() + "\", " + result.getFrequency() + ", " + result.getPosition());
					writer.newLine();
				}
				writer.newLine();
			}
			writer.flush();
			lock.unlockWrite();
		} catch (IOException e) {
			System.err.println("An error occurred while writing results to file.");
		}
	}

	/**
	 * Searches through the path provided and uses the partialSearch method in the
	 * inverted index.
	 *
	 * @param queryPath - The path of the query file.
	 * @param index - The inverted index.
	 */
	public void search(Path queryPath, InvertedIndex index) {
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(queryPath, charset)) {
			String queryLine;

			while ((queryLine = reader.readLine()) != null) {
				queryLine = cleanText(queryLine);
			}
		}
		catch (Exception e) {
			System.err.println("An error occurred while searching. Check command line args.");
		}
	}

}
