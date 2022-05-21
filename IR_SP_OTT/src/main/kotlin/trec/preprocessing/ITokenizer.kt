package trec.preprocessing

import java.util.ArrayList

interface ITokenizer {

    fun removeAccents(text: String): String
    fun tokenize(text: String, removeStopWords: Boolean = true): ArrayList<String>
}