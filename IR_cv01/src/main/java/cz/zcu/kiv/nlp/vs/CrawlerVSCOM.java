package cz.zcu.kiv.nlp.vs;

import cz.zcu.kiv.nlp.ir.AbstractHTMLDownloader;
import cz.zcu.kiv.nlp.ir.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.Utils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * CrawlerVSCOM class acts as a controller. You should only adapt this file to serve your needs.
 * Created by Tigi on 31.10.2014.
 */
public class CrawlerVSCOM {
    /**
     * Xpath expressions to extract and their descriptions.
     */
    public final static Map<String, String> xpathMap = new HashMap<String, String>();

    static {
        xpathMap.put("mainText", "//div[contains(@class, 'maincontent')]//div[contains(@class, 'popisdetail')]//text()");
        xpathMap.put("title", "//div[contains(@class, 'maincontent')]//h1[contains(@class, 'nadpisdetail')]//text()");
        xpathMap.put("dateCreated", "//div[contains(@class, 'inzeratydetnadpis')]//span[contains(@class, 'velikost10')]//text()");
        xpathMap.put("info", "//td[contains(@class, 'listadvlevo')]");
        //xpathMap.put("author", "(//td[contains(@class, 'listadvlevo')]/table//tr[1]/td[2]/text()");
        //xpathMap.put("location", "//td[contains(@class, 'listadvlevo')]/table//tr[3]/td[3]/text()");
        //xpathMap.put("seen", "//td[contains(@class, 'listadvlevo')]/table//tr[4]/td[2]/text()");
        //xpathMap.put("cost", "//td[contains(@class, 'listadvlevo')]/table//tr[5]/td[2]/text()");
    }

    public static final String STORAGE = "./storage/VSCOMTest";
    public static String SITE = "https://www.bazos.cz";
    public static String SITE_END = "bazos.cz";
    public static String SITE_SUFFIX = "/search.php";


    /**
     * Be polite and don't send requests too often.
     * Waiting period between requests. (in milisec)
     */
    public static final int POLITENESS_INTERVAL = 2500;
    public static final Logger log = Logger.getLogger(CrawlerVSCOM.class);


    /**
     * Save file with failed links for later examination.
     *
     * @param failedLinks links that couldn't be downloaded, extracted etc.
     */
    public static void reportProblems(Set<String> failedLinks) {
        if (!failedLinks.isEmpty()) {

            Utils.saveFile(new File(STORAGE + Utils.SDF.format(System.currentTimeMillis()) + "_undownloaded_links_size_" + failedLinks.size() + ".txt"),
                    failedLinks);
            log.info("Failed links: " + failedLinks.size());
        }
    }


}
