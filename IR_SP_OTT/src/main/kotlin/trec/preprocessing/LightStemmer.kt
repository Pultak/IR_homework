package trec.preprocessing


/**
 * @author Dolamic Ljiljana  University of Neuchatel
 *
 * Czech stemmer-removes case endings form nouns and adjetives, possesive adj.
 * endings from names
 * and takes care of palatalisation
 */
object LightStemmer: IStemmer {
    /**
     * A buffer of the current word being stemmed
     */
    private val sb = StringBuffer()


    override fun stem(text: ArrayList<String>): ArrayList<String> { //
        for(i in 0 until text.size){
            text[i] = LightStemmer.stem(text[i])
        }
        return text
    }

    override fun stem(text: String): String {

        //
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
        return sb.toString()
    }

    private fun palatalise(buffer: StringBuffer) {
        val len = buffer.length
        if (buffer.substring(len - 2, len) == "ci" || buffer.substring(
                len - 2,
                len
            ) == "ce" || buffer.substring(len - 2, len) == "\u010di" || buffer.substring(len - 2, len) == "\u010de"
        ) {  //-č
            buffer.replace(len - 2, len, "k")
            return
        }
        if (buffer.substring(len - 2, len) == "zi" || buffer.substring(
                len - 2,
                len
            ) == "ze" || buffer.substring(len - 2, len) == "\u017ei" || buffer.substring(len - 2, len) == "\u017ee"
        ) {  //-že
            buffer.replace(len - 2, len, "h")
            return
        }
        if (buffer.substring(len - 3, len) == "\u010dt\u011b" || buffer.substring(
                len - 3,
                len
            ) == "\u010dti" || buffer.substring(len - 3, len) == "\u010dt\u00ed"
        ) {  //-čté
            buffer.replace(len - 3, len, "ck")
            return
        }
        if (buffer.substring(len - 2, len) == "\u0161t\u011b" || buffer.substring(
                len - 2,
                len
            ) == "\u0161ti" || buffer.substring(len - 2, len) == "\u0161t\u00ed"
        ) {  //-šté
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
        return
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
            if (buffer.substring(len - 4, len) == "at\u016fm") {  //-atům
                buffer.delete(len - 4, len)
                return
            }
        }
        if (len > 5) {
            if (buffer.substring(len - 3, len) == "ech" || buffer.substring(len - 3, len) == "ich" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00edch"
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
                ) == "\u00e9mu" || buffer.substring(len - 3, len) == "\u011bte" || buffer.substring(
                    len - 3,
                    len
                ) == "\u011bti" || buffer.substring(len - 3, len) == "iho" || buffer.substring(
                    len - 3,
                    len
                ) == "\u00edho" || buffer.substring(len - 3, len) == "\u00edmi" || buffer.substring(
                    len - 3,
                    len
                ) == "imu"
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
            ) {  //-ými
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
            ) {   //-ím
                buffer.delete(len - 2, len)
                palatalise(buffer)
                return
            }
            if (buffer.substring(len - 2, len) == "\u016fm") {  //-ům
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
            ) {  //-ů
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
            ) {   //-ý
                buffer.delete(len - 1, len)
                return
            }
        } //len>3
    }
} //CzechStemmer_1

