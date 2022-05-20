package trec.preprocessing

import trec.IOUtils
import trec.utils.Logger
import java.io.File
import java.text.Normalizer
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


object Tokenizer : ITokenizer {

    private const val defaultRegex =
        "((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~|!:,.;]*[-a-zA-Z0-9+&@#/%=~|]|[\\w]+[*][\\w]+)|(\\d+[.,](\\d{2}|\\d[.,]?)?(\\d+)?)|([\\p{L}\\d]+)|(<.*?>)|([\\p{Punct}])|\\d+[.,](\\d+)?"

    private val stopWords : ArrayList<String> = arrayListOf()

    init {
        val stopWordsFile = File("stopwords-cs.txt")
        if(!stopWordsFile.exists()){
            Logger.error("Tokenizer -> Missing stopWords file!")
            exitProcess(1)
        }
        stopWordsFile.forEachLine {
            stopWords.add(it.trim())
        }

    }

    private fun tokenize(text: String, regex: String): ArrayList<String>{
        val pattern: Pattern = Pattern.compile(regex)

        val words = ArrayList<String>()

        val matcher: Matcher = pattern.matcher(text)
        while (matcher.find()) {
            val start: Int = matcher.start()
            val end: Int = matcher.end()
            words.add(text.substring(start, end))
        }

        //remove stopWords
        /*todo
        val it = words.iterator()
        while(it.hasNext()){
            val token = it.next()
            if(stopWords.contains(token)){
                it.remove()
            }else if(token.length < 3){
                it.remove()
            }
        }*/

        return words
    }

    override fun removeAccents(text: String) : String{
        return Normalizer.normalize(text, Normalizer.Form.NFD).replace("\\\\p{InCombiningDiacriticalMarks}+", "")
    }


    override fun tokenize(text: String): ArrayList<String> {
        return tokenize(text, defaultRegex)
    }
}