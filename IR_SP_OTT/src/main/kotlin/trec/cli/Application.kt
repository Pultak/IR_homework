package trec.cli

import trec.evaluate.QueryType
import trec.preprocessing.QueryParser
import java.util.regex.Pattern

class Application {

    private var running = false;

    private var indexer : IIndexer? = null



    fun run(){


    }


    private fun signPostLoop(){

        running = true
        loop@ while(running){

            val input = readLine()

            if(input.isNullOrEmpty()){
                running = false
                break
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
        println("Please write down 'help' to the command line for more information.")
    }


    private fun queryLoop(){
        if(!indexed){
            println("Please index some data before using queries!")
            return
        }

        println("Welcome to query mode! Please insert your query in following format: ")
        println("'query <normal-query>' or for boolean query insert this 'boolean <bool-query>'")

        loop@ while(true){

            val input = readLine()
            if(input.isNullOrEmpty()){
                running = false;
                return
            }
            val tokens = input.split(Pattern.compile(" "), 2)
            if(tokens.size < 2){
                println("No query inserted! Please try again!")
                continue
            }

            when(tokens[0]){
                "exit", "e" -> break@loop
                "boolean", "b" -> QueryParser.parse(QueryType.BOOLEAN, tokens[1]).evaluate()
                "query", "q" -> QueryParser.parse(QueryType.NORMAL, tokens[1]).evaluate()
                else -> println("Unknown command! Please try again!")
            }
        }
    }


    private fun indexLoop(){

        println("Welcome to indexer submenu.")
        println("Please select one of the following options:")
        println("Start new indexing process -> 'index <file-name>'")
        println("Load old index -> 'load <file-name>'")

        loop@ while(true){
            val input = readLine()
            if(input.isNullOrEmpty()){
                running = false;
                return
            }
            val tokens = input.split(Pattern.compile(" "), 2)
            if(tokens.size < 2){
                println("No query inserted! Please try again!")
                continue
            }

            when(tokens[0]){
                "exit", "e" -> break@loop
                "index", "i" -> QueryParser.parse(QueryType.BOOLEAN, tokens[1]).evaluate()
                "load", "l" -> QueryParser.parse(QueryType.NORMAL, tokens[1]).evaluate()
                else -> println("Unknown command! Please try again!")
            }
        }


        indexed = true;
    }

    private fun printHelp(){
        println("Usage:")
        println("'help' or 'h' -> prints information about usage of this application" )
        println("'index' or 'i' -> command brings you to the " )


    }


}