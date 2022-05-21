package trec.evaluate

import trec.data.IResult
import trec.indexing.IIndexer

interface IQuery {

    val terms: ArrayList<String>

    fun evaluate(indexer: IIndexer): ArrayList<IResult>

}