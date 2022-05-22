package trec.data

/**
 * Created by Tigi on 6.1.2015.
 *
 * Třída [AResult] implementuje rozhraní [IResult]
 *
 * Představuje výsledek pro ohodnocené vyhledávání. Tzn. po zadání dotazu vyhledávač vrátí
 * "List<trec.data.Result>", kde každý objekt [IResult] reprezentuje jeden dokument a jeho relevanci k zadanému dotazu.
 * => tj. id dokumentu, skóre podobnosti mezi tímto dokumentem a dotazem (např. kosinova podobnost), a rank tj.
 * pořadí mezi ostatními vrácenými dokumenty (dokument s rankem 1 bude dokument, který je nejrelevantnější k dodtazu)
 *
 * Od této třídy byste měli dědit pokud vám nestačí implementace třídy např. pokud potřebujete
 * přidat nějaké další proměnné.
 *
 * Metodu toString(String topic) neměnte, ani nepřepisujte v odděděných třídách slouží pro generování výstupu
 * v daném formátu pro vyhodnocovací skript.
 *
</trec.data.Result> */
abstract class AResult : IResult {

    override fun toString(): String {
        return "trec.data.Result{" +
                "documentID='" + documentID + '\'' +
                ", rank=" + rank +
                ", score=" + score +
                '}'
    }

    /**
     * Metoda používaná pro generování výstupu pro vyhodnocovací skript.
     * Metodu nepřepisujte (v potomcích) ani neupravujte
     */
    override fun toString(topic: String): String {
        return "$topic Q0 $documentID $rank $score runindex1"
    }
}