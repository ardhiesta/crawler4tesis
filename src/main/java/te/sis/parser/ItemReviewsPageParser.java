package te.sis.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by linuxluv on 8/25/15.
 * crawler-tripadvisor || GitHub
 */
public class ItemReviewsPageParser {
    public boolean isItemReviewsPage(String path) {
        //ambil review hotel saja
        return (path.startsWith("/Hotel_Review"));
    }

    public String parseItemIdFromPath(String path) {
        String[] pathFields = path.split("-");
		/*
		 * Tipo URL
		 * http://www.tripadvisor.it/Hotel_Review-g187791-d191099-Reviews
		 * -or1200-Albergo_del_Senato-Rome_Lazio.html#REVIEWS Quindi l'id
		 * dell'item sarebbe il terzo componente della stringa url (d191099)
		 */
        return pathFields[2];
    }

    /**
     * Effettua il parsing delle review sulla pagina corrente del parser.
     *
     * @param html
     *            contiene tutto l'html della pagina (non piÃ¹ usata, ma
     *            fondamentale se poi vorremo estrarre altre info)
     * @param path
     *            Ã¨ il percorso della pagina, ad es.
     *            "/Restaurant_Review-g1207908-d2060003-Reviews-or20-Mama_Rosa_s-Leighton_Buzzard_Bedfordshire_England.html"
     * @return Una lista di Review, corrispondenti a quelle sulla pagina
     *         corrente
     */
//    public List<Review> parseReviews(String html, String path) {
//        //parsing isi halaman
//        Document doc = Jsoup.parse(html);
//        Elements elements = doc.getElementsByClass("reviewSelector");
//        System.out.println("ukuran elements : " + elements.size());
//        System.out.println("");
//        for(int i=0; i<elements.size(); i++){
//            Element element = elements.get(i);
//            System.out.println("isi element : "+element.toString());
//            System.out.println("");
//        }
//
//        List<Review> parsedReviews = new LinkedList<Review>();
//        return parsedReviews;
//    }
}
