import cz.zcu.kiv.nlp.ir.AbstractHTMLDownloader
import cz.zcu.kiv.nlp.ir.HTMLDownloaderSelenium
import cz.zcu.kiv.nlp.ir.Utils
import cz.zcu.kiv.nlp.vs.CrawlerVSCOM
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup
import java.io.*
import java.text.ParseException
import java.util.*
import kotlin.system.exitProcess

fun loadJSON(){
    val parser = JSONParser()

    try {
        FileReader("test.json").use { reader ->
            val root = parser.parse(reader) as JSONArray
            // loop array
            val iterator = root.iterator()
            while (iterator.hasNext()) {
                println(iterator.next())
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}


/**
 * Function that parses the rest of table that the selenium could not parse for some reason
 */
fun parseTableInfo(tableHtml: String, parentSite: JSONObject){
    try {
        val doc = Jsoup.parse(tableHtml)
        val rows = doc.body().child(0).child(0).children()

        val name = rows[0].children()[1].text()
        parentSite["name"] = name

        val location = rows[2].children()[2].text()
        parentSite["location"] = location

        val seenCount = rows[3].children()[1].text()
        parentSite["seenCount"] = seenCount

        val price =  rows[4].children()[1].text()
        parentSite["price"] = price
    }catch(e: Exception){
        CrawlerVSCOM.log.error(parentSite["page"].toString() + " info table structure is broken and cant be parsed!")
    }
}

fun collectedWebLinks(urlsSet: MutableCollection<String>, downloader: AbstractHTMLDownloader){
    //Try to load links

    val max = CrawlerVSCOM.LINK_COLLECTION_COUNT
    var lastCount = urlsSet.size
    var errorCount = 0
    //int addition to prevent the crawler to be stuck on one page
    var addition = 0
    while (lastCount < max) {
        try {
            val link: String =
                CrawlerVSCOM.SITE + CrawlerVSCOM.SITE_SUFFIX + "?hledat=&hlokalita=&humkreis=10000&cenaod=&cenado=&order=&crz=" + lastCount
            urlsSet.addAll(downloader.getLinks(link, "//div[contains(@class, 'maincontent')]//div[contains(@class, 'inzeraty')]//h2[contains(@class, 'nadpis')]/a/@href"))
            val found = urlsSet.size - (lastCount - addition)
            if(found < 5){
                ++errorCount
                if(errorCount > CrawlerVSCOM.MAX_FAILED_COUNT){
                    CrawlerVSCOM.log.error("Failed to crawl desired count of links!")
                    break
                }
                lastCount += 20
                addition += 20
                Thread.sleep(CrawlerVSCOM.POLITENESS_INTERVAL)
                continue
            }
            lastCount = urlsSet.size + addition
            errorCount = 0
            Thread.sleep(CrawlerVSCOM.POLITENESS_INTERVAL)
        }catch(e: Exception){
            ++errorCount
            if(errorCount > CrawlerVSCOM.MAX_FAILED_COUNT){
                CrawlerVSCOM.log.error("Failed to crawl desired count of links!")
                break
            }
            Thread.sleep(CrawlerVSCOM.POLITENESS_INTERVAL)
        }
    }
    //save the collected links
    Utils.saveFile(
        File(
            CrawlerVSCOM.STORAGE + Utils.SDF.format(System.currentTimeMillis())
                .toString() + "_links_size_" + urlsSet.size.toString() + ".txt"
        ),
        urlsSet
    )

}

fun createOutputDir(){
    val outputDir = File(CrawlerVSCOM.STORAGE)
    if (!outputDir.exists()) {
        val mkdirs = outputDir.mkdirs()
        if (mkdirs) {
            CrawlerVSCOM.log.info("Output directory created: $outputDir")
        } else {
            CrawlerVSCOM.log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: $outputDir")
        }
    }
}

fun parseFile(links: File, urlsSet: MutableCollection<String>){
    try {
        val lines: List<String> = Utils.readTXTFile(FileInputStream(links))
        for (line in lines) {
            urlsSet.add(line)
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}


fun main(args: Array<String>) {
    //Initialization
    BasicConfigurator.configure()
    Logger.getRootLogger().level = Level.INFO
    createOutputDir()

    //HTMLDownloader downloader = new HTMLDownloader();
    val downloader: AbstractHTMLDownloader = HTMLDownloaderSelenium()

    val urlsSet: MutableCollection<String> = HashSet()
    //val printStreamMap: MutableMap<String, PrintStream?> = HashMap()

    //are the links already prepared?
    if(args.isNotEmpty()){
        val links = File(args[0])
        if(!links.exists()){
            Logger.getRootLogger().error( "You need to pass valid file!")
            return
        }
        parseFile(links, urlsSet)
    }else{
        collectedWebLinks(urlsSet, downloader)
    }

    val root = JSONArray()
    var count = 0
    for (url in urlsSet) {
        try {
            val page = JSONObject()
            //first parse the link
            var link = url
            if (!link.contains(CrawlerVSCOM.SITE_END)) {
                link = CrawlerVSCOM.SITE + url
            }
            page["page"] = link

            //Download and extract data according to xpathMap
            val products: Map<String, List<String>> = downloader.processUrl(link, CrawlerVSCOM.xpathMap)
            count++
            if (count % 100 == 0) {
                CrawlerVSCOM.log.info(count.toString() + " / " + urlsSet.size + " = " + count / (0.0 + urlsSet.size) + "% done.")
            }
            for (entry in products.entries) {
                if(entry.key == "info"){
                    parseTableInfo(entry.value[0], page)
                }else{
                    page[entry.key] = entry.value[0]
                }
                CrawlerVSCOM.log.info(entry.value.toString())
            }
            root.add(page)
            Thread.sleep(CrawlerVSCOM.POLITENESS_INTERVAL)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch(e: Exception){
            CrawlerVSCOM.log.error("Unexpected error during site parsing: ${e.printStackTrace()}")
        }finally {
            Thread.sleep(CrawlerVSCOM.POLITENESS_INTERVAL)
        }
    }

    try {
        FileWriter(CrawlerVSCOM.STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + ".txt")
            .use { file -> file.write(root.toJSONString()) }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    // Save links that failed in some way.
    // Be sure to go through these and explain why the process failed on these links.
    // Try to eliminate all failed links - they consume your time while crawling data.

    // Save links that failed in some way.
    // Be sure to go through these and explain why the process failed on these links.
    // Try to eliminate all failed links - they consume your time while crawling data.
    CrawlerVSCOM.reportProblems(downloader.failedLinks)
    downloader.emptyFailedLinks()
    downloader.quit()
    CrawlerVSCOM.log.info("-----------------------------")
    exitProcess(0)
}