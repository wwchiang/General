import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Creates a server at the specified port(default 8080) to search with. Must be
 * used in conjunction with the driver and a seed URL.
 * @author William
 *
 */
public class SearchServer {
	private static final String TITLE = "Search Engine";
	private InvertedIndex index;
	private MultiBuilder builder;
	private LinkedHashMap<String, ArrayList<SearchResult>> serverResults;
	private static boolean viewResult;

	private ArrayList<String> storedQueries;
	private Map<String, String> cookies;
	private String queries;
	public static final String HISTORY = "history";

	public SearchServer(InvertedIndex index, MultiBuilder builder) throws Exception {
		this(8080, index, builder);
	}

	public SearchServer(int port, InvertedIndex index, MultiBuilder builder) throws Exception {
		storedQueries = new ArrayList<String>();
		this.builder = builder;
		this.index = index;
		viewResult = false;
		Server server = new Server(port);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchServlet()), "/search");
		server.setHandler(handler);
		server.start();
		server.join();
	}

	/**
	 * Performs a partial search on a new entry of queries.
	 * @param queries
	 */
	private void search(String queries) {
		builder.serverSearch(queries, index);
		builder.finish();
		serverResults = new LinkedHashMap<String, ArrayList<SearchResult>>(builder.getResults());
	}

	/**
	 * Servlet to GET handle requests to /check.
	 */
	public class SearchServlet extends HttpServlet {

		/**
		 * Displays a form where users can enter a search query, which will be
		 * separated by spaces & non-word characters.
		 *
		 * Can have option to clear search history.
		 */
		@Override
		protected void doGet(
				HttpServletRequest request,
				HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");
			printForm(request, response);

			cookies = getCookieMap(request);
			Set<String> history = cookies.keySet();
			out.printf("<p><b>Search History</b></p>");
			if (!cookies.isEmpty()) {
				for (String query: storedQueries) {
					out.printf("<p>%s</p>", query);
				}
			}
			if (viewResult) {
				out.printf("<p><b>Results</b></p>");
				Set<String> queries = serverResults.keySet();
				for (String query: queries){

					out.printf("<p>%s</p>", query);
					ArrayList<SearchResult> resultList = serverResults.get(query);
					if (resultList == null) {
						System.out.println("NULL");
					}
					for (SearchResult result: resultList){
						out.printf("<p><a href=\"%s\">%s</a></p>", result.getPath(), result.getPath());
					}
				}
			}

			response.addCookie(new Cookie("history", queries));
			out.printf("</body>%n");
			out.printf("</html>%n");
		}

		@Override
		protected void doPost(
				HttpServletRequest request,
				HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			queries = request.getParameter("SEARCH");

			if (request.getParameter("clear") != null) {
				clearCookies(request, response);
				storedQueries = new ArrayList<String>();
			}

			if (!queries.isEmpty()) {
				storedQueries.add(queries);
				search(queries);
			}
			viewResult = true;

			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(request.getServletPath());
		}

		private void printForm(
				HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			PrintWriter out = response.getWriter();
			out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
			out.printf("\t\t<input type=\"text\" name=\"SEARCH\" maxlength=\"100\" size=\"60\">%n");
			out.printf("<p><input type=\"submit\" value=\"Submit\"></p>\n%n");
			out.printf("<input type=\"checkbox\" name=\"clear\">Clear Search History<br>");
			out.printf("</form>\n%n");
		}
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie key to
	 * the cookie value.
	 *
	 * @param 	request - HTTP request from web server
	 * @return 	map from cookie key to cookie value
	 */
	public Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request - HTTP request
	 * @param response - HTTP response
	 */
	public void clearCookies(
			HttpServletRequest request,
			HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {

			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
}
