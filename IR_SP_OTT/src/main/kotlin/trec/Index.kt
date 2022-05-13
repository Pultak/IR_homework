package cz.zcu.kiv.nlp.ir.trec

import trec.IIndexer
import trec.ISearcher
import trec.data.IDocument
import trec.data.IResult

/**
 * @author tigi
 *
 * Třída reprezentující index.
 *
 * Tuto třídu doplňte tak aby implementovala rozhranní [IIndexer] a [ISearcher].
 * Pokud potřebujete, přidejte další rozhraní, která tato třída implementujte nebo
 * přidejte metody do rozhraní [IIndexer] a [ISearcher].
 */
class Index : IIndexer, ISearcher {
    override fun index(documents: List<IDocument?>?) { //  todo implement
    }

    override fun search(query: String?): List<IResult>? { //  todo implement
        return null
    }
}