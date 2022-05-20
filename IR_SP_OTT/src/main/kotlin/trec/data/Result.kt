package trec.data

/**
 * Created by Tigi on 8.1.2015.
 *
 * Třída představuje výsledek vrácený po vyhledávání.
 * Třídu můžete libovolně upravovat, popř. si můžete vytvořit vlastní třídu,
 * která dědí od abstraktní třídy
 */
class Result(override var documentID: String, override var score: Float = -1.0f, override var rank: Int = -1) : AResult()