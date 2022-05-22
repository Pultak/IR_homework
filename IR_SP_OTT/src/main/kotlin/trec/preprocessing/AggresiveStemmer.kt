package trec.preprocessing

/***
 * Aggressive stemmer
 */
object AggresiveStemmer : IStemmer {
    /**
     * A buffer of the current word being stemmed
     */
    private val sb = StringBuffer()

    fun stemWithMap(text: ArrayList<String>): HashMap<String, ArrayList<String>>{
        val result = hashMapOf<String, ArrayList<String>>()
        for(i in 0 until text.size){
            val token = text[i]
            val stemResult = AggresiveStemmer.stem(text[i])
            if(result.containsKey(stemResult)){
                val tokenList = result[stemResult]!!
                if(!tokenList.contains(token)){
                    tokenList.add(token)
                }
            }else{
                result[stemResult] = arrayListOf(token)
            }
        }

        return result
    }

    override fun stem(text: ArrayList<String>): ArrayList<String> { //
        for(i in 0 until text.size){
            text[i] = stem(text[i])
        }
        return text
    }
    override fun stem(text: String): String { //
        var input = text
        input = input.toLowerCase()
        //reset string buffer
        sb.delete(0, sb.length)
        sb.insert(0, input)
        // stemming...
        //removes case endings from nouns and adjectives
        removeCase(sb)
        //removes possesive endings from names -ov- and -in-
        removePossessives(sb)
        //removes comparative endings
        removeComparative(sb)
        //removes diminutive endings
        removeDiminutive(sb)
        //removes augmentatives endings
        removeAugmentative(sb)
        //removes derivational sufixes from nouns
        removeDerivational(sb)

        var sbResult = sb.toString()
        sbResult = sbResult.replace('í', 'i')
        sbResult = sbResult.replace('á', 'a')
        sbResult = sbResult.replace('é', 'e')
        sbResult = sbResult.replace('ý', 'y')
        sbResult = sbResult.replace('ě', 'e')
        sbResult = sbResult.replace('ú', 'u')
        sbResult = sbResult.replace('ů', 'u')
        sbResult = sbResult.replace('š', 's')
        sbResult = sbResult.replace('č', 'c')
        sbResult = sbResult.replace('ř', 'r')
        sbResult = sbResult.replace('ž', 'z')
        return sbResult
    }

    private fun removeDerivational(buffer: StringBuffer) {
        val len = buffer.length
        //
        if (len > 8 && buffer.substring(len - 6, len) == "obinec") {
            buffer.delete(len - 6, len)
            return
        } //len >8
        if (len > 7) {
            if (buffer.substring(len - 5, len) == "ion\u00e1\u0159") { // -ionář
                buffer.delete(len - 4, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 5, len) == "ovisk" || buffer.substring(
                    len - 5,
                    len
                ) == "ovstv" || buffer.substring(len - 5, len) == "ovi\u0161t" || buffer.substring(
                    len - 5,
                    len
                ) == "ovn\u00edk"
            ) { //-ovník
                buffer.delete(len - 5, len)
                return
            }
        } //len>7
        if (len > 6) {
            if (buffer.substring(len - 4, len) == "\u00e1sek" || buffer.substring(
                    len - 4,
                    len
                ) == "loun" || buffer.substring(len - 4, len) == "nost" || buffer.substring(
                    len - 4,
                    len
                ) == "teln" || buffer.substring(len - 4, len) == "ovec" || buffer.substring(
                    len - 5,
                    len
                ) == "ov\u00edk" || buffer.substring(len - 4, len) == "ovtv" || buffer.substring(
                    len - 4,
                    len
                ) == "ovin" || buffer.substring(len - 4, len) == "\u0161tin"
            ) { //-štin
                buffer.delete(len - 4, len)
                return
            }
            if (buffer.substring(len - 4, len) == "enic" || buffer.substring(
                    len - 4,
                    len
                ) == "inec" || buffer.substring(len - 4, len) == "itel"
            ) {
                buffer.delete(len - 3, len)
                palatalise(buffer)
                return
            }
        } //len>6
        if (len > 5) {
            if (buffer.substring(len - 3, len) == "\u00e1rn") { //-árn
                buffer.delete(len - 3, len)
                return
            }
            if (buffer.substring(len - 3, len) == "\u011bnk") { //-ěnk
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 3, len) == "i\u00e1n" || buffer.substring(
                    len - 3,
                    len
                ) == "ist" || buffer.substring(len - 3, len) == "isk" || buffer.substring(
                    len - 3,
                    len
                ) == "i\u0161t" || buffer.substring(len - 3, len) == "itb" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00edrn"
            ) { //-írn
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 3, len) == "och" || buffer.substring(
                    len - 3,
                    len
                ) == "ost" || buffer.substring(len - 3, len) == "ovn" || buffer.substring(
                    len - 3,
                    len
                ) == "oun" || buffer.substring(len - 3, len) == "out" || buffer.substring(
                    len - 3,
                    len
                ) == "ou\u0161"
            ) { //-ouš
                buffer.delete(len - 3, len)
                return
            }
            if (buffer.substring(len - 3, len) == "u\u0161k") { //-ušk
                buffer.delete(len - 3, len)
                return
            }
            if (buffer.substring(len - 3, len) == "kyn" || buffer.substring(
                    len - 3,
                    len
                ) == "\u010dan" || buffer.substring(len - 3, len) == "k\u00e1\u0159" || buffer.substring(
                    len - 3,
                    len
                ) == "n\u00e9\u0159" || buffer.substring(len - 3, len) == "n\u00edk" || buffer.substring(
                    len - 3,
                    len
                ) == "ctv" || buffer.substring(len - 3, len) == "stv"
            ) {
                buffer.delete(len - 3, len)
                return
            }
        } //len>5
        if (len > 4) {
            if (buffer.substring(len - 2, len) == "\u00e1\u010d" || buffer.substring(
                    len - 2,
                    len
                ) == "a\u010d" || buffer.substring(len - 2, len) == "\u00e1n" || buffer.substring(
                    len - 2,
                    len
                ) == "an" || buffer.substring(len - 2, len) == "\u00e1\u0159" || buffer.substring(
                    len - 2,
                    len
                ) == "as"
            ) {
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "ec" || buffer.substring(
                    len - 2,
                    len
                ) == "en" || buffer.substring(len - 2, len) == "\u011bn" || buffer.substring(
                    len - 2,
                    len
                ) == "\u00e9\u0159"
            ) { //-éř
                buffer.delete(len - 1, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "\u00ed\u0159" || buffer.substring(
                    len - 2,
                    len
                ) == "ic" || buffer.substring(len - 2, len) == "in" || buffer.substring(
                    len - 2,
                    len
                ) == "\u00edn" || buffer.substring(len - 2, len) == "it" || buffer.substring(len - 2, len) == "iv"
            ) {
                buffer.delete(len - 1, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "ob" || buffer.substring(
                    len - 2,
                    len
                ) == "ot" || buffer.substring(len - 2, len) == "ov" || buffer.substring(len - 2, len) == "o\u0148"
            ) { //-oň
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "ul") {
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "yn") {
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "\u010dk" || buffer.substring(
                    len - 2,
                    len
                ) == "\u010dn" || buffer.substring(len - 2, len) == "dl" || buffer.substring(
                    len - 2,
                    len
                ) == "nk" || buffer.substring(len - 2, len) == "tv" || buffer.substring(
                    len - 2,
                    len
                ) == "tk" || buffer.substring(len - 2, len) == "vk"
            ) {
                buffer.delete(len - 2, len)
                return
            }
        } //len>4
        if (len > 3) {
            if (buffer[buffer.length - 1] == 'c' || buffer[buffer.length - 1] == '\u010d' || //-č
                buffer[buffer.length - 1] == 'k' || buffer[buffer.length - 1] == 'l' || buffer[buffer.length - 1] == 'n' || buffer[buffer.length - 1] == 't'
            ) {
                buffer.delete(len - 1, len)
            }
        } //len>3
    } //removeDerivational


    private fun removeAugmentative(buffer: StringBuffer) {
        val len = buffer.length
        //
        if (len > 6 && buffer.substring(len - 4, len) == "ajzn") {
            buffer.delete(len - 4, len)
            return
        }
        if (len > 5 &&
            (buffer.substring(len - 3, len) == "izn" || buffer.substring(len - 3, len) == "isk")
        ) {
            buffer.delete(len - 2, len)
            palatalise(buffer)
            return
        }
        if (len > 4 && buffer.substring(len - 2, len) == "\u0000e1k") { //-ák
            buffer.delete(len - 2, len)
            return
        }
    }

    private fun removeDiminutive(buffer: StringBuffer) {
        val len = buffer.length
        //
        if (len > 7 && buffer.substring(len - 5, len) == "ou\u0161ek") { //-oušek
            buffer.delete(len - 5, len)
            return
        }
        if (len > 6) {
            if (buffer.substring(len - 4, len) == "e\u010dek" || buffer.substring(
                    len - 4,
                    len
                ) == "\u00e9\u010dek" || buffer.substring(len - 4, len) == "i\u010dek" || buffer.substring(
                    len - 4,
                    len
                ) == "\u00ed\u010dek" || buffer.substring(len - 4, len) == "enek" || buffer.substring(
                    len - 4,
                    len
                ) == "\u00e9nek" || buffer.substring(len - 4, len) == "inek" || buffer.substring(
                    len - 4,
                    len
                ) == "\u00ednek"
            ) { //-ínek
                buffer.delete(len - 3, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 4, len) == "\u00e1\u010dek" || buffer.substring(
                    len - 4,
                    len
                ) == "a\u010dek" || buffer.substring(len - 4, len) == "o\u010dek" || buffer.substring(
                    len - 4,
                    len
                ) == "u\u010dek" || buffer.substring(len - 4, len) == "anek" || buffer.substring(
                    len - 4,
                    len
                ) == "onek" || buffer.substring(len - 4, len) == "unek" || buffer.substring(
                    len - 4,
                    len
                ) == "\u00e1nek"
            ) { //-ánek
                buffer.delete(len - 4, len)
                return
            }
        } //len>6
        if (len > 5) {
            if (buffer.substring(len - 3, len) == "e\u010dk" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00e9\u010dk" || buffer.substring(len - 3, len) == "i\u010dk" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00ed\u010dk" || buffer.substring(len - 3, len) == "enk" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00e9nk" || buffer.substring(len - 3, len) == "ink" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00ednk"
            ) { //-ínk
                buffer.delete(len - 3, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 3, len) == "\u00e1\u010dk" || buffer.substring(
                    len - 3,
                    len
                ) == "au010dk" || buffer.substring(len - 3, len) == "o\u010dk" || buffer.substring(
                    len - 3,
                    len
                ) == "u\u010dk" || buffer.substring(len - 3, len) == "ank" || buffer.substring(
                    len - 3,
                    len
                ) == "onk" || buffer.substring(len - 3, len) == "unk"
            ) {
                buffer.delete(len - 3, len)
                return
            }
            if (buffer.substring(len - 3, len) == "\u00e1tk" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00e1nk" || buffer.substring(len - 3, len) == "u\u0161k"
            ) { //-ušk
                buffer.delete(len - 3, len)
                return
            }
        } //len>5
        if (len > 4) {
            if (buffer.substring(len - 2, len) == "ek" || buffer.substring(
                    len - 2,
                    len
                ) == "\u00e9k" || buffer.substring(len - 2, len) == "\u00edk" || buffer.substring(
                    len - 2,
                    len
                ) == "ik"
            ) {
                buffer.delete(len - 1, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "\u00e1k" || buffer.substring(
                    len - 2,
                    len
                ) == "ak" || buffer.substring(len - 2, len) == "ok" || buffer.substring(len - 2, len) == "uk"
            ) {
                buffer.delete(len - 1, len)
                return
            }
        }
        if (len > 3 && buffer.substring(len - 1, len) == "k") {
            buffer.delete(len - 1, len)
            return
        }
    } //removeDiminutives


    private fun removeComparative(buffer: StringBuffer) {
        val len = buffer.length
        //
        if (len > 5 &&
            (buffer.substring(len - 3, len) == "ej\u0161" || buffer.substring(len - 3, len) == "\u011bj\u0161")
        ) { //-ějš
            buffer.delete(len - 2, len)
            palatalise(buffer)
            return
        }
    }

    private fun palatalise(buffer: StringBuffer) {
        val len = buffer.length
        if (buffer.substring(len - 2, len) == "ci" || buffer.substring(len - 2, len) == "ce" || buffer.substring(
                len - 2,
                len
            ) == "\u010di" || buffer.substring(len - 2, len) == "\u010de"
        ) { //-če
            buffer.replace(len - 2, len, "k")
            return
        }
        if (buffer.substring(len - 2, len) == "zi" || buffer.substring(len - 2, len) == "ze" || buffer.substring(
                len - 2,
                len
            ) == "\u017ei" || buffer.substring(len - 2, len) == "\u017ee"
        ) { //-že
            buffer.replace(len - 2, len, "h")
            return
        }
        if (buffer.substring(len - 3, len) == "\u010dt\u011b" || buffer.substring(
                len - 3,
                len
            ) == "\u010dti" || buffer.substring(len - 3, len) == "\u010dt\u00ed"
        ) { //-čtí
            buffer.replace(len - 3, len, "ck")
            return
        }
        if (buffer.substring(len - 2, len) == "\u0161t\u011b" || buffer.substring(
                len - 2,
                len
            ) == "\u0161ti" || buffer.substring(len - 2, len) == "\u0161t\u00ed"
        ) { //-ští
            buffer.replace(len - 2, len, "sk")
            return
        }
        buffer.delete(len - 1, len)
        return
    } //palatalise


    private fun removePossessives(buffer: StringBuffer) {
        val len = buffer.length
        if (len > 5) {
            if (buffer.substring(len - 2, len) == "ov") {
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "\u016fv") { //-ův
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "in") {
                buffer.delete(len - 1, len)
                palatalise(buffer)
                return
            }
        }
    } //removePossessives


    private fun removeCase(buffer: StringBuffer) {
        val len = buffer.length
        //
        if (len > 7 && buffer.substring(len - 5, len) == "atech") {
            buffer.delete(len - 5, len)
            return
        } //len>7
        if (len > 6) {
            if (buffer.substring(len - 4, len) == "\u011btem") { //-ětem
                buffer.delete(len - 3, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 4, len) == "at\u016fm") { //-atům
                buffer.delete(len - 4, len)
                return
            }
        }
        if (len > 5) {
            if (buffer.substring(len - 3, len) == "ech" || buffer.substring(
                    len - 3,
                    len
                ) == "ich" || buffer.substring(len - 3, len) == "\u00edch"
            ) { //-ích
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 3, len) == "\u00e9ho" || buffer.substring(
                    len - 3,
                    len
                ) == "\u011bmi" || buffer.substring(len - 3, len) == "emi" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00e9mu" || buffer.substring(len - 3, len) == "eti" || buffer.substring(
                    len - 3,
                    len
                ) == "iho" || buffer.substring(len - 3, len) == "\u00edho" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00edmi" || buffer.substring(len - 3, len) == "imu"
            ) {
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 3, len) == "\u00e1ch" || buffer.substring(
                    len - 3,
                    len
                ) == "ata" || buffer.substring(len - 3, len) == "aty" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00fdch" || buffer.substring(len - 3, len) == "ama" || buffer.substring(
                    len - 3,
                    len
                ) == "ami" || buffer.substring(len - 3, len) == "ov\u00e9" || buffer.substring(
                    len - 3,
                    len
                ) == "ovi" || buffer.substring(len - 3, len) == "\u00fdmi"
            ) { //-ými
                buffer.delete(len - 3, len)
                return
            }
        }
        if (len > 4) {
            if (buffer.substring(len - 2, len) == "em") {
                buffer.delete(len - 1, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "es" || buffer.substring(
                    len - 2,
                    len
                ) == "\u00e9m" || buffer.substring(len - 2, len) == "\u00edm"
            ) { //-ím
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "\u016fm") {
                buffer.delete(len - 2, len)
                return
            }
            if (buffer.substring(len - 2, len) == "at" || buffer.substring(
                    len - 2,
                    len
                ) == "\u00e1m" || buffer.substring(len - 2, len) == "os" || buffer.substring(
                    len - 2,
                    len
                ) == "us" || buffer.substring(len - 2, len) == "\u00fdm" || buffer.substring(
                    len - 2,
                    len
                ) == "mi" || buffer.substring(len - 2, len) == "ou"
            ) {
                buffer.delete(len - 2, len)
                return
            }
        } //len>4
        if (len > 3) {
            if (buffer.substring(len - 1, len) == "e" || buffer.substring(len - 1, len) == "i") {
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 1, len) == "\u00ed" || buffer.substring(len - 1, len) == "\u011b") { //-ě
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 1, len) == "u" || buffer.substring(
                    len - 1,
                    len
                ) == "y" || buffer.substring(len - 1, len) == "\u016f"
            ) { //-ů
                buffer.delete(len - 1, len)
                return
            }
            if (buffer.substring(len - 1, len) == "a" || buffer.substring(
                    len - 1,
                    len
                ) == "o" || buffer.substring(len - 1, len) == "\u00e1" || buffer.substring(
                    len - 1,
                    len
                ) == "\u00e9" || buffer.substring(len - 1, len) == "\u00fd"
            ) { //-ý
                buffer.delete(len - 1, len)
                return
            }
        } //len>3
    }
}