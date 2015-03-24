import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * This class instance is used for all indexing and searching functions, and
 * can be done through either the directory input or through a URL.
 * @author William
 *
 */
public class MultiBuilder {
	private QueryBuilder localQuery;
	private final WorkQueue taskPool;
	private int pending;
	private final InvertedIndex index;

	private MultiReaderLock urlLock;
	private HashSet<String> urlSet;

	public MultiBuilder(InvertedIndex index) {
		this(1, index);
	}

	public MultiBuilder(int threads, InvertedIndex index) {

		pending = 0;
		this.index = index;
		urlSet = new HashSet<String>();
		urlLock = new MultiReaderLock();
		if (threads < 1) {
			taskPool = new WorkQueue();
		}
		else {
			taskPool = new WorkQueue(threads);
		}
	}

	/**
	 * For use with a web address. Crawls that address, parsing any texts
	 * it encounters, and adds any additional URLs to the work queue.
	 * @param address
	 */
	public void crawl(URL address, InvertedIndex index) {
		try {
			taskPool.execute(new UrlWorker(address));
		} catch (Exception e) {
			System.err.println("Original URL could not be accessed.");
		}
	}

	/**
	 * For use with a directory, reads into an inverted index using text
	 * files from that directory.
	 * @param path
	 * @param index
	 * @throws IOException
	 */
	public void read(Path path, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path file : listing) {

				if (Files.isDirectory(file)) {
					read(file, index);
				}
				else {
					String fileName = file.getFileName().toString().toLowerCase();

					if (fileName.endsWith(".txt")) {
						taskPool.execute(new MultiIndexer(file));
					}
				}
			}
		}
		catch (Exception e) {
			System.err.println("A reading error occurred. Check command line arguments.");
		}
	}

	/**
	 * Searches through a directory path and adds its results to the multiBuilder
	 * @param query
	 * @param index
	 */
	public void search(Path query, InvertedIndex index) {
		localQuery = new QueryBuilder();
		localQuery.search(query, index);
	}

	/**
	 * Searches through input provided from a webpage(used with SearchServer)
	 *  and adds its results to the multiBuilder
	 * @param queries
	 * @param index
	 */
	public void serverSearch(String queries, InvertedIndex index) {
		localQuery = new QueryBuilder();
		localQuery.search(queries, index);
	}

	/**
	 * Writes the queries stored in this class to a path.
	 * @param path
	 */
	public void writeQueries(String path) {
		localQuery.writeResults(path);
	}

	/**
	 * Returns the results of this multiBuilder's search to another class.
	 * @return
	 */
	public LinkedHashMap<String, ArrayList<SearchResult>> getResults() {
		return localQuery.returnQuery();
	}

	/**
	 * To be called after any new function is used, ensuring that all threads
	 * are concurrent.
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				this.wait();
			}
		}
		catch (InterruptedException e) {
			System.err.println("Thread was interrupted while finishing.");
		}
	}

	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public synchronized void shutdown() {
		finish();
		taskPool.shutdown();
	}

	private synchronized void incrementPending() {
		pending++;
	}

	private synchronized void decrementPending() {
		pending--;
		if (pending <= 0) {
			this.notifyAll();
		}
	}

	/**
	 * A private class for parsing through URLs.
	 * @author William
	 *
	 */
	private class UrlWorker implements Runnable {

		private URL baseURL;

		public UrlWorker (URL baseURL) {
			this.baseURL = baseURL;
			incrementPending();
		}
		@Override
		public void run() {
			try {
				String page = HTMLCleaner.fetchHTML(baseURL.toString());

				ArrayList<String> pageLinks = new ArrayList<String>(HTMLLinkParser.listLinks(page));
				ArrayList<String> pageWords = new ArrayList<String>(HTMLCleaner.parseWords(InvertedIndexBuilder.cleanText( HTMLCleaner.cleanHTML(page) ) ) );

				for(String link: pageLinks) {
					link = link.replaceAll("#.*", "");
					URL absolute = new URL(baseURL, link);

					if (!urlSet.contains(absolute.toString()) && urlSet.size() != 49) {
						urlLock.lockWrite();
						urlSet.add(absolute.toString());
						urlLock.unlockWrite();
						taskPool.execute(new UrlWorker(absolute));
					}
				}

				InvertedIndex local = new InvertedIndex();
				int location = 0;

				for(String entry: pageWords) {
					local.add(entry, baseURL.toString(), location);
					location++;
				}

				index.addAll(local);
				decrementPending();
			} catch (Exception e) {
				System.err.println("An error occurred while reading URL from page.");
			}
		}

	}
	/**
	 * A private class of the MultiBuilder which allows for building the inverted
	 * index.
	 *
	 * @author William
	 *
	 */
	private class MultiIndexer implements Runnable {

		private Path directory;

		public MultiIndexer(Path directory) {
			this.directory = directory;
			incrementPending();
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.addFile(directory, local);
				index.addAll(local);

				decrementPending();
			} catch (IOException e) {
				System.err.println("An error occurred while building index. Check command line args.");
			}

		}
	}

	/** A private class for the MultiBuilder which allows for searches.
	 *
	 * @author William
	 *
	 */
	private class QueryBuilder {
		private final MultiReaderLock lock;
		private final LinkedHashMap<String, ArrayList<SearchResult>> resultsMap;

		public QueryBuilder() {
			resultsMap = new LinkedHashMap<String, ArrayList<SearchResult>>();
			lock = new MultiReaderLock();

		}

		private String cleanText(String text) {
			text = text.toLowerCase().replaceAll("[\\W\\_]", " ")
					.replaceAll("\\s+", " ").trim();
			return text;
		}


		public LinkedHashMap<String, ArrayList<SearchResult>> returnQuery() {
			return resultsMap;
		}
		/**
		 * Writes results to the specified parameter path.
		 * @param path
		 */
		public void writeResults(String path) {
			lock.lockRead();
			try (
				BufferedWriter writer = Files.newBufferedWriter(
						Paths.get(path),
						Charset.forName("UTF-8"),
						StandardOpenOption.CREATE,
						StandardOpenOption.WRITE);
				) {
				Set<String> queries = resultsMap.keySet();
				for (String query: queries){
					writer.write(query);
					writer.newLine();
					ArrayList<SearchResult> resultList = resultsMap.get(query);
					for (SearchResult result: resultList){
						writer.write("\"" + result.getPath() + "\", " + result.getFrequency() + ", " + result.getPosition());
						writer.newLine();
					}
					writer.newLine();
				}
				writer.flush();
			} catch (IOException e) {
				System.err.println("An error occurred while writing results to file.");
			}
			lock.unlockRead();
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
					String[] words = queryLine.split("\\s");
					resultsMap.put(queryLine, null);

					taskPool.execute(new MultiSearcher(queryLine, words));

				}
			}
			catch (Exception e) {
				System.err.println("An error occurred while searching. Check command line args.");
			}
		}

		/**
		 * A version of the search constructor which is used for searching from a webserver
		 * @param queryWords
		 * @param index
		 */
		public void search(String queryWords, InvertedIndex index) {
			queryWords = cleanText(queryWords);
			String[] words = queryWords.split("\\s");
			for (String word: words) {
				String[] entry = new String[]{word};
				resultsMap.put(word, null);

				taskPool.execute(new MultiSearcher(word, entry));
			}

		}

		/** A nested subclass of the searching class which implements a runnable
		 * method so that each object can do their own searches.
		 *
		 * @author William
		 *
		 */
		private class MultiSearcher implements Runnable {
			String queryLine;
			String[] queryList;

			public MultiSearcher(String queryLine, String[] words) {
				queryList = words;
				this.queryLine = queryLine;
				incrementPending();
			}

			@Override
			public void run() {
				try {
					ArrayList<SearchResult> temp = index.partialSearch(queryList);
					lock.lockRead();
					resultsMap.put(queryLine, temp);
					lock.unlockRead();

					decrementPending();
				} catch (Exception e) {
					System.err.println("An error occurred during partial searches. Check command line args. ");
				}

			}
	}
	}
}
