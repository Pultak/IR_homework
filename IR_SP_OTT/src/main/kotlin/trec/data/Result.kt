import trec.data.AResult

/**
 * Created by Tigi on 8.1.2015.
 *
 * Třída představuje výsledek vrácený po vyhledávání.
 * Třídu můžete libovolně upravovat, popř. si můžete vytvořit vlastní třídu,
 * která dědí od abstraktní třídy [AbstractResult]
 */
class Result(override val documentID: String?, override val score: Float, override val rank: Int) : AResult(){



}