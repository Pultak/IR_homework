package trec.data

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní představuje výsledek pro ohodnocené vyhledávání. Tzn. po zadání dotazu vyhledávač vrátí
 * "List<trec.data.Result>", kde každý objekt [IResult] reprezentuje jeden dokument a jeho relevanci k zadanému dotazu.
 * => tj. id dokumentu, skóre podobnosti mezi tímto dokumentem a dotazem (např. kosinova podobnost) a rank tj.
 * pořadí mezi ostatními vrácenými dokumenty (dokument s rankem 1 bude dokument, který je nejrelevantnější k dodtazu)
 *
 * Toto rozhranní neimplementujte, ale použijte třídu [ResultImpl], kterou můžete libovolně upravovat, případně
 * si vytvořte vlastní třídu, která dědí od abstraktní třídy [AResult].
</trec.data.Result> */
interface IResult {
    /**
     * Vrátí id dokumentu
     * @return id dokumentu
     */
    val documentID: String
    /**
     * Vrátí skóre podobnosti mezi dokumentem a dotazem
     * např. kosinova podobnost
     *
     * @return skóre podobnosti mezi dokumentem a dotazem
     */
    var score: Float

    /**
     * Pořadí mezi ostatními vrácenými dokumenty
     * Výsledek s rank 1 je nejrelevantnější dokument k zadanému dotazu
     *
     * @return pořadí mezi ostatními vrácenými dokumenty
     */
    var rank: Int

    /**
     * Metoda používaná pro generování výstupu pro vyhodnocovací skript.
     * Metodu nepřepisujte (v potomcích) ani neupravujte
     */
    fun toString(topic: String): String
}