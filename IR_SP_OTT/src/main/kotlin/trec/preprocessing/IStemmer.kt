package trec.preprocessing

interface IStemmer {

    fun stem(text: String): String
}