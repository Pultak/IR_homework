package trec.indexing

import trec.ISearcher
import trec.data.IDocument
import trec.data.IResult
import trec.preprocessing.Tokenizer
import java.io.File

/**
 * @author tigi
 *
 * Třída reprezentující index.
 *
 * Tuto třídu doplňte tak aby implementovala rozhranní [IIndexer] a [ISearcher].
 * Pokud potřebujete, přidejte další rozhraní, která tato třída implementujte nebo
 * přidejte metody do rozhraní [IIndexer] a [ISearcher].
 */
class Indexer : IIndexer{



    private val index = hashMapOf<String, ArrayList<Int>>()


    override fun index(documents: List<IDocument>) {

        documents.forEachIndexed{
            i, doc ->

            val finalString = "${doc.location} ${doc.name} ${doc.text} ${doc.title}"

            val tokens = Tokenizer.tokenize(finalString)
            for(token in tokens){
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
    }

    override fun loadIndexedData(file: File): Boolean {

    }

    override fun saveIndexedData(file: File): Boolean {

    }
}