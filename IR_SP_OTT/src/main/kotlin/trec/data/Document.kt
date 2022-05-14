package trec.data

import java.util.*

class Document(override val text: String,
               override val title: String,
               override val date: Date,
               override var url: String?,
               override var price: String?,
               override var seenCount: String?,
               override var location: String?,
               override var name: String?
) : IDocument, java.io.Serializable {

    override val id: Long = objectCount++

    override fun toString(): String {
        return "DocumentNew{" +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                "text='" + text + '\'' +
                '}'
    }

    companion object {
        const val serialVersionUID = -5097715898427114007L
        var objectCount : Long = 0
    }
}