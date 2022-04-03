import kotlin.math.log10
import kotlin.math.sqrt

fun findUniques(dict1: HashMap<String, Int>, dict2: HashMap<String, Int>): List<String>{
    val uniques = arrayListOf<String>()
    uniques.addAll(dict1.keys)
    uniques.addAll(dict2.keys)
    return uniques.distinct()
}


fun evaluateQueries(index: HashMap<String, ArrayList<Int>>, docDict: HashMap<String, Int>,
                    queryDict: HashMap<String, Int>, docCount: Int): Double{
    val uniques = findUniques(docDict, queryDict)

    var numerator = 0.0
    var normDoc = 0.0
    var normQuery = 0.0
    for(word in uniques){
        if(!index.containsKey(word)){
            continue
        }
        val idf = log10(docCount.toDouble() / index[word]!!.size)

        val tfIdfDoc =
            when(val count = docDict[word]){
                null -> 0.0
                else -> (1 + log10(count.toDouble())) * idf
            }
        val tfIdfQuery =
            when(val count = queryDict[word]){
                null -> 0.0
                else -> (1 + log10(count.toDouble())) * idf
            }

        normDoc += tfIdfDoc * tfIdfDoc
        normQuery += tfIdfQuery * tfIdfQuery
        numerator += tfIdfDoc * tfIdfQuery
    }

    normDoc = sqrt(normDoc)
    normQuery = sqrt(normQuery)

    val norm = normDoc * normQuery
    return if(norm == 0.0){
        0.0
    }else{
        numerator / norm
    }
}

fun tokenizeString(string: String): HashMap<String, Int>{
    val resultDict = HashMap<String, Int>()

    for(token in string.split(" ")){
        when (val count = resultDict[token])
        {
            null -> resultDict[token] = 1
            else -> resultDict[token] = count + 1
        }
    }
    return resultDict
}


fun indexDocs(docs: Array<String> ) : HashMap<String, ArrayList<Int>>{

    val index = HashMap<String, ArrayList<Int>>()

    docs.forEachIndexed { i, doc ->
        val tokens = doc.split(" ")
        for(token in tokens){
            if(token == "je"){
                print("")
            }
            if(index.containsKey(token)){
                if(index[token]!!.contains(i)){
                    continue
                }
                index[token]?.add(i)
            }else{
                index[token] = arrayListOf(i)
            }
        }
    }

    return index
}


fun main(args: Array<String>) {
    /*
    //documents and query from the first part of this assignment
    val docs = arrayOf(
        "Plzeň je krásné město a je to krásné místo",
        "Ostrava je ošklivé místo",
        "Praha je také krásné město Plzeň je hezčí")
    val q = arrayOf(
        "krásné město")

    val queries = arrayListOf<HashMap<String, Int>>()
    for(query in q){
        queries.add(tokenizeString(query))
    }
    */
    //documents and queries from the second part of this assignment
    val docs = arrayOf(
        "tropical fish include fish found in tropical environments",
        "fish live in a sea",
        "tropical fish are popular aquarium fish",
        "fish also live in Czechia",
        "Czechia is a country")

    val q = arrayOf(
        "tropical fish sea",
        "tropical fish")

    val queries = arrayListOf<HashMap<String, Int>>()
    for(query in q){
        queries.add(tokenizeString(query))
    }

    val index = indexDocs(docs);


    val results1 = HashMap<String, Double>()

    docs.forEachIndexed { i, doc ->
        queries.forEachIndexed { j, query ->
            val result = evaluateQueries(index, tokenizeString(doc), query, docs.size)
            results1["Dokument$i: $doc; Query$j: ${query.keys}"] = result
        }
    }

    results1.toSortedMap().forEach{ it ->
        println(it)
    }
}