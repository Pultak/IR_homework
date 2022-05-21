package trec.evaluate

import trec.data.IResult
import trec.data.Result
import trec.indexing.DocumentInformation
import trec.indexing.IIndexer
import trec.indexing.Indexer
import kotlin.math.log10
import kotlin.math.sqrt

class NormalQuery(override val terms: ArrayList<String>) : IQuery {

	/**
	*Map of all unique terms with its count
	*/
    private val queryDict = hashMapOf<String, Int>()

    init {
    //creates all the unique terms in the query
        terms.forEach{term ->
        	//is this term already in the map?
            if(queryDict.containsKey(term)){
                queryDict[term]!!.plus(1)
            }else{
                queryDict[term] = 1
            }
        }
    }

    override fun evaluate(indexer: IIndexer): ArrayList<IResult> {
        val res = arrayListOf<IResult>()
        //map of all related docs found in the term postings 
        val docDicts = hashMapOf<String, HashMap<String, Pair<Float, DocumentInformation>>>()

        for((term, count) in queryDict){
            val postings = indexer.indexInfo.index[term]
            if(postings != null){
                for (docInfo in postings) {
                	//is this doc not in the doc map?
                    if(!docDicts.containsKey(docInfo.documentId)){
                        docDicts[docInfo.documentId] = hashMapOf()
                    }
                    docDicts[docInfo.documentId]!![term] = Pair(count.toFloat(), docInfo)
                }
            }else{
				//todo useless branch?
            }
        }


		//for every related doc -> get some metrics
        for((docId, uniques) in docDicts){
            val tfIdfResult = evaluateDoc(indexer, docId, uniques)
            res.add(Result(docId, tfIdfResult))
        }

        res.sortByDescending { it.score }
        //assign ranks to each doc
        res.forEachIndexed { i, actualRes ->
            actualRes.rank = i + 1
        }
        return res
    }


/**
*	Method calculates the metrics for the passed document. 
*	TDIDF metric is used. 
*	@param indexer actually used indexer
*	@param docId id of the currectly evaluated document
*	@param wordDict map od all unique terms located in the doc and query
* 	@returns tfidf metric
*/
    private fun evaluateDoc(indexer: IIndexer, docId: String,
                            wordDict: HashMap<String, Pair<Float, DocumentInformation>>
    ): Float{
        var numerator = 0.0f
        var normQuery = 0.0f

		//for every unique term in the query
        queryDict.forEach { (term, queryCount) ->

			//get all the unique words of the doc and query
            val uniques = wordDict[term]
            //get doc idf if present
            val docTfIdf: Float = uniques?.second?.tfIdfMetric ?: 0.0f

			//get list of docs containing this term
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

		//normalize the results
        val normDoc = sqrt(indexer.indexInfo.normsDocs[docId]!!)
        normQuery = sqrt(normQuery)
        val norm = normDoc * normQuery
        
        //return result
        return if(norm == 0.0f){
            0.0f
        }else{
            numerator / norm
        }
    }




}
