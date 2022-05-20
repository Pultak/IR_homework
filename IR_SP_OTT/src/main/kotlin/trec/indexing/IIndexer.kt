package trec.indexing

import trec.data.IDocument
import java.io.File

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní, pro indexaci dokumentů.
 *
 * Pokud potřebujete/chcete můžete přidat další metody např. pro indexaci po jednotlivých dokumentech
 * a jiné potřebné metody (např. CRUD operace update, delete, ... dokumentů), ale zachovejte původní metodu.
 *
 * metodu index implementujte ve třídě [Indexer]
 */
interface IIndexer {

    var indexInfo: IndexInfo

    /**
     * Metoda zaindexuje zadaný seznam dokumentů
     *
     * @param documents list dokumentů
     */
    fun index(documents: List<IDocument>)



    /**
     * Loads indexed data from specified file.
     * @param file - file with created indexem.
     * @return True if loading was succesfull.
     */
    fun loadIndexedData(file: File): Boolean

    /**
     * Saves our index to a specified file.
     * @param file - file with to which we save our index.
     * @return True if the index save was successful.
     */
    fun saveIndexedData(file: File): Boolean
}