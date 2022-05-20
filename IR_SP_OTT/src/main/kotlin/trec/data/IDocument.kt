package trec.data

import java.util.*

/**
 * Created by Tigi on 8.1.2015.
 *
 * Rozhraní reprezentuje dokument, který je možné indexovat a vyhledávat.
 *
 * Implementujte toto rozhranní.
 *
 * Pokud potřebujete můžete do rozhranní přidat metody, ale signaturu stávajících metod neměnte.
 *
 */
interface IDocument {
    /**
     * Text dokumentu
     * @return text
     */
    val article: String

    /**
     * Unikátní id dokumentu
     * @return id dokumentu
     */
    var id: String
/*
    var url: String?

    var price: String?
    var seenCount: String?
    var location: String?
    var name: String?
*/
    /**
     * Titulek dokumentu
     * @return titulek dokumentu
     */
    val title: String

    /**
     * Datum přidání dokumentu (tj. např. indexace nebo stažení nebo publikování
     *
     * @return datum vztažené k dokumentu
     */
    val datetime: String
}