package trec.evaluate

import trec.data.IResult
import trec.indexing.IIndexer

interface IQuery {

    fun evaluate(indexer: IIndexer): ArrayList<IResult>

}