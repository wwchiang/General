import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;


/**
 * A class designed to make fetching the results of different HTTP operations
 * easier. This particular class handles the GET operation.
 *
 * @see HTTPFetcher
 * @see HTMLFetcher
 * @see HeaderFetcher
 */
public class HTMLFetcher {
	/** Used to determine if headers have been read. */
	private boolean head;

	/** Port used by socket. For web servers, should be port 80. */
	private static final int PORT = 80;

	/** The URL to fetch from a web server. */
	private final URL url;

	/**
	 * Initializes this fetcher. Must call {@link #fetch()} to actually start
	 * the process.
	 *
	 * @param url - the link to fetch from the webserver
	 * @throws MalformedURLException if unable to parse URL
	 */
	public HTMLFetcher(String url) throws MalformedURLException {
		this.url = new URL(url);
		head = true;
	}

	/**
	 * Returns the port being used to fetch URLs.
	 *
	 * @return port number
	 */
	public int getPort() {
		return PORT;
	}

	/**
	 * Returns the URL being used by this fetcher.
	 *
	 * @return URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Crafts the HTTP GET request from the URL.
	 *
	 * @return HTTP request
	 */
	protected String craftRequest() {
		String host = this.getURL().getHost();
		String resource = this.getURL().getFile().isEmpty() ? "/" : this.getURL().getFile();

		StringBuffer output = new StringBuffer();
		output.append("GET " + resource + " HTTP/1.1\n");
		output.append("Host: " + host + "\n");
		output.append("Connection: close\n");
		output.append("\r\n");

		return output.toString();
	}

	/**
	 * Will skip any headers returned by the web server, and then output each
	 * line of HTML to the console.
	 */
//	@Override
//	protected void processLine(String line) {
//		if (head) {
//			// Check if we hit the blank line separating headers and HTML
//			if (line.trim().isEmpty()) {
//				head = false;
//			}
//		}
//		else {
//			System.out.println(line);
//		}
//	}

	/**
	 * Will connect to the web server and fetch the URL using the HTTP request
	 * from {@link #craftRequest()}, and then call {@link #processLine(String)}
	 * on each of the returned lines.
	 */
	public String fetch() {
		StringBuilder sb = new StringBuilder();
		try (
			Socket socket = new Socket(url.getHost(), PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
		) {
			String request = craftRequest();

			writer.println(request);
			writer.flush();
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				if (!head) {
					sb.append(line);
					sb.append("\n");
				}
				else if (line.isEmpty()) {
					head = false;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}
	public static void main(String[] args) throws MalformedURLException {
		new HTMLFetcher("http://www.cs.usfca.edu/~sjengle/archived.html").fetch();
	}
}