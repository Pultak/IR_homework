package trec

import cz.zcu.kiv.nlp.ir.trec.Index

import trec.data.IDocument
import trec.data.IResult
import trec.utils.Logger
import java.io.*
import java.util.*

const val OUTPUT_DIR = "./TREC"


/**
 * Metoda vytvoří objekt indexu, načte dhgutuyata, zaindexuje je provede předdefinované dotazy a výsledky vyhledávání
 * zapíše souboru a pokusí se spustit evaluační skript.
 *
 * Na windows evaluační skript pravděpodbně nebude možné spustit. Pokud chcete můžete si skript přeložit a pak
 * by mělo být možné ho spustit.
 *
 * Pokud se váme skript nechce překládat/nebo se vám to nepodaří. Můžete vygenerovaný soubor s výsledky zkopírovat a
 * spolu s přiloženým skriptem spustit (přeložit) na
 * Linuxu např. pomocí vašeho účtu na serveru ares.fav.zcu.cz
 *
 * Metodu není třeba měnit kromě řádků označených T O D O  - tj. vytvoření objektu třídy [Index] a
 */
fun main() {




    /*val queries = arrayListOf<HashMap<String, Int>>()
    for(query in q){
        queries.add(tokenizeString(query))
    }

    val index = indexDocs(docs);


    val results1 = HashMap<String, Double>()

    docs.forEachIndexed { i, doc ->
        queries.forEachIndexed { j, query ->
            val result = evaluateQueries(index, tokenizeString(doc), query, docs.size)
            results1["Dokument$i: $doc; Query$j: ${query.keys}"] = result
        }
    }

    results1.toSortedMap().forEach{ it ->
        println(it)
    }*/

}
fun main(args: Array<String>) {

}
