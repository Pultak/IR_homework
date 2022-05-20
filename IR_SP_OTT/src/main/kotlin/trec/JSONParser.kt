package trec

import trec.IOUtils.readFolder
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>){

    System.setProperty("file.encoding", "UTF-8")
    val defaultencoding = System.getProperty("file.encoding")

    // Return the above string of character encoded

    // Return the above string of character encoded
    println(
        "Default Charset: "
                + defaultencoding
    )
    println("Your working directory is ${Paths.get("").toAbsolutePath()}.")
    //val docs = IOUtils.readFolder(File("trec-all"))

    //todo date format
    //val documents = arrayListOf<IDocument>(Document("text", "title", ""), Document("text", "title", Date()))



    val potat = readFolder(File("mydatasmall"))

    println()
    /*
    dateCreated -> dateTime
    price x
    seenCOunt x
    name x
    mainText -> article
    locatino x
    title OK
     */

}

