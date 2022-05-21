package trec.evaluate

import trec.data.IResult
import trec.data.Result
import trec.indexing.DocumentInformation
import trec.indexing.IIndexer
import trec.preprocessing.LightStemmer

class BooleanQuery(override val terms :ArrayList<String>) : IQuery {

    private val allPostings = arrayListOf<Pair<String, ArrayList<DocumentInformation>>>()


/**
*	
*	@param indexer currently used indexer
* 	@param returns all documents that were relevant for inserted query
*
*/
    override fun evaluate(indexer: IIndexer): ArrayList<IResult> {
        createPostingsList(indexer)

        val iter = allPostings.listIterator()

		//first of two elements that will be joined  
		//todo change to first 
        var last : ArrayList<DocumentInformation>? = null
        //second of two elements that will be joined
        //todo change to second
        var previous : ArrayList<DocumentInformation>? = null
        while(iter.hasNext()){
            val value = iter.next()

            var merged : ArrayList<DocumentInformation>? = null
            when(value.first){
            	//is actual term one of the operations?
                "and" -> {
                    merged = booleanANDPostings(previous!!, last!!)
                }
                "or" -> {
                    merged = booleanORPostings(previous!!, last!!)
                }
                "not" -> {
                    merged = booleanNOTPostings(previous!!, last!!)
                }
            }
            if(merged != null){
                //remove last 3 objects
                iter.remove()
                iter.previous()
                iter.remove()
                iter.previous()
                iter.remove()
                //todo test
                //and create one joined from the remove postings
                iter.add(Pair("", merged))
            }
            if(previous != null){
                last = previous
            }
            previous = value.second

        }

        val result = arrayListOf<IResult>()
        //asign ranks
        allPostings[0].second.forEachIndexed{ i, it ->
            //todo score?
            result.add(Result(it.documentId, /*-1.0f*/0.0f, i + 1))
        }
        return result
    }


/**
*
**/
    private fun createPostingsList(indexer: IIndexer){
        terms.forEach { term ->
            when(term){
                "or", "and", "not" -> {
                    allPostings.add(Pair(term, arrayListOf()))
                }
                else -> {
                    val stem = LightStemmer.stem(term)
                    val res = indexer.indexInfo.index[stem]
                    if(res != null){
                        allPostings.add(Pair(stem, res))
                    }
                }
            }

        }
    }


/**
*	Joins the postings of the passed lists by AND operation which results intersection od the two passed postings
* 	@param firstList list of all document information of the first term
*	@param secondList list of all documents of the second term
*	@returns Joined postings with AND operation
*
*/
    private fun booleanANDPostings(firstList: List<DocumentInformation>, secondList: List<DocumentInformation>): ArrayList<DocumentInformation>{
        val res = arrayListOf<DocumentInformation>()
        var lastCheckIndex = 0
        //for (i in 0 until firstList.size){
        for (element in firstList){
            for (j in lastCheckIndex until secondList.size){
                val value = secondList[j]
                if(element.documentId == value.documentId){
                    lastCheckIndex = j
                    res.add(value)
                }
            }
        }
        return res
    }

/**
*	Joins the postings of the passed lists by OR operation which results unifications od the two passed postings
* 	@param firstList list of all document information of the first term
*	@param secondList list of all documents of the second term
*	@returns Joined postings with OR operation
*
*/
    private fun booleanORPostings(firstList: List<DocumentInformation>, secondList: List<DocumentInformation>): ArrayList<DocumentInformation>{
        val res = arrayListOf<DocumentInformation>()
        var lastCheckIndex = 0

        for (i in 0..firstList.size){
            val firstVal = firstList[i]
            for(j in lastCheckIndex..secondList.size){
                val secondVal = secondList[i]
                if(firstList[i].documentId == secondVal.documentId){
                    lastCheckIndex = j
                    break
                }else{
                    res.add(secondVal)
                }
            }
            res.add(firstVal)
        }
        return res
    }


/**
*	Joins the postings of the passed lists by NOT operation which results in first postings but without the data from the second postings. 
* 	@param firstList list of all document information of the first term
*	@param secondList list of all documents of the second term
*	@returns Joined postings with NOT operation
*
*/
    private fun booleanNOTPostings(firstList: ArrayList<DocumentInformation>, secondList: ArrayList<DocumentInformation>): ArrayList<DocumentInformation>{
        //todo does it work?
        val res = firstList.filter {
            secondList.any {
                    sec -> sec.documentId == it.documentId
            }
        } as ArrayList<DocumentInformation>

        return res
    }
}
