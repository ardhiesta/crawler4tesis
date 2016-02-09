package te.sis.crawler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().write("Hello World.".getBytes());

//		try {
//			String crawlStorageFolder = "/black/downloads/crawler4tesis_data";
//			int numberOfCrawlers = 7;
//
//			CrawlConfig config = new CrawlConfig();
//			config.setCrawlStorageFolder(crawlStorageFolder);
//			config.setMaxDepthOfCrawling(1);
//
//			/*
//			 * Instantiate the controller for this crawl.
//			 */
//			PageFetcher pageFetcher = new PageFetcher(config);
//			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
//			RobotstxtServer robotstxtServer = new RobotstxtServer(
//					robotstxtConfig, pageFetcher);
//
//			CrawlController controller = new CrawlController(config,
//					pageFetcher, robotstxtServer);
//			
//			/*
//	         * For each crawl, you need to add some seed urls. These are the first
//	         * URLs that are fetched and then the crawler starts following links
//	         * which are found in these pages
//	         */
//	        controller.addSeed("http://www.ics.uci.edu/~lopes/");
//	        //controller.addSeed("http://www.ics.uci.edu/~welling/");
//	        //controller.addSeed("http://www.ics.uci.edu/");
//
//	        /*
//	         * Start the crawl. This is a blocking operation, meaning that your code
//	         * will reach the line after this only when crawling is finished.
//	         */
//	        controller.start(MyCrawler.class, numberOfCrawlers);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
