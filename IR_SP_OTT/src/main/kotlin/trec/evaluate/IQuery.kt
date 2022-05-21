package trec.evaluate

import trec.data.IResult
import trec.indexing.IIndexer

interface IQuery {

/**
* List of all parsed terms that were passed during the query init
*/
    val terms: ArrayList<String>

/**
*	Evaluates this query with passed indexer
*	@param indexer passed indexer containing index and other related attributes
*	@returns List of hits for the passed indexer
*/
    fun evaluate(indexer: IIndexer): ArrayList<IResult>

}
