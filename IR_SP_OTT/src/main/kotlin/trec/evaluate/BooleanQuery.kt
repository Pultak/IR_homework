package trec.evaluate

import trec.data.IResult
import trec.data.Result
import trec.indexing.DocumentInformation
import trec.indexing.IIndexer
import trec.preprocessing.AggresiveStemmer

class BooleanQuery(override val terms :ArrayList<String>) : IQuery {

    private val allPostings = arrayListOf<Pair<String, ArrayList<DocumentInformation>>>()


/**
*	
*	@param indexer currently used indexer
* 	@returns all documents that were relevant for inserted query
*
*/
    override fun evaluate(indexer: IIndexer): ArrayList<IResult> {
        createPostingsList(indexer)

        val iter = allPostings.listIterator()

        while(iter.hasNext()){
            val value = iter.next()

            var merged : ArrayList<DocumentInformation>? = null
            when(value.first){
            	//is actual term one of the operations?
                "and" -> {
                    merged = booleanANDPostings(getTwoPreviousLists(iter))
                }
                "or" -> {
                    merged = booleanORPostings(getTwoPreviousLists(iter))
                }
                "not" -> {
                    merged = booleanNOTPostings(getTwoPreviousLists(iter))
                }
            }
            if(merged != null){
                //remove last 3 objects
                iter.remove()
                iter.next()
                iter.remove()
                iter.next()
                iter.remove()
                //and create one joined from the remove postings
                iter.add(Pair("", merged))
            }

        }

        val result = arrayListOf<IResult>()
        //asign ranks
        allPostings[0].second.forEachIndexed{ i, it ->
            result.add(Result(it.documentId, /*-1.0f*/0.0f, i + 1))
        }
        return result
    }


    private fun getTwoPreviousLists(it: MutableListIterator<Pair<String, ArrayList<DocumentInformation>>>):
            Pair<List<DocumentInformation>, List<DocumentInformation>>{
        //first discard the operation operator
        it.previous()
        //then get last two postings
        val list2 = it.previous()
        val list1 = it.previous()
        return Pair(list1.second, list2.second)
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
                    val stem = AggresiveStemmer.stem(term)
                    //empty array if not inside index
                    val res = indexer.indexInfo.index[stem] ?: arrayListOf()
                    allPostings.add(Pair(stem, res))
                }
            }

        }
    }


/**
*	Joins the postings of the passed lists by AND operation which results intersection od the two passed postings
* 	@param postings list of all document information of the first term + t list of all documents of the second term
*	@returns Joined postings with AND operation
*
*/
    private fun booleanANDPostings(postings: Pair<List<DocumentInformation>, List<DocumentInformation>>): ArrayList<DocumentInformation>{
        val res = arrayListOf<DocumentInformation>()
        var lastCheckIndex = 0
        //for (i in 0 until firstList.size){
        for (element in postings.first){
            for (j in lastCheckIndex until postings.second.size){
                val value = postings.second[j]
                if(element.documentId == value.documentId){
                    res.add(value)
                    lastCheckIndex = j + 1
                }
            }
        }
        return res
    }

/**
*	Joins the postings of the passed lists by OR operation which results unifications od the two passed postings
* 	@param postings list of all document information of the first term + secondList list of all documents of the second term
*	@returns Joined postings with OR operation
*
*/
    private fun booleanORPostings(postings: Pair<List<DocumentInformation>, List<DocumentInformation>>): ArrayList<DocumentInformation>{
        val res = arrayListOf<DocumentInformation>()
        var lastCheckIndex = 0

        for (i in postings.first.indices){
            val firstVal = postings.first[i]
            if(lastCheckIndex < postings.second.size){
                for(j in lastCheckIndex until postings.second.size){
                    lastCheckIndex = j
                    val secondVal = postings.second[j]
                    if(firstVal.documentId == secondVal.documentId){
                        ++lastCheckIndex
                        break
                    }else{
                        res.add(secondVal)
                    }
                }
            }
            res.add(firstVal)
        }
        return res
    }


/**
*	Joins the postings of the passed lists by NOT operation which results in first postings but without the data from the second postings. 
* 	@param postings list of all document information of the first term + secondList list of all documents of the second term
*	@returns Joined postings with NOT operation
*
*/
    private fun booleanNOTPostings(postings: Pair<List<DocumentInformation>, List<DocumentInformation>>):
        ArrayList<DocumentInformation>{
        val result = ArrayList(postings.first)
        val it = result.iterator()
        var lastIndex = 0
        while(it.hasNext()){
            val actual = it.next()
            for(i in lastIndex until postings.second.size){
                if(actual.documentId == postings.second[i].documentId){
                    lastIndex = i + 1
                    it.remove()
                    break
                }
            }
        }
        return result
    }
}
