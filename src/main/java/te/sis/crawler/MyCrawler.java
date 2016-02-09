package te.sis.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.jsoup.select.Elements;

import te.sis.db.MyDataSource;
import te.sis.parser.ItemReviewsPageParser;

public class MyCrawler extends WebCrawler {
	private ItemReviewsPageParser parser;

	// cek URL reviews
	public boolean isItemReviewsPage(String path) {
		return this.parser.isItemReviewsPage(path);
	}

	public String parseItemIdFromPath(String path) {
		return this.parser.parseItemIdFromPath(path);
	}

	// public List<Review> parseReviews(String html,String path){
	// return this.parser.parseReviews(html, path);
	// }

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");

	/**
	 * This method receives two parameters. The first parameter is the page in
	 * which we have discovered this new url and the second parameter is the new
	 * url. You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic). In this example,
	 * we are instructing the crawler to ignore urls that have css, js, git, ...
	 * extensions and to only accept urls that start with
	 * "http://www.ics.uci.edu/". In this case, we didn't need the referringPage
	 * parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://www.tripadvisor.com/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String path = page.getWebURL().getPath();

		if (page.getParseData() instanceof HtmlParseData
				&& path.startsWith("/ShowUserReviews")) { // && dst itu tambahan
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByClass("placeTypeText");

			JSONObject joDataRating = new JSONObject();

			String fileName = path.replace(".html", ".json").replace("/", "")
					.replace("ShowUserReviews-", "");
			File file = new File(MyFilePath.myOpenshiftPath
					+ "crawler4tesis_output/" + fileName);

			if (elements.size() > 0) {
				if (elements.text().equals("Hotel") && !file.exists() && !cekIfUrlVisited(fileName)
						&& !file.isDirectory()) {
					// System.out.println("--> DIBACA path: "+path);

					Elements elementsReviewSelector = doc
							.getElementsByClass("reviewSelector");

					JSONArray jaRating = new JSONArray();
					for (int i = 0; i < elementsReviewSelector.size(); i++) {
						JSONObject joRating = new JSONObject();

						Elements elReviewItemInline = elementsReviewSelector
								.get(i).getElementsByClass("reviewItemInline");
						Elements elRatingList = elementsReviewSelector.get(i)
								.getElementsByClass("rating-list");
						// if (elRatingList.size() > 0) {
						for (int j = 0; j < elRatingList.size(); j++) {
							Document docReviewItemInline = Jsoup
									.parse(elReviewItemInline.get(j)
											.outerHtml());
							Elements elImgReviewItemInline = docReviewItemInline
									.select("span.sprite-rating_s img");

							Document docRatingList = Jsoup.parse(elRatingList
									.get(j).outerHtml());
							Elements elImg = docRatingList
									.select("ul.recommend img");
							Elements elCriteriaName = docRatingList
									.getElementsByClass("recommend-answer");
							JSONObject joRatingMultikriteria = new JSONObject();
							for (int k = 0; k < elImg.size(); k++) {
								String ratingVal = elImg.get(k).attr("alt")
										.replace(" of 5 stars", "");

								joRatingMultikriteria.put(elCriteriaName.get(k)
										.text(), ratingVal);
							}

							joRatingMultikriteria.put("overall",
									elImgReviewItemInline.get(0).attr("alt")
											.replace(" of 5 stars", ""));
							joRating.put("rating_value", joRatingMultikriteria);
						}
						if (elRatingList.size() == 0) {
							Document docReviewItemInline = Jsoup
									.parse(elReviewItemInline.get(i)
											.outerHtml());
							Elements elImgReviewItemInline = docReviewItemInline
									.select("span.sprite-rating_s img");
							JSONObject joRatingMultikriteria = new JSONObject();
							joRatingMultikriteria.put("overall",
									elImgReviewItemInline.get(0).attr("alt")
											.replace(" of 5 stars", ""));
							joRating.put("rating_value", joRatingMultikriteria);
						}
						joRating.put("username", elementsReviewSelector.get(i)
								.getElementsByClass("scrname").text());
						String words = elementsReviewSelector.get(i)
								.getElementsByClass("scrname").outerHtml();
						String[] ar = words.split(" ");
						for(int j = 0; j<ar.length; j++){
							if(ar[j].startsWith("mbrName")){
//								System.out.println(ar[j].replace("\"", "").replace("mbrName_", ""));
								joRating.put("id_user", ar[j].replace("\"", "").replace("mbrName_", ""));
							}
						}
//						joRating.put("id_user", elementsReviewSelector.get(i)
//								.getElementsByClass("memberOverlayLink").get(0)
//								.id());
						joRating.put("id_rating", elementsReviewSelector.get(i)
								.id());

						jaRating.add(joRating);

					}
					joDataRating.put("rating_data", jaRating);
					JSONObject jHotel = new JSONObject();
					jHotel.put("hotel_name",
							doc.getElementsByClass("altHeadInline").text()
									.replace("Review of ", ""));
					jHotel.put("id_hotel", doc.select("span.altHeadInline a")
							.attr("href").replace("/", ""));
					joDataRating.put("hotel_data", jHotel);
					joDataRating.put("path", path);

					try {
						if (joDataRating.size() < 1) {
							System.out.println("kosong: " + path + " _ "
									+ fileName);
						} else {
							file.createNewFile();

							FileWriter fw = new FileWriter(
									file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(joDataRating.toJSONString());
							bw.close();
							insertLog(fileName);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public boolean insertLog(String url){
		boolean hasil = false;
		Connection conn = null;
		PreparedStatement preparedStmt = null;
        DataSource dataSource = MyDataSource.getInstance();
        
        try {
            conn = dataSource.getConnection();
            String query = "INSERT INTO crawlog(alamat, waktu) VALUES (?, ?)";
            preparedStmt = conn
					.prepareStatement(query);
			preparedStmt.setString(1, url);
		    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date now = new Date();
		    String strDate = sdfDate.format(now);
			preparedStmt.setString(2, strDate);
			hasil = preparedStmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
//            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (preparedStmt != null) preparedStmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		return hasil;
	}
	
	public boolean cekIfUrlVisited(String url){
		boolean hasil = false;
		Connection conn = null;
		PreparedStatement preparedStmt = null;
        ResultSet rset = null;
        DataSource dataSource = MyDataSource.getInstance();
        
        try {
            conn = dataSource.getConnection();
            String query = "SELECT * FROM crawlog WHERE alamat = ?";
            preparedStmt = conn
					.prepareStatement(query);
			preparedStmt.setString(1, url);
			rset = preparedStmt.executeQuery();
            if(rset.next()) {
            	hasil = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (preparedStmt != null) preparedStmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		return hasil;
	}
}
