package trec.preprocessing

import trec.evaluate.BooleanQuery
import trec.evaluate.IQuery
import trec.evaluate.NormalQuery
import trec.evaluate.QueryType
import trec.utils.Logger
import java.util.*
import kotlin.collections.ArrayList

object QueryParser {


    fun parse(queryType: QueryType, query: String) : IQuery?{
        val parseQuery = query.toLowerCase()
        if(queryType == QueryType.BOOLEAN){
            Logger.debug("Parsing of boolean query started!")
            val parsedBool = parseBoolean(parseQuery)
            //now to validate the result

            var containsKeywords = false
            parsedBool.forEach {
                if(it.contains(")") || it.contains("(")){
                    Logger.error("Parsing of boolean query failed! Invalid format!")
                    return null
                }else if(it == "and" || it == "or" || it == "not") {
                    containsKeywords = true
                }
            }
            Logger.debug("Boolean query after stemming: $parsedBool")
            if(containsKeywords){
                return BooleanQuery(parsedBool)
            }
        }else{
            Logger.debug("Parsing of vector query started!")
            val tokens = Tokenizer.tokenize(query)
            for(i in 0 until tokens.size){
                tokens[i] = LightStemmer.stem(tokens[i])
            }
            Logger.debug("Query after stemming: $tokens")
            return NormalQuery(tokens)
        }
        Logger.error("Parsing of query failed! Invalid format!")
        return null
    }


    private fun parseBoolean(query: String): ArrayList<String>{
        var procQuery = query.replace("(", " ( ")
        procQuery = procQuery.replace("(", " ( ")

        val tokens = procQuery.split(" ").filter {it != ""}

        val stack = Stack<String>()
        val result = arrayListOf<String>()
        tokens.forEach {
            token ->
            when(token){
                "or", "and", "not" -> {
                    if(stack.isNotEmpty() && stack.peek() != "("){
                        result.add(token)
                    }
                    stack.push(token)
                }
                "(" -> {
                    stack.push(token)
                }
                ")" -> {
                    var popin = true
                    while(popin){
                        val top = stack.pop()
                        if(top == "("){
                            popin = false
                        }else{
                            result.add(top)
                        }
                    }
                }
                else -> {
                    result.add(token)
                }
            }
        }
        while(stack.isNotEmpty()){
            result.add(stack.pop())
        }

        return result
    }
}