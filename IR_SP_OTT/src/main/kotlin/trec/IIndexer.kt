package trec

import trec.data.IDocument

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní, pro indexaci dokumentů.
 *
 * Pokud potřebujete/chcete můžete přidat další metody např. pro indexaci po jednotlivých dokumentech
 * a jiné potřebné metody (např. CRUD operace update, delete, ... dokumentů), ale zachovejte původní metodu.
 *
 * metodu index implementujte ve třídě [Index]
 */
interface IIndexer {
    /**
     * Metoda zaindexuje zadaný seznam dokumentů
     *
     * @param documents list dokumentů
     */
    fun index(documents: List<IDocument?>?)
}