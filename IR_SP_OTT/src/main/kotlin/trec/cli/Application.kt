package trec.cli

import trec.IOUtils
import trec.data.IResult
import trec.evaluate.IQuery
import trec.evaluate.QueryType
import trec.indexing.IIndexer
import trec.indexing.Indexer
import trec.preprocessing.AggresiveStemmer
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

/***
* Count of pages that will be shown during the result presenting
*/
    private val pageShowCount: Int = 10

/**
*	Flag that indicates if this application is running
*/
    private var running = false

/**
*	Instance of currently loaded indexer
*/
    private var indexer : IIndexer? = null

/**
* Method inits and executes the CLI + prints the introduction 
*/
    fun run(){
        printIntroduction()
        signpostLoop()
        println("See you again soon!")
    }


/**
*	Method used to show user possible functions of this application -> mainly used as signpost
*/
    private fun signpostLoop(){
        running = true
        loop@ while(running){

            print(">")
            val input = readLine()

            if(input == null){
                running = false
                //posible EOT or ETX
                println("Null line inserted! Terminating")
                break
            }else if(input.isEmpty()){
            //empty line inserted
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


/**
*	Method used to process all the query related tasks. 
*	It is possible to used vector or boolean based queries.
* 	To use this method, you need to have index loaded.
*/
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
            //get the command part and leave the rest
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
                "boolean", "b" -> QueryParser.parse(tokens[1])
                "query", "q" -> QueryParser.parse(tokens[1])
                else -> {
                    println("Unknown command! Please try again!")
                    continue@loop
                }
            }
            //was some query parsed successfully?
            if(actualQuery != null){
                val hits = actualQuery.evaluate(this.indexer!!)
                printResults(hits, actualQuery)
            }
        }
    }

/**
*	Method used to print all the hits that were received from the inserted query.
*	Results are showed in pages, where if you need a more detailed view you can insert 's' command
*/
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
                //get first command and leave rest without spliting
                val tokens = response.split(Pattern.compile(" "), 2)
                when(tokens[0]){
                //need to show more information about the document?
                    "s" -> {
                        showDocument(tokens, hits, query)
                        i -= pageShowCount
                        if(i < 0) i = 0
                    }
                    //wanna see next page?
                    "f" -> {
                        ++i
                        continue@loop
                    }
                    //wanna see previous page?
                    "b" -> {
                        i -= (pageShowCount * 2) - 1
                        if(i < 0) i = 0
                    }
                    //user wants to return from page results
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


/**
*	Method used to show the contents of document. First parses and validates the passed rank number.
*	Then gets the document from file and highlights the searched keywords.
*
*	@param tokens	list of ranks to show
*	@param hits		all hits that were received from actual query
*	@param query	actual query which resulted in these hits
*/
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

			//loads the contents of doc file
            var stringDoc: String = IOUtils.readLines(File(filePath)) ?: return

			//tokenizes the doc and stems it -> for searched words highlighting 
            val docTokens = Tokenizer.tokenize(stringDoc, false)
            val termsMap = AggresiveStemmer.stemWithMap(docTokens)

			//highligh all searched words
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


/**
*	Method used to show and process all the possible index related tasks
*/
    private fun indexLoop(){

        println("Welcome to index menu.")
        println("Please select one of the following options:")
        println("Start new indexing process -> 'index <folder-name>'")
        println("Save actual index to file -> 'save <file-name>'")
        println("Load old index -> 'load <file-name>'")
        println("Add document to current index -> 'add <file-name>'")

        loop@ while(true){
            print(">")
            val input = readLine()
            if(input == null){
            //EOT or ETX possibly received
                println("Null line inserted! Terminating!")
                running = false
                return
            }else if(input.isEmpty()){
            //empty line inserted
                continue@loop
            }
            //get first command and leave rest without spliting
            val tokens = input.split(Pattern.compile(" "), 2)
            if(tokens.isNotEmpty() && (tokens[0] == "exit" || tokens[0] == "e")){
                println("Exiting index menu")
                break@loop
            }
            if(tokens.size < 2){
                println("No command arguments inserted! Please try again!")
                continue
            }

            when(tokens[0]){
            //indexing of new data 
                "index", "i" ->{
                    val docs = IOUtils.readFolder(File(tokens[1]))
                    if(docs.size > 0){
                        indexer = Indexer()
                        indexer!!.index(docs)
                    }
                }
                "add", "a" -> {
                    val file = File(tokens[1])
                    trec.utils.Logger.info("New doc (${file.name}) to index added: ${indexer?.addDoc(file)}")
                }
                //saving of the actual index 
                "save", "s" -> {
                    if(indexer == null){
                        trec.utils.Logger.error("You need to have index loaded first! Terminating!")
                        continue@loop
                    }
                    trec.utils.Logger.info("Index saved: ${indexer!!.saveIndexedData(File(tokens[1]))}")
                }
                //loading of saved index from the filesystem
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
