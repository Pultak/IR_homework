package trec.cli

import java.io.Console

class Application {

    private var running = false;

    fun run(){


    }


    private fun signPostLoop(){

        running = true
        loop@ while(running){

            val input = readLine()

            if(input.isNullOrEmpty()){
                running = false;
                break;
            }

            val tokens = input.split(" ")

            if(tokens.isNullOrEmpty()){
                continue
            }


            when(tokens[0]){
                "exit", "e" -> running = false
                "index", "i" -> indexLoop()
                "query", "q" -> queryLoop()
                "help", "h" -> printHelp()
            }
        }

    }

    private fun printIntroduction(){
        println("Welcome to indexer application made for subject KIV/IR.")
        println("Please write down 'help' to the command line for more information.")
    }


    private fun queryLoop(){

        while(true){



        }

    }


    private fun indexLoop(){


        readLine()
    }

    private fun printHelp(){
        println("Usage:")
        println("'help' or 'h' -> prints information about usage of this application" )
        println("'index' or 'i' -> command brings you to the " )


    }


}