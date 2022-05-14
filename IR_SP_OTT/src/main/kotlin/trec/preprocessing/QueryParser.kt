package trec.preprocessing

import trec.evaluate.BooleanQuery
import trec.evaluate.IQuery
import trec.evaluate.QueryType

object QueryParser {


    fun parse(queryType: QueryType, query: String) : IQuery{
        if(queryType == QueryType.BOOLEAN){

        }else{

        }
        //todo missing implementation

        return BooleanQuery()
    }


    private fun parseBoolean(){


    }

}