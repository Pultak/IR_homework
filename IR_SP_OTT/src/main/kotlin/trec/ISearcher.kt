package trec

import trec.data.IResult

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní umožňující vyhledávat v zaindexovaných dokumentech.
 *
 * Pokud potřebujete, můžete přidat další metody k implementaci, ale neměňte signaturu
 * již existující metody search.
 *
 * Metodu search implementujte ve tříde [Index]
 */
interface ISearcher {
    fun search(query: String?): List<IResult?>?
}