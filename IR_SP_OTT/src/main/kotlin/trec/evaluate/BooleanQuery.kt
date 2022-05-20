package trec.evaluate

import trec.data.IResult
import trec.data.Result
import trec.indexing.DocumentInformation
import trec.indexing.IIndexer
import trec.preprocessing.LightStemmer

class BooleanQuery(private val terms :List<String>) : IQuery {

    private val allPostings = arrayListOf<Pair<String, ArrayList<DocumentInformation>>>()


    override fun evaluate(indexer: IIndexer): ArrayList<IResult> {
        createPostingsList(indexer)

        val iter = allPostings.listIterator()

        var last : ArrayList<DocumentInformation>? = null
        var previous : ArrayList<DocumentInformation>? = null
        while(iter.hasNext()){
            val value = iter.next()

            var merged : ArrayList<DocumentInformation>? = null
            when(value.first){
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
                iter.add(Pair("", merged))
            }
            if(previous != null){
                last = previous
            }
            previous = value.second

        }

        val result = arrayListOf<IResult>()
        allPostings[0].second.forEachIndexed{ i, it ->
            //todo score?
            result.add(Result(it.documentId, /*-1.0f*/0.0f, i + 1))
        }
        return result
    }

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


    private fun booleanANDPostings(firstList: List<DocumentInformation>, secondList: List<DocumentInformation>): ArrayList<DocumentInformation>{
        val res = arrayListOf<DocumentInformation>()
        var lastCheckIndex = 0
        for (i in 0..firstList.size){
            for (j in lastCheckIndex..secondList.size){
                val value = secondList[j]
                if(firstList[i].documentId == value.documentId){
                    lastCheckIndex = j
                    res.add(value)
                }
            }
        }
        return res
    }

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