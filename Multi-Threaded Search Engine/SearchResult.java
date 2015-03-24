
/**
 *
 * @author William Chiang
 * Desc - A helper class for the partial search functionality of the inverted
 * index. Implements the comparable interface to be used with Collections.Sort()
 *
 */
public class SearchResult implements Comparable<SearchResult> {

	private Integer frequency, position;
	private final String path;

	public SearchResult(Integer frequencies, Integer position, String path) {
		if (frequency == null) {
			frequency = 0;
		}
		frequency += frequencies;
		this.position = position;
		this.path = path;
	}
	/**
	 * Returns the path of this search result
	 * @return This result's path
	 */
	public String getPath() {
		return path.toString();
	}

	/**
	 * Returns this search result's frequency
	 * @return This result's frequency
	 */
	public Integer getFrequency() {
		return frequency.intValue();
	}

	/**
	 * Returns this search result's position
	 * @return This result's position
	 */
	public Integer getPosition() {
		return position.intValue();
	}

	/**
	 * Adds the number of occurrences to this result
	 * @param frequencies - The number of frequencies to add
	 */
	public void addFrequency(Integer frequencies) {
		frequency += frequencies;
	}

	/**
	 * Adds the smallest position to this result
	 * @param position - The position to add
	 */
	public void addPosition(Integer position) {
		if (position < this.position) {
			this.position = position;
		}
	}


	/**
	 * Determines the relevancy of two search results, with the priority as:
	 * 1. Number of times the word appears
	 * 2. Earliest position of the word
	 * 3. The file's name from which the word is located.
	 *
	 */
	@Override
	public int compareTo(SearchResult input) {
		if (this.frequency.compareTo(input.frequency) < 0) {
			return 1;
		}
		else if (this.frequency.compareTo(input.frequency) > 0) {
			return -1;
		}
		else {
			if (this.position.compareTo(input.position) < 0) {
				return -1;
			}
			else if (this.position.compareTo(input.position) > 0) {
				return 1;
			}
			else {
				return this.path.compareToIgnoreCase(input.path);
			}
		}

	}

}
