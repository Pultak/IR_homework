package cz.zcu.kiv.nlp.ir.trec.data

import trec.data.IDocument
import java.util.*

class Document(override val text: String?, override val id: String?, override val title: String?, override val date: Date?) : IDocument, java.io.Serializable {
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
    }
}