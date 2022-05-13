package trec.preprocessing

import java.text.Normalizer
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Tokenizer : ITokenizer {

    private const val defaultRegex =
        "((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~|!:,.;]*[-a-zA-Z0-9+&@#/%=~|]|[\\w]+[*][\\w]+)|(\\d+[.,](\\d{2}|\\d[.,]?)?(\\d+)?)|([\\p{L}\\d]+)|(<.*?>)|([\\p{Punct}])|\\d+[.,](\\d+)?"

    private fun tokenize(text: String, regex: String): Array<String>{
        val pattern: Pattern = Pattern.compile(regex)

        val words = ArrayList<String>()

        val matcher: Matcher = pattern.matcher(text)
        while (matcher.find()) {
            val start: Int = matcher.start()
            val end: Int = matcher.end()
            words.add(text.substring(start, end))
        }
        return words.toArray() as Array<String>
    }

    override fun removeAccents(text: String) : String{
        return Normalizer.normalize(text, Normalizer.Form.NFD).replace("\\\\p{InCombiningDiacriticalMarks}+", "")
    }


    override fun tokenize(text: String): Array<String> {
        return tokenize(text, defaultRegex)
    }
}