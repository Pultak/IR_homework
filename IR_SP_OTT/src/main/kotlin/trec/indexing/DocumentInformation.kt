package trec.indexing

import java.io.Serializable

class DocumentInformation(val documentId: String, var wordCount: Int = 0, var tfIdfMetric: Float = -1.0f) : Serializable