package trec

import com.beust.klaxon.*
import trec.data.Document
import trec.data.IDocument
import trec.utils.Logger
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


/**
 * @author tigi
 *
 * trec.IOUtils, existující metody neměňte.
 */
object IOUtils {

    private val jsonParser = Klaxon()

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return list of lines
     */
    fun readLines(inputStream: InputStream?): List<String> {
        requireNotNull(inputStream) { "Cannot locate stream" }
        return try {
            val result: MutableList<String> = ArrayList()
            val br = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                if (line.trim { it <= ' ' }.isNotEmpty()) {
                    result.add(line.trim { it <= ' ' })
                }
            }
            inputStream.close()
            result
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return text
     */
    private fun readFile(inputStream: InputStream?): String {
        val sb = StringBuilder()
        requireNotNull(inputStream) { "Cannot locate stream" }
        return try {
            val br = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                sb.append(line + "\n")
            }
            inputStream.close()
            sb.toString().trim { it <= ' ' }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param list lines of text to save
     */
    fun saveFile(file: File?, list: Collection<String?>) {
        var printStream: PrintStream? = null
        try {
            printStream = PrintStream(FileOutputStream(file), true, "UTF-8")
            for (text in list) {
                printStream.println(text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            printStream?.close()
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param text text to save
     */
    fun saveFile(file: File?, text: String?) {
        var printStream: PrintStream? = null
        try {
            printStream = PrintStream(FileOutputStream(file), true, "UTF-8")
            printStream.println(text)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            printStream?.close()
        }
    }

    fun readFolder(folder: File): ArrayList<IDocument>{
        val result = arrayListOf<IDocument>()
        if(!folder.exists()){
            Logger.error("readFolder -> Passed folder does not exist!")
            return result
        }
        Logger.debug("readFolder -> loading documents from ${folder.path}")
        val allFiles = folder.walk()
        val part: Int = ceil(allFiles.count().toDouble() / 10.0).toInt()
        var percentage = 0
        allFiles.forEachIndexed {
            i, file ->
            if(i % part == 0){
                Logger.info("readFolder -> $percentage% loaded!")
                percentage += 10
            }

            if(file.isFile){
                val doc = readDocument(file)

                if(doc != null){
                    doc.id = file.name
                    result.add(doc)
                }
            }
        }
        Logger.debug("readFolder -> loading done! ${result.size} docs loaded!")
        return result
    }

    private fun readDocument(file: File): IDocument? {
        return jsonParser.parse<Document>(file)
    }

    /*fun readDocument(file: File): Document{
        //todo encoding
        val doc = jsonParser.parse<Document>(file.inputStream().bufferedReader(Charset.defaultCharset()))

        return doc //as ArrayList<Document>
    }*/

}