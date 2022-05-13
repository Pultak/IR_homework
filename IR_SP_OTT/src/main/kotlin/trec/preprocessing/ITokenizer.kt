package trec.preprocessing

interface ITokenizer {

    fun removeAccents(text: String): String
    fun tokenize(text: String): Array<String>
}