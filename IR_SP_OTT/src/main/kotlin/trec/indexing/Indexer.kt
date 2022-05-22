package trec.indexing

import trec.IOUtils
import trec.data.IDocument
import trec.preprocessing.AggresiveStemmer
import trec.preprocessing.Tokenizer
import trec.utils.Logger
import java.io.*
import java.lang.Exception
import kotlin.math.ceil
import kotlin.math.log10

/**
 * @author tigi
 *
 * Třída reprezentující index.
 *
 * Tuto třídu doplňte tak aby implementovala rozhranní [IIndexer].
 * Pokud potřebujete, přidejte další rozhraní, která tato třída implementujte nebo
 * přidejte metody do rozhraní [IIndexer].
 */
class Indexer : IIndexer{


    override var indexInfo: IndexInfo = IndexInfo(hashMapOf(), 0, hashMapOf())

    override fun index(documents: List<IDocument>) {

        Logger.debug("index -> Indexing process started!")
        val startTime = System.currentTimeMillis()
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
            indexData(doc)
        }

        Logger.debug("index -> Index metrics calculation!")
        indexMetricsCalc(docCount.toDouble())

        val endTime = System.currentTimeMillis()
        Logger.debug("index -> Indexing process done in ${(endTime - startTime)/1000.0} secs!")
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

    override fun addDoc(file: File): Boolean{

        if(!file.exists()){
            Logger.error("addDoc -> Adding of ${file.path} doc failed! File non existent!")
            return false
        }

        val doc = IOUtils.readDocument(file)

        if(doc != null){

            Logger.debug("addDoc -> Indexing of ${file.path} doc started!")
            ++(indexInfo.docsSize)
            indexInfo.normsDocs
            indexData(doc)

            Logger.debug("addDoc -> Index metrics calculation!")
            indexMetricsCalc(indexInfo.docsSize.toDouble())
        }
        return true
    }


    private fun indexData(doc: IDocument){
        val finalString = "${doc.article} ${doc.title}"
        //tokenize strings and find uniques
        val tokens = Tokenizer.tokenize(finalString)/*.distinct() as ArrayList*/
        val stemmedTokens = AggresiveStemmer.stem(tokens)
        for(term in stemmedTokens){
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


    private fun indexMetricsCalc(docsCount: Double){
        indexInfo.index.forEach {
                (_, invertedDocs) ->

            val idf = log10(docsCount / invertedDocs.size.toDouble())

            invertedDocs.forEach{
                it.tfIdfMetric = ((1 + log10(it.wordCount.toFloat())) * idf).toFloat()
                if(indexInfo.normsDocs.containsKey(it.documentId)){
                    indexInfo.normsDocs[it.documentId] = indexInfo.normsDocs[it.documentId]!!.plus(it.tfIdfMetric * it.tfIdfMetric)
                }else{
                    indexInfo.normsDocs[it.documentId] = it.tfIdfMetric * it.tfIdfMetric
                }
            }
        }
    }

}