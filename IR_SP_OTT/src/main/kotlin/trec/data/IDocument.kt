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
    val text: String?

    /**
     * Unikátní id dokumentu
     * @return id dokumentu
     */
    val id: String?

    /**
     * Titulek dokumentu
     * @return titulek dokumentu
     */
    val title: String?

    /**
     * Datum přidání dokumentu (tj. např. indexace nebo stažení nebo publikování
     *
     * @return datum vztažené k dokumentu
     */
    val date: Date?
}