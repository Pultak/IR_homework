package trec.indexing

import trec.ISearcher
import trec.data.IDocument
import trec.preprocessing.LightStemmer
import trec.preprocessing.Tokenizer
import trec.utils.Logger
import java.io.*
import java.lang.Exception
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.sqrt

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


    override var indexInfo: IndexInfo = IndexInfo(hashMapOf(), 0, hashMapOf())

    override fun index(documents: List<IDocument>) {

        Logger.debug("index -> Indexing process started!")
        val docCount = documents.size.toFloat()
        indexInfo.docsSize = documents.size

        val part = ceil(documents.count().toDouble() / 10.0).toInt()
        var percentage = 0
        documents.forEachIndexed{
            i, doc ->

            if(i % part == 0){
                Logger.info("index -> $percentage% indexed!")
                percentage += 10
            }
            val finalString = /*todo"${doc.location} ${doc.name}*/ "${doc.article} ${doc.title}"

            //tokenize strings and find uniques
            val tokens = Tokenizer.tokenize(finalString)/*.distinct() as ArrayList*/
            //todo val stemmedTokens = LightStemmer.stem(tokens)
            for(term in /*stemmedTokens*/ tokens){
                if(indexInfo.index.containsKey(term)){
                    var found = false
                    indexInfo.index[term]!!.forEach{
                        if(it.documentId == doc.id){
                            it.wordCount += 1
                            found = true
                            return@forEach
                        }
                    }
                    if(!found){
                        indexInfo.index[term]?.add(DocumentInformation(doc.id, 1))
                    }
                }else{
                    indexInfo.index[term] = arrayListOf(DocumentInformation(doc.id, 1))
                }
            }
        }

        Logger.debug("index -> Index metrics calculation!")
        indexInfo.index.forEach {
                (term, invertedDocs) ->

            val idf = log10(docCount / invertedDocs.size)

            invertedDocs.forEach{
                it.tfIdfMetric = (1 + log10(it.wordCount.toFloat())) * idf
                if(indexInfo.normsDocs.containsKey(it.documentId)){
                    indexInfo.normsDocs[it.documentId]!!.plus(it.tfIdfMetric * it.tfIdfMetric)
                }else{
                    indexInfo.normsDocs[it.documentId] = it.tfIdfMetric * it.tfIdfMetric
                }
            }
        }


        Logger.debug("index -> Indexing process done!")
    }


    override fun loadIndexedData(file: File): Boolean {
        try {
            Logger.info("Loading ${file.name}!")
            val input = ObjectInputStream(FileInputStream(file))
            indexInfo = input.readObject() as IndexInfo

            input.close()
            Logger.info("Loading of indexed data from ${file.name} is done!")
        }catch(e: Exception){
            Logger.error("Loading of indexed data from ${file.name} failed due to ${e.cause}!")
            return false
        }
        return true
    }

    override fun saveIndexedData(file: File): Boolean {
        try {
            Logger.info("Saving index to ${file.name}!")
            val out = ObjectOutputStream(FileOutputStream(file))
            out.writeObject(indexInfo)
            out.flush()
            out.close()
            Logger.info("Saving of indexed data into ${file.name} is done!")
        }catch(e: Exception){
            Logger.error("Saving of indexed data into ${file.name} failed due to ${e.cause}!")
            return false
        }

        return true
    }
}