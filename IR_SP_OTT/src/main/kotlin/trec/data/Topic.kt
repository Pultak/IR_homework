package trec.data

import java.io.Serializable

/**
 * Created by Tigi on 8.1.2015.
 *
 * Tuto třídu neměnte, pro vypracování semestrální práce není potřeba.
 *
 * Třída Topic představuje dotaz pro předpřipravené dokumenty.
 * Obsahuje tři textová pole narrative, description a title, která můžete použít ve vašem dotazu.
 */
class Topic (var narrative: String, var description: String, var id: String, var title: String, var lang: String) : Serializable {

    override fun toString(): String {
        return "Topic{" +
                "narrative='" + narrative + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", lang='" + lang + '\'' +
                '}'
    }
}