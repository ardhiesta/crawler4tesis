package te.sis.crawler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;

/*
 * http://svn.apache.org/viewvc/commons/proper/dbcp/trunk/doc/PoolingDataSourceExample.java?view=markup
 * */

public class WebContextListener implements ServletContextListener {
	CrawlController controller = null;

	public void contextInitialized(ServletContextEvent sce) {
		try {
			/*
			 * crawlStorageFolder is a folder where intermediate crawl data is
			 * stored.
			 */
			String crawlStorageFolder = MyFilePath.myOpenshiftPath
					+ "crawler4tesis_data/";
			/*
			 * numberOfCrawlers shows the number of concurrent threads that
			 * should be initiated for crawling.
			 */
			int numberOfCrawlers = 5;

			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(crawlStorageFolder);

			/*
			 * Be polite: Make sure that we don't send more than 1 request per
			 * second (1000 milliseconds between requests).
			 */
			int delay = 1000;
			config.setPolitenessDelay(delay);

			/*
			 * You can set the maximum crawl depth here. The default value is -1
			 * for unlimited depth
			 */
			config.setMaxDepthOfCrawling(-1);

			/*
			 * You can set the maximum number of pages to crawl. The default
			 * value is -1 for unlimited number of pages
			 */
			config.setMaxPagesToFetch(-1);

			/*
			 * This config parameter can be used to set your crawl to be
			 * resumable (meaning that you can resume the crawl from a
			 * previously interrupted/crashed crawl). Note: if you enable
			 * resuming feature and want to start a fresh crawl, you need to
			 * delete the contents of rootFolder manually.
			 */
			config.setResumableCrawling(false);

			/*
			 * Instantiate the controller for this crawl.
			 */
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(
					robotstxtConfig, pageFetcher);

			controller = new CrawlController(config, pageFetcher,
					robotstxtServer);

			/*
			 * For each crawl, you need to add some seed urls. These are the
			 * first URLs that are fetched and then the crawler starts following
			 * links which are found in these pages
			 */
			controller.addSeed("http://www.tripadvisor.com");

			Date startTime = new java.util.Date();
			System.out.println("Crawling dimulai - " + startTime.toString());
			System.out.println("jumlah crawler : " + numberOfCrawlers
					+ ", max "
					+ (double) (1000.00 / config.getPolitenessDelay())
					+ " request/sec ");
			System.out
					.println("=============================================================");

			long start_ts = System.currentTimeMillis();

			/*
			 * Start the crawl. This is a blocking operation, meaning that your
			 * code will reach the line after this only when crawling is
			 * finished.
			 */
			controller.start(MyCrawler.class, numberOfCrawlers);

			/*
			 * Crawl completed, report elapsed time.
			 */
			Date endTime = new java.util.Date();
			long end_ts = System.currentTimeMillis();

			int tot_elapsed_secs = (int) (end_ts - start_ts) / 1000;
			int hours = tot_elapsed_secs / 3600;
			int remainder = tot_elapsed_secs % 3600;
			int minutes = remainder / 60;
			int seconds = remainder % 60;
			String elapsed = hours + "h:" + minutes + "':" + seconds + "\"";

			System.out.println("Crawling selesai - " + endTime.toString()
					+ " - (durasi: " + elapsed + " )");
			System.out.println("jumlah crawler : " + numberOfCrawlers
					+ ", limit max "
					+ (double) (1000.00 / config.getPolitenessDelay())
					+ " request/sec ");
			System.out
					.println("=============================================================");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {
		controller.shutdown();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
//                LOG.log(Level.INFO, String.format("deregistering jdbc driver: %s", driver));
                System.out.println("deregistering jdbc driver: "+ driver);
            } catch (SQLException e) {
//                LOG.log(Level.SEVERE, String.format("Error deregistering driver %s", driver), e);
            	System.out.println("Error deregistering jdbc driver: "+ driver+" | "+e);
            }
        }
        
        try {
		    AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
//		    logger.warn("SEVERE problem cleaning up: " + e.getMessage());
		    System.out.println("SEVERE problem cleaning up: " + e.getMessage());
		    e.printStackTrace();
		}
	}
}
