package trec.indexing

import java.io.Serializable

data class IndexInfo(var index: HashMap<String, ArrayList<DocumentInformation>>,
        var docsSize: Int, var normsDocs: HashMap<String, Float>) : Serializable