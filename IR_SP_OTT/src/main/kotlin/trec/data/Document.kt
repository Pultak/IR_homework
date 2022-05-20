package trec.data

import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class Document(
    override val article: String,
    override val datetime: String,
    override val title: String
    /*@Optional override var url: String? = null,
               override var price: String? = null,
               override var seenCount: String? = null,
               override var location: String? = null,
               override var name: String? = null*/
) : IDocument/*, Serializable*/ {

    override lateinit var id: String




    override fun toString(): String {
        return "DocumentNew{" +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", date=" + datetime +
                "text='" + article + '\'' +
                '}'
    }

    companion object {
        const val serialVersionUID = -5097715898427114007L
        var objectCount : Int = 0
    }
}