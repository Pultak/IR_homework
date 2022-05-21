package trec.cli

import trec.IOUtils
import trec.data.IResult
import trec.evaluate.IQuery
import trec.evaluate.QueryType
import trec.indexing.IIndexer
import trec.indexing.Indexer
import trec.preprocessing.LightStemmer
import trec.preprocessing.QueryParser
import trec.preprocessing.Tokenizer
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.math.min

class Application {

    private val pageShowCount: Int = 10

    private var running = false

    private var indexer : IIndexer? = null

    fun run(){
        printIntroduction()

/*
        //todo remove later
        val docs = IOUtils.readFolder(File("mydatasmall"))
        indexer = Indexer()
        indexer!!.index(docs)
        //todo
*/
        //todo remove
        indexer = Indexer()
        indexer!!.loadIndexedData(File("aloha"))

        //val query = QueryParser.parse(QueryType.NORMAL, "Prodám elektrický impulzní ohradník")
        val query = QueryParser.parse(QueryType.BOOLEAN, "Prodám AND ohradník")

        printResults(query!!.evaluate(indexer!!), query)


        signpostLoop()
        println("See you again soon!")
    }


    private fun signpostLoop(){
        running = true
        loop@ while(running){

            print(">")
            val input = readLine()

            if(input == null){
                running = false
                println("Null line inserted! Terminating")
                break
            }else if(input.isEmpty()){
                continue@loop
            }

            when(input.trim()){
                "exit", "e" -> running = false
                "index", "i" -> indexLoop()
                "query", "q" -> queryLoop()
                "help", "h" -> printHelp()
                else -> println("Unknown command! To show possible command list please insert 'help' or 'h'!")
            }
        }

    }

    private fun printIntroduction(){
        println("Welcome to indexer application made for subject KIV/IR.")
        println("Your working directory is ${Paths.get("").toAbsolutePath()}.")
        println("Please write down 'help' to the command line for more information.")
    }


    private fun queryLoop(){
        if(indexer == null){
            println("Please index some data before using queries!")
            return
        }

        println("Welcome to query mode! Please insert your query in following format: ")
        println("'query <normal-query>' or for boolean query insert this 'boolean <bool-query>'")

        loop@ while(true){

            print(">")
            val input = readLine()
            if(input == null){
                println("Null line inserted! Terminating!")
                running = false
                return
            }else if(input.isEmpty()){
                continue@loop
            }
            val tokens = input.split(Pattern.compile(" "), 2)
            if(tokens.isNotEmpty() && (tokens[0] == "exit" || tokens[0] == "e")){
                println("Exiting query menu")
                break@loop
            }
            if(tokens.size < 2){
                println("No query inserted! Please try again!")
                continue
            }

            val actualQuery = when(tokens[0]){
                "boolean", "b" -> QueryParser.parse(QueryType.BOOLEAN, tokens[1])
                "query", "q" -> QueryParser.parse(QueryType.NORMAL, tokens[1])
                else -> {
                    println("Unknown command! Please try again!")
                    continue@loop
                }
            }
            if(actualQuery != null){
                val hits = actualQuery.evaluate(this.indexer!!)
                printResults(hits, actualQuery)
            }
        }
    }

    private fun printResults(hits: ArrayList<IResult>, query: IQuery){
        println("Found ${hits.size} hits.")

        val hitsSize = hits.size
        var i = 0
        println("________________")
        loop@ while (i < hitsSize){
            val hit = hits[i]
            println("$hit")
            val lastHit = i == (hitsSize - 1)
            if ((i + 1) % pageShowCount == 0 || lastHit) {
                println("________________")
                if(!lastHit){
                    println("Next page? 'f'")
                }
                //are there any previous hits?
                if(i/pageShowCount >= 1){
                    println("Previous page? 'b'")
                }
                println("Show more doc info? -> 's <rank>'")
                println("other -> NO, return to query")
                print(">")
                val response = readLine()
                if(response.isNullOrBlank()){
                    return
                }
                val tokens = response.split(Pattern.compile(" "), 2)
                when(tokens[0]){
                    "s" -> {
                        showDocument(tokens, hits, query)
                        i -= pageShowCount
                        if(i < 0) i = 0
                    }
                    "f" -> {
                        ++i
                        continue@loop
                    }
                    "b" -> {
                        i -= (pageShowCount * 2) - 1
                        if(i < 0) i = 0
                    }
                    else -> {
                        return
                    }
                }
                println("________________")
                continue
            }
            ++i
        }
    }


    private fun showDocument(tokens: List<String>, hits: ArrayList<IResult>, query: IQuery){
        if(tokens.size < 2){
            println("To show more, enter 's <rank>' in correct format please!")
        }else{

            val rank = try {
                tokens[1].toInt() - 1
            }catch (e: NumberFormatException){
                println("Please enter valid rank!")
                return
            }
            if(rank < 0 || rank >= hits.size){
                println("Please enter valid rank!")
                return
            }
            val filePath = hits[rank].documentID
            println("Showing ${rank + 1}. ranked document which is located on $filePath")

            var stringDoc: String = IOUtils.readLines(File(filePath)) ?: return

            val docTokens = Tokenizer.tokenize(stringDoc, false)
            val termsMap = LightStemmer.stemWithMap(docTokens)

            query.terms.forEachIndexed{ i, term ->
                termsMap[term]?.forEach {token ->
                    stringDoc = stringDoc.replace(token, "|$i|$token|")
                }

            }
            println("_____________________________")
            println(stringDoc)
            println("_____________________________")
            println("Press enter to continue")
            readLine()
        }
    }

    private fun indexLoop(){

        println("Welcome to index menu.")
        println("Please select one of the following options:")
        println("Start new indexing process -> 'index <folder-name>'")
        println("Save actual index to file -> 'save <file-name>'")
        println("Load old index -> 'load <file-name>'")

        loop@ while(true){
            print(">")
            val input = readLine()
            if(input == null){
                println("Null line inserted! Terminating!")
                running = false
                return
            }else if(input.isEmpty()){
                continue@loop
            }
            val tokens = input.split(Pattern.compile(" "), 2)
            if(tokens.isNotEmpty() && (tokens[0] == "exit" || tokens[0] == "e")){
                println("Exiting index menu")
                break@loop
            }
            if(tokens.size < 2){
                println("No command inserted! Please try again!")
                continue
            }

            when(tokens[0]){
                "index", "i" ->{
                    val docs = IOUtils.readFolder(File(tokens[1]))
                    if(docs.size > 0){
                        indexer = Indexer()
                        indexer!!.index(docs)
                    }
                }
                "save", "s" -> {
                    if(indexer == null){
                        trec.utils.Logger.error("You need to have index loaded first! Terminating!")
                        continue@loop
                    }
                    trec.utils.Logger.info("Index saved: ${indexer!!.saveIndexedData(File(tokens[1]))}")
                }
                "load", "l" -> {
                    if(indexer == null){
                        indexer = Indexer()
                    }
                    trec.utils.Logger.info("Index loaded: ${indexer!!.loadIndexedData(File(tokens[1]))}")
                }
                else -> println("Unknown command! Please try again!")
            }
        }
    }

    private fun printHelp(){
        println("Usage:")
        println("'help' or 'h' -> prints information about usage of this application" )
        println("'index' or 'i' -> open indexing menu where you can index your data or load saved indexes" )
        println("'query' or 'q' -> open query menu where you can execute your queries" )
        println("'exit' or 'e' -> exits the application" )


    }


}