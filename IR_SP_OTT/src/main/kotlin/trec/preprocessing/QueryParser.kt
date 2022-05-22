package trec.preprocessing

import trec.evaluate.BooleanQuery
import trec.evaluate.IQuery
import trec.evaluate.NormalQuery
import trec.evaluate.QueryType
import trec.utils.Logger
import java.util.*
import kotlin.collections.ArrayList

object QueryParser {


    fun parse(query: String) : IQuery?{
        val parseQuery = query.toLowerCase()
        Logger.debug("parse -> New query received")
        val queryType = getQueryType(query)

        if(queryType == QueryType.BOOLEAN){
            Logger.debug("parse -> Parsing of boolean query started!")

            var leftCount = 0
            var rightCount = 0
            for(char in parseQuery){
                if(char == '(') ++leftCount
                else if(char == ')') ++rightCount
            }
            if(leftCount != rightCount){
                Logger.error("parse -> Brackets dont match!")
                return null
            }

            val parsedBool = parseBool(parseQuery)
            //now to validate the result and stem tokens

            var containsKeywords = false
            for(i in 0 until parsedBool.size){
                val it = parsedBool[i]
                if(it.contains(")") || it.contains("(")){
                    Logger.error("parse -> Parsing of boolean query failed! Invalid format!")
                    return null
                }else if(it == "and" || it == "or" || it == "not") {
                    containsKeywords = true
                }else{
                    parsedBool[i] = AggresiveStemmer.stem(it)
                }
            }
            Logger.debug("parse -> Boolean query after stemming: $parsedBool")
            if(containsKeywords){
                return BooleanQuery(parsedBool)
            }
            Logger.error("parse -> Non valid boolean query!")
        }
        else{
            //parsing vector query
            Logger.debug("parse -> Parsing of vector query started!")
            val tokens = Tokenizer.tokenize(query)
            for(i in 0 until tokens.size){
                tokens[i] = AggresiveStemmer.stem(tokens[i])
            }
            Logger.debug("parse -> Query after stemming: $tokens")
            return NormalQuery(tokens)
        }
        Logger.error("parse -> Parsing of query failed! Invalid format!")
        return null
    }

    private fun getQueryType(query: String): QueryType{
        val query2 = query.toLowerCase()
        return if(query2.contains(" and ") || query2.contains(" or ") || query2.contains(" not ")){
                QueryType.BOOLEAN
            }else QueryType.NORMAL
    }

    private fun parseBool(query: String): ArrayList<String>{
        var procQuery = query.replace("(", " ( ")
        procQuery = procQuery.replace(")", " ) ")

        val tokens = procQuery.split(" ").filter {it != ""}

        val stack = Stack<String>()
        val result = arrayListOf<String>()
        tokens.forEach {
            token ->
            when(token){
                "and", "or", "not" -> {
                    if(stack.isNotEmpty() && stack.peek() != "("){
                        result.add(stack.pop())
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