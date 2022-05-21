package trec.evaluate

import trec.data.IResult
import trec.data.Result
import trec.indexing.DocumentInformation
import trec.indexing.IIndexer
import trec.indexing.Indexer
import kotlin.math.log10
import kotlin.math.sqrt

class NormalQuery(override val terms: ArrayList<String>) : IQuery {

    private val queryDict = hashMapOf<String, Int>()

    init {
        terms.forEach{term ->
            if(queryDict.containsKey(term)){
                queryDict[term]!!.plus(1)
            }else{
                queryDict[term] = 1
            }
        }
    }

    override fun evaluate(indexer: IIndexer): ArrayList<IResult> {
        val res = arrayListOf<IResult>()
        val docDicts = hashMapOf<String, HashMap<String, Pair<Float, DocumentInformation>>>()

        for((term, count) in queryDict){
            val postings = indexer.indexInfo.index[term]
            if(postings != null){
                for (docInfo in postings) {
                    if(!docDicts.containsKey(docInfo.documentId)){
                        docDicts[docInfo.documentId] = hashMapOf()
                    }
                    docDicts[docInfo.documentId]!![term] = Pair(count.toFloat(), docInfo)
                }
            }else{

            }
        }

        for((docId, uniques) in docDicts){
            val tfIdfResult = evaluateDoc(indexer, docId, uniques)
            res.add(Result(docId, tfIdfResult))
        }

        res.sortByDescending { it.score }
        res.forEachIndexed { i, actualRes ->
            actualRes.rank = i + 1
        }
        return res
    }


    private fun evaluateDoc(indexer: IIndexer, docId: String,
                            wordDict: HashMap<String, Pair<Float, DocumentInformation>>
    ): Float{
        var numerator = 0.0f
        var normQuery = 0.0f

        queryDict.forEach { (term, queryCount) ->

            val uniques = wordDict[term]
            //var queryCount = 0.0f
            //get doc idf if present
            val docTfIdf: Float = uniques?.second?.tfIdfMetric ?: 0.0f

            val termPostings = indexer.indexInfo.index[term]
            val idf = if(termPostings == null) 0.0f else
             log10(indexer.indexInfo.docsSize / termPostings.size.toDouble()).toFloat()

            val tfIdfQuery =
                when(queryCount){
                    0 -> 0.0f
                    else -> (1.0f + log10(queryCount.toFloat())) * idf
                }

            normQuery += tfIdfQuery * tfIdfQuery
            numerator += docTfIdf * tfIdfQuery

        }

        val normDoc = sqrt(indexer.indexInfo.normsDocs[docId]!!)
        normQuery = sqrt(normQuery)

        val norm = normDoc * normQuery
        return if(norm == 0.0f){
            0.0f
        }else{
            numerator / norm
        }
    }




}