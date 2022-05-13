package trec.evaluate

import kotlin.math.log10
import kotlin.math.sqrt


object Evaluator{

    fun findUniques(dict1: HashMap<String, Int>, dict2: HashMap<String, Int>): List<String>{
        val uniques = arrayListOf<String>()
        uniques.addAll(dict1.keys)
        uniques.addAll(dict2.keys)
        return uniques.distinct()
    }


    fun evaluateQueries(index: HashMap<String, ArrayList<Int>>, docDict: HashMap<String, Int>,
                        queryDict: HashMap<String, Int>, docCount: Int): Double{
        val uniques = findUniques(docDict, queryDict)

        var numerator = 0.0
        var normDoc = 0.0
        var normQuery = 0.0
        for(word in uniques){
            if(!index.containsKey(word)){
                continue
            }
            val idf = log10(docCount.toDouble() / index[word]!!.size)

            val tfIdfDoc =
                when(val count = docDict[word]){
                    null -> 0.0
                    else -> (1 + log10(count.toDouble())) * idf
                }
            val tfIdfQuery =
                when(val count = queryDict[word]){
                    null -> 0.0
                    else -> (1 + log10(count.toDouble())) * idf
                }

            normDoc += tfIdfDoc * tfIdfDoc
            normQuery += tfIdfQuery * tfIdfQuery
            numerator += tfIdfDoc * tfIdfQuery
        }

        normDoc = sqrt(normDoc)
        normQuery = sqrt(normQuery)

        val norm = normDoc * normQuery
        return if(norm == 0.0){
            0.0
        }else{
            numerator / norm
        }
    }

    fun tokenizeString(string: String): HashMap<String, Int>{
        val resultDict = HashMap<String, Int>()

        for(token in string.split(" ")){
            when (val count = resultDict[token])
            {
                null -> resultDict[token] = 1
                else -> resultDict[token] = count + 1
            }
        }
        return resultDict
    }


    fun indexDocs(docs: Array<String> ) : HashMap<String, ArrayList<Int>>{

        val index = HashMap<String, ArrayList<Int>>()

        docs.forEachIndexed { i, doc ->
            val tokens = doc.split(" ")
            for(token in tokens){
                if(token == "je"){
                    print("")
                }
                if(index.containsKey(token)){
                    if(index[token]!!.contains(i)){
                        continue
                    }
                    index[token]?.add(i)
                }else{
                    index[token] = arrayListOf(i)
                }
            }
        }

        return index
    }


}
