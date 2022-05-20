package trec.preprocessing

interface IStemmer {

    fun stem(text: String): String
    fun stem(text: ArrayList<String>): ArrayList<String>
}