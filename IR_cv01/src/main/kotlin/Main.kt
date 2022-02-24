import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.io.*
import java.util.*


import cz.zcu.kiv.nlp.ir.AbstractHTMLDownloader

import cz.zcu.kiv.nlp.ir.HTMLDownloader

import cz.zcu.kiv.nlp.ir.HTMLDownloaderSelenium

import cz.zcu.kiv.nlp.ir.Utils

import cz.zcu.kiv.nlp.vs.CrawlerVSCOM





fun main(args: Array<String>) {
    //Initialization

    //Initialization
    BasicConfigurator.configure()
    Logger.getRootLogger().level = Level.INFO
    val outputDir: File = File(CrawlerVSCOM.STORAGE)
    if (!outputDir.exists()) {
        val mkdirs = outputDir.mkdirs()
        if (mkdirs) {
            cz.zcu.kiv.nlp.vs.CrawlerVSCOM.log.info("Output directory created: $outputDir")
        } else {
            cz.zcu.kiv.nlp.vs.CrawlerVSCOM.log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: $outputDir")
        }
    }
//        HTMLDownloader downloader = new HTMLDownloader();
    //        HTMLDownloader downloader = new HTMLDownloader();
    val downloader: AbstractHTMLDownloader = HTMLDownloaderSelenium()
    val results: MutableMap<String, MutableMap<String, List<String>>> = HashMap()

    for (key in cz.zcu.kiv.nlp.vs.CrawlerVSCOM.xpathMap.keys) {
        val map: MutableMap<String, List<String>> = HashMap()
        results[key] = map
    }

//        Collection<String> urlsSet = new ArrayList<String>();

//        Collection<String> urlsSet = new ArrayList<String>();
    val urlsSet: MutableCollection<String> = HashSet()
    val printStreamMap: MutableMap<String, PrintStream?> = HashMap()

    //Try to load links

    //Try to load links
    //val links: File = File(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.STORAGE + "_urls.txt")
    val links: File = File(CrawlerVSCOM.STORAGE + "2022-02-24_20_29_690_links_size_3925.txt")
    if (links.exists()) {
        try {
            val lines: List<String> = Utils.readTXTFile(FileInputStream(links))
            for (line in lines) {
                urlsSet.add(line)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
    var max = 10000000
    var i = 0
    var lastCount = urlsSet.size
    var errorCount = 0
    var addition = 0
    while (lastCount < max) {
        try {
            val link: String =
                cz.zcu.kiv.nlp.vs.CrawlerVSCOM.SITE + cz.zcu.kiv.nlp.vs.CrawlerVSCOM.SITE_SUFFIX + "?hledat=&hlokalita=&humkreis=10000&cenaod=&cenado=&order=&crz=" + lastCount
            urlsSet.addAll(downloader.getLinks(link, "//div[contains(@class, 'maincontent')]//div[contains(@class, 'inzeraty')]//h2[contains(@class, 'nadpis')]/a/@href"))
            var found = urlsSet.size - (lastCount - addition)
            if(found < 5){
                ++errorCount
                if(errorCount > 1500){
                    println("Everything died!")
                    break
                }
                lastCount += 20
                addition += 20
                Thread.sleep(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.POLITENESS_INTERVAL.toLong())
                continue
            }
            lastCount = urlsSet.size + addition
            errorCount = 0
            Thread.sleep(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.POLITENESS_INTERVAL.toLong())
        }catch(e: Exception){
            ++errorCount
            if(errorCount > 1500){
                println("Everything died!")
                break
            }
            Thread.sleep(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.POLITENESS_INTERVAL.toLong())
        }
    }
    Utils.saveFile(
        File(
            cz.zcu.kiv.nlp.vs.CrawlerVSCOM.STORAGE + Utils.SDF.format(System.currentTimeMillis())
                .toString() + "_links_size_" + urlsSet.size.toString() + ".txt"
        ),
        urlsSet
    )
    //todo remove
    return
    for (key in results.keys) {
        val file: File =
            File(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + ".txt")
        var printStream: PrintStream? = null
        try {
            printStream = PrintStream(FileOutputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        printStreamMap[key] = printStream
    }

    var count = 0
    for (url in urlsSet) {

        try {
            var link = url
            if (!link.contains(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.SITE_END)) {
                link = cz.zcu.kiv.nlp.vs.CrawlerVSCOM.SITE + url
            }
            //Download and extract data according to xpathMap
            val products: Map<String, List<String>> = downloader.processUrl(link, cz.zcu.kiv.nlp.vs.CrawlerVSCOM.xpathMap)
            count++
            if (count % 100 == 0) {
                cz.zcu.kiv.nlp.vs.CrawlerVSCOM.log.info(count.toString() + " / " + urlsSet.size + " = " + count / (0.0 + urlsSet.size) + "% done.")
            }
            for (key in results.keys) {
                val map = results[key]!!
                val list = products[key]
                if (list != null) {
                    map[url] = list
                    cz.zcu.kiv.nlp.vs.CrawlerVSCOM.log.info(list.toTypedArray().contentToString())
                    //print
                    val printStream = printStreamMap[key]
                    for (result in list) {
                        printStream!!.println(url + "\t" + result)
                    }
                }
            }
            Thread.sleep(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.POLITENESS_INTERVAL.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch(e: Exception){
            println("Error during site parsing: ${e.printStackTrace()}")
        }finally {
            Thread.sleep(cz.zcu.kiv.nlp.vs.CrawlerVSCOM.POLITENESS_INTERVAL.toLong())
        }
    }

    //close print streams

    //close print streams
    for (key in results.keys) {
        val printStream = printStreamMap[key]
        printStream!!.close()
    }

    // Save links that failed in some way.
    // Be sure to go through these and explain why the process failed on these links.
    // Try to eliminate all failed links - they consume your time while crawling data.

    // Save links that failed in some way.
    // Be sure to go through these and explain why the process failed on these links.
    // Try to eliminate all failed links - they consume your time while crawling data.
    cz.zcu.kiv.nlp.vs.CrawlerVSCOM.reportProblems(downloader.getFailedLinks())
    downloader.emptyFailedLinks()
    cz.zcu.kiv.nlp.vs.CrawlerVSCOM.log.info("-----------------------------")


//        // Print some information.
//        for (String key : results.keySet()) {
//            Map<String, List<String>> map = results.get(key);
//            Utils.saveFile(new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + "_final.txt"),
//                    map, idMap);
//            log.info(key + ": " + map.size());
//        }


//        // Print some information.
//        for (String key : results.keySet()) {
//            Map<String, List<String>> map = results.get(key);
//            Utils.saveFile(new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + "_final.txt"),
//                    map, idMap);
//            log.info(key + ": " + map.size());
//        }
    System.exit(0)
}