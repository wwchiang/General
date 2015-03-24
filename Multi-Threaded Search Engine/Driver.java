
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/** PROJECT 4 & 5 DRIVER
 * @author William Chiang
 *
 * Description: Extends Project 1,2,3, & allows for URLs & server searches.
 *
 */
public class Driver {

	/**
	 *This driver takes in up to four command line arguments:
	 * -d <Directory Path>
	 * -i <Write Path>
	 * -q <Query Path>
	 * -r <Results Path>
	 * -t <Number of threads>
	 * -u <URL path>
	 * -p <Port #>
	 *
	 * -u traverses through a given URL and parses all non-HTML text
	 * -d writes the contents of a file/directory to an Inverted Index.
	 * -q requires -d to function, and partial searches a query file.
	 * -t specifies how many threads should run the program.
	 *
	 *The -i command is optional; using it without a write path makes the
	 *program output to a default file called 'index.txt'. -r is also optional,
	 *and the default output for results is 'results.txt'
	 *-t is optional -- if it is not provided, only one thread will run. If it
	 *is provided and no number of threads is specified, it defaults to five.
	 *
	 * @param args The array of command line arguments.
	 */
	public static void main(String[] args) {
		try {
			MultiBuilder multiBuilder;

			ArgumentParser cmdargs = new ArgumentParser(args);
			InvertedIndex invertedIndex = new InvertedIndex();
			if ( cmdargs.hasFlag("-t")) {
				if ( cmdargs.hasValue("-t")) {
					multiBuilder = new MultiBuilder(Integer.parseInt(cmdargs.getValue("-t")), invertedIndex);
				}
				else {
					multiBuilder = new MultiBuilder(invertedIndex);
				}
			}
			else {
				multiBuilder = new MultiBuilder(1, invertedIndex);
			}
			if ( cmdargs.hasFlag("-u") ){
				URL indexURL = new URL(cmdargs.getValue("-u"));
				multiBuilder.crawl(indexURL, invertedIndex);
			}
			if ( cmdargs.hasFlag("-d") ){
				String readPath = cmdargs.getValue("-d");
				Path path = Paths.get(readPath).toAbsolutePath().normalize();
				multiBuilder.read(path, invertedIndex);
			}
			multiBuilder.finish();
			if ( cmdargs.hasFlag("-p")) {
				SearchServer server = new SearchServer(Integer.parseInt(cmdargs.getValue("-p")), invertedIndex, multiBuilder);
			}
			if ( cmdargs.hasFlag("-q")) {
				String queryPath = cmdargs.getValue("-q");
				Path query = Paths.get(queryPath).toAbsolutePath().normalize();
				multiBuilder.search(query, invertedIndex);
				multiBuilder.finish();
				if ( cmdargs.hasFlag("-r")) {
					if ( cmdargs.hasValue("-r")) {
						String resultPath = cmdargs.getValue("-r");
						multiBuilder.writeQueries(resultPath);
					}
					else {
						multiBuilder.writeQueries("results.txt");
					}
				}
			}
			if ( cmdargs.hasFlag("-i")) {
				if ( cmdargs.hasValue("-i")) {
					String writePath = cmdargs.getValue("-i");
					invertedIndex.write(writePath);
				}
				else {
					invertedIndex.write("index.txt");
				}
			}
			multiBuilder.shutdown();
		}
		catch (Exception e){
			System.err.println("An unknown error has occurred."
					+ " Please check command line args.");
		}

	}

}
