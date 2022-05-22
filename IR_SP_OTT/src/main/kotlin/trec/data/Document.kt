package trec.data

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
}