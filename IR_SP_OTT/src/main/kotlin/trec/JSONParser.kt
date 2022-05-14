package trec
import com.beust.klaxon.Klaxon
import trec.data.Document
import java.io.File

fun main(args: Array<String>){

    if(args.size < 2){
        println("Not enough parameters passed! Usage: JSONParser <indexed-data-json>")
    }
    val result = Klaxon().parse<Array<Document>>(File("mydatasmall.json"))


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

